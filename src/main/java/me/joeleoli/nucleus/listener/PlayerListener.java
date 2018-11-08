package me.joeleoli.nucleus.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.NucleusAPI;
import me.joeleoli.nucleus.board.Board;
import me.joeleoli.nucleus.cooldown.Cooldown;
import me.joeleoli.nucleus.jedis.handler.NucleusPayload;
import me.joeleoli.nucleus.json.JsonChain;
import me.joeleoli.nucleus.log.CommandLog;
import me.joeleoli.nucleus.log.ConnectionLog;
import me.joeleoli.nucleus.log.LogQueue;
import me.joeleoli.nucleus.log.PublicMessageLog;
import me.joeleoli.nucleus.nametag.NameTagHandler;
import me.joeleoli.nucleus.packet.Packet;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.DefinedSetting;
import me.joeleoli.nucleus.punishment.Punishment;
import me.joeleoli.nucleus.punishment.SharedBan;
import me.joeleoli.nucleus.rank.Rank;
import me.joeleoli.nucleus.util.PlayerUtil;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.uuid.UUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommandProcess(PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		final String lowercase = event.getMessage().toLowerCase();

		if (lowercase.startsWith("//calc") ||
		    lowercase.startsWith("//eval") ||
		    lowercase.startsWith("//solve") ||
		    lowercase.startsWith("/bukkit:") ||
		    lowercase.startsWith("/me") ||
		    lowercase.startsWith("/bukkit:me") ||
		    lowercase.startsWith("/minecraft:") ||
		    lowercase.startsWith("/minecraft:me")) {
			player.sendMessage(Style.RED + "You cannot perform this command.");
			event.setCancelled(true);
			return;
		}

		if (NucleusPlayer.getByUuid(event.getPlayer().getUniqueId()).isFrozen()) {
			if (!(lowercase.startsWith("/msg") || lowercase.startsWith("/r") || lowercase.startsWith("/reply"))) {
				player.sendMessage(Style.RED + "You cannot use commands while frozen.");
				event.setCancelled(true);
				return;
			}
		}

		LogQueue.getCommandLogs().add(new CommandLog(
				player.getUniqueId(),
				event.getMessage(),
				System.currentTimeMillis()
		));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (!Nucleus.getInstance().isLoaded()) {
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			event.setKickMessage(Style.RED + "The server is starting...");
			return;
		}

		try {
			final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(event.getUniqueId());

			nucleusPlayer.setName(event.getName());
			nucleusPlayer.setSessionLoginTimestamp(System.currentTimeMillis());
			nucleusPlayer.setIpAddress(event.getAddress().getHostAddress());

			if (!nucleusPlayer.isLoaded()) {
				nucleusPlayer.load();
			}

			if (!nucleusPlayer.isLoaded()) {
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
				event.setKickMessage(Style.API_FAILED);
				return;
			}

			nucleusPlayer.searchForAlts();

			Punishment activeBan = nucleusPlayer.getActiveBan();

			if (activeBan != null) {
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
				event.setKickMessage(activeBan.getType().getMessage().replace("%EXPIRATION%", activeBan.getTimeLeft()));
				return;
			}

			SharedBan sharedBan = nucleusPlayer.getSharedBan();

			if (sharedBan == null || !sharedBan.getPunishment().isActive()) {
				nucleusPlayer.findSharedBan();
			}

			if (sharedBan != null) {
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
				event.setKickMessage(sharedBan.getPunishment().getType().getSharedMessage()
				                              .replace("%PLAYER%", sharedBan.getAltName())
				                              .replace("%EXPIRATION%", sharedBan.getPunishment().getTimeLeft())
				);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			event.setKickMessage(Style.API_FAILED);
			return;
		}

		if (Nucleus.getInstance().getNucleusJedis().isActive()) {
			UUIDCache.update(event.getName(), event.getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerPreLoginHighest(AsyncPlayerPreLoginEvent event) {
		LogQueue.getConnectionLogs()
		        .add(new ConnectionLog(event.getUniqueId(), event.getAddress().getHostAddress(), event.getLoginResult(),
				        event.getKickMessage(), System.currentTimeMillis()
		        ));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		final Player player = event.getPlayer();
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());
		final PermissionAttachment attachment = player.addAttachment(Nucleus.getInstance());

		if (Nucleus.getInstance().getBoardManager() != null) {
			Nucleus.getInstance().getBoardManager().getPlayerBoards().put(
					player.getUniqueId(),
					new Board(Nucleus.getInstance(), player, Nucleus.getInstance().getBoardManager().getAdapter())
			);
		}

		if (nucleusPlayer.getActiveRank() == null) {
			System.out.println("DEBUG: ACTIVE RANK NULL");
			nucleusPlayer.setRank(Rank.getDefaultRank());
		}

		nucleusPlayer.getActiveRank().getEffectivePermissions().forEach(permission -> {
			attachment.setPermission(permission, true);
		});

		player.recalculatePermissions();

		nucleusPlayer.refreshDisplayName();

		if (player.hasPermission("nucleus.staff")) {
			Nucleus.getInstance().getNucleusJedis().write(
					NucleusPayload.STAFF_JOIN,
					new JsonChain()
							.addProperty("server", Nucleus.getInstance().getNucleusConfig().getServerId())
							.addProperty("player_name", player.getDisplayName())
							.get()
			);
		}

		NameTagHandler.setup(event.getPlayer());

		final String json =
				"{\"text\":\"\",\"extra\":[{\"text\":\"\\u00a70\\u00a70\\u00a71\\u00a72\\u00a73\\u00a74\\u00a75\\u00a76\\u00a77\\u00a7e\\u00a7f\"}]}";
		final Packet packet = new Packet(PacketType.Play.Server.CHAT);

		packet.getChatComponents().write(0, WrappedChatComponent.fromJson(json));
		packet.send(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		if (Nucleus.getInstance().getBoardManager() != null) {
			Nucleus.getInstance().getBoardManager().getPlayerBoards().remove(event.getPlayer().getUniqueId());
		}

		final NucleusPlayer nucleusPlayer = NucleusPlayer.getCached().remove(event.getPlayer().getUniqueId());

		if (nucleusPlayer != null) {
			nucleusPlayer.save();

			if (nucleusPlayer.isFrozen()) {
				PlayerUtil.messageStaff(Style.GOLD + Style.BOLD + Style.UNICODE_CAUTION + " " +
				                        Style.PINK + nucleusPlayer.getName() + Style.YELLOW +
				                        " disconnected while frozen.");
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(event.getEntity().getUniqueId());

			if (nucleusPlayer.isFrozen()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);

		final Player player = event.getPlayer();

		if (Nucleus.getInstance().getChatManager().isChatMuted() && !player.hasPermission("nucleus.staff")) {
			player.sendMessage(Style.RED + "Public chat is currently muted.");
			return;
		}

		if (!NucleusAPI.<Boolean>getSetting(player, DefinedSetting.GlobalPlayerSetting.RECEIVE_GLOBAL_MESSAGES)) {
			player.sendMessage(Style.RED + "You can't chat while you have globla chat disabled.");
			return;
		}

		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());
		final String message = event.getMessage();
		final Punishment mute = nucleusPlayer.getActiveMute();

		if (mute != null) {
			if (mute.isPermanent()) {
				player.sendMessage(Style.RED + "You are permanently muted.");
			} else {
				player.sendMessage(Style.RED + "You are currently muted for another " + mute.getTimeLeft() + ".");
			}

			return;
		}

		if (!player.hasPermission("nucleus.chatdelay.bypass")) {
			if (!nucleusPlayer.getChatCooldown().hasExpired()) {
				player.sendMessage(Style.RED + "You can chat again in " + Style.BOLD +
				                   nucleusPlayer.getChatCooldown().getTimeLeft() + "s" + Style.RED + ".");
				return;
			} else {
				nucleusPlayer
						.setChatCooldown(new Cooldown(Nucleus.getInstance().getChatManager().getDelayTime() * 1000));
			}
		}

		if (!player.hasPermission("nucleus.chatfilter.bypass")) {
			if (Nucleus.getInstance().getChatManager().shouldFilter(event.getMessage())) {
				player.sendMessage(Style.RED + "Your message was filtered.");
				PlayerUtil.messageStaff(Style.formatFilteredPublicMessage(player, event.getMessage()));
				return;
			}
		}

		for (Player receiver : Bukkit.getOnlinePlayers()) {
			final NucleusPlayer receiverData = NucleusPlayer.getByUuid(receiver.getUniqueId());

			if (receiverData.getSettings().getBoolean(DefinedSetting.GlobalPlayerSetting.RECEIVE_GLOBAL_MESSAGES) &&
			    !receiverData.isIgnored(player.getUniqueId())) {
				receiver.sendMessage(
						Nucleus.getInstance().getChatManager().getChatFormat()
						       .format(event.getPlayer(), receiver, message));
			}
		}

		LogQueue.getPublicMessageLogs().add(
				new PublicMessageLog(
						player.getUniqueId(),
						message,
						System.currentTimeMillis()
				)
		);
	}

}
