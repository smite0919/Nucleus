package me.joeleoli.nucleus.command.commands;

import java.util.ArrayList;
import java.util.List;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.NucleusAPI;
import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.command.param.Parameter;
import me.joeleoli.nucleus.jedis.handler.NucleusPayload;
import me.joeleoli.nucleus.json.JsonChain;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.PlayerInfo;
import me.joeleoli.nucleus.punishment.gui.PunishmentHistoryMenu;
import me.joeleoli.nucleus.util.PlayerUtil;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.uuid.UUIDCache;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommands {

	@Command(names = { "freeze", "ss" }, permissionNode = "nucleus.freeze")
	public static void freeze(CommandSender sender, @Parameter(name = "target") Player target) {
		final NucleusPlayer targetData = NucleusPlayer.getByUuid(target.getUniqueId());
		final boolean isFrozen = !targetData.isFrozen();

		if (isFrozen) {
			target.sendMessage(new String[]{
					Style.BLANK_LINE,
					Style.YELLOW + Style.center("You have been frozen by a staff member."),
					Style.PINK + Style.center("Please join our TeamSpeak: ts.lotuspvp.xyz"),
					Style.PINK + Style.center("You have 5 minutes. Disconnecting will result in a ban."),
					Style.BLANK_LINE,
			});

			PlayerUtil.denyMovement(target);
		} else {
			target.sendMessage(Style.YELLOW + "You are no longer frozen.");

			PlayerUtil.allowMovement(target);
		}

		targetData.setFrozen(!targetData.isFrozen());

		PlayerUtil.messageStaff(
				Style.formatFreezeMessage(sender.getName(), target.getName(), isFrozen ? "frozen" : "unfrozen"));
	}

	@Command(names = { "mutechat", "silencechat" }, permissionNode = "nucleus.mutechat")
	public static void muteChat(CommandSender sender) {
		Nucleus.getInstance().getChatManager().toggleMuteChat();

		final String message = Style.formatMuteChatMessage(
				sender.getName(),
				Nucleus.getInstance().getChatManager().isChatMuted() ? "muted" : "unmuted"
		);

		for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
			player.sendMessage(message);
		}
	}

	@Command(names = { "staffchat", "sc" }, permissionNode = "nucleus.staff", async = true)
	public static void staffChat(Player player, @Parameter(name = "message", wildcard = true) String message) {
		Nucleus.getInstance().getNucleusJedis().write(
				NucleusPayload.STAFF_CHAT,
				new JsonChain()
						.addProperty("server", Nucleus.getInstance().getNucleusConfig().getServerId())
						.addProperty("player_name", NucleusAPI.getColoredName(player))
						.addProperty("message", message)
						.get()
		);
	}

	@Command(names = { "punishments", "history" }, permissionNode = "nucleus.history", async = true)
	public static void history(Player player, @Parameter(name = "target") PlayerInfo playerInfo) {
		final NucleusPlayer targetData = NucleusPlayer.getByName(playerInfo.getName());

		if (targetData == null) {
			player.sendMessage(Style.formatPlayerNotFoundMessage(playerInfo.getName()));
			return;
		}

		if (!targetData.isLoaded()) {
			targetData.load();
		}

		new PunishmentHistoryMenu(targetData).openMenu(player);
	}

	@Command(names = { "alts" }, permissionNode = "nucleus.staff", async = true)
	public static void alternates(CommandSender sender, @Parameter(name = "target") PlayerInfo playerInfo) {
		final NucleusPlayer targetData = NucleusPlayer.getByName(playerInfo.getName());

		if (targetData == null) {
			sender.sendMessage(Style.formatPlayerNotFoundMessage(playerInfo.getName()));
			return;
		}

		if (!targetData.isLoaded()) {
			targetData.load();
		}

		String targetName = playerInfo.getName();

		if (targetData.getName() != null && playerInfo.getName().equalsIgnoreCase(targetData.getName())) {
			targetName = targetData.getName();
		}

		if (targetData.getAlternates().isEmpty()) {
			sender.sendMessage(Style.PINK + targetName + Style.YELLOW + " does not have any alts.");
			return;
		}

		final List<String> names = new ArrayList<>();

		targetData.getAlternates().forEach(uuid -> {
			String name = UUIDCache.getName(uuid);

			if (name != null) {
				names.add(name);
			}
		});

		sender.sendMessage(Style.YELLOW + "Alts of " + Style.PINK + targetName);
		names.forEach(sender::sendMessage);
	}

	@Command(names = { "clearchat", "cc" }, permissionNode = "nucleus.clearchat", async = true)
	public static void clear(CommandSender sender) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < 100; i++) {
			builder.append("§a §b §c §d §e §f §0 §r \n");
		}

		final String clear = builder.toString();
		final String broadcast = Style.formatClearChatMessage(sender.getName());

		for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
			if (!player.hasPermission("nucleus.staff")) {
				player.sendMessage(clear);
			}

			player.sendMessage(broadcast);
		}
	}

}
