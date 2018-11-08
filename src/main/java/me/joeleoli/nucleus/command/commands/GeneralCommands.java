package me.joeleoli.nucleus.command.commands;

import com.comphenix.protocol.PacketType;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.NucleusAPI;
import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.command.param.Parameter;
import me.joeleoli.nucleus.cooldown.Cooldown;
import me.joeleoli.nucleus.jedis.NucleusJedis;
import me.joeleoli.nucleus.jedis.handler.NucleusPayload;
import me.joeleoli.nucleus.json.JsonChain;
import me.joeleoli.nucleus.packet.Packet;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.PlayerInfo;
import me.joeleoli.nucleus.reflection.BukkitReflection;
import me.joeleoli.nucleus.util.PlayerUtil;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GeneralCommands {

	@Command(names = "help")
	public static void help(Player player) {
		player.sendMessage(new String[]{
				Style.getBorderLine(),
				Style.YELLOW + "Twitter: " + Style.PINK + "@MineXD",
				Style.YELLOW + "Discord: " + Style.PINK + "discord.gg/5aVjHM9",
				"",
				Style.YELLOW + "/report <player> <reason>" + Style.GRAY + " - " + Style.PINK + "Report a player",
				Style.YELLOW + "/request <message>" + Style.GRAY + " - " + Style.PINK + "Request assistance",
				Style.getBorderLine()
		});
	}

	@Command(names = "night")
	public static void night(Player player) {
		Packet packet = new Packet(PacketType.Play.Server.UPDATE_TIME);

		packet.getLongs()
		      .write(0, 14500L)
		      .write(1, 14500L);

		packet.send(player);

		player.sendMessage(Style.GREEN + "You set your time to night.");
	}

	@Command(names = "day")
	public static void day(Player player) {
		Packet packet = new Packet(PacketType.Play.Server.UPDATE_TIME);

		packet.getLongs()
		      .write(0, 0L)
		      .write(1, 0L);

		packet.send(player);

		player.sendMessage(Style.GREEN + "You set your time to dau.");
	}

	@Command(names = { "broadcast", "bc" }, permissionNode = "nucleus.broadcast")
	public static void broadcast(CommandSender sender, @Parameter(wildcard = true, name = "message") String broadcast) {
		String msg = broadcast.replaceAll("(&([a-f0-9l-or]))", "\u00A7$2");

		NucleusJedis.getInstance().write(
				NucleusPayload.BROADCAST_MESSAGE,
				new JsonChain()
						.addProperty("message", msg)
						.get()
		);
	}

	@Command(names = "raw", permissionNode = "nucleus.raw")
	public static void raw(CommandSender sender, @Parameter(wildcard = true, name = "message") String broadcast) {
		String msg = broadcast.replaceAll("(&([a-f0-9l-or]))", "\u00A7$2");
		Bukkit.broadcastMessage(msg);
	}

	@Command(names = "heal", permissionNode = "nucleus.heal")
	public static void heal(Player player) {
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.updateInventory();
		player.sendMessage(Style.GOLD + "You have been healed.");
	}

	@Command(names = "healother", permissionNode = "nucleus.heal")
	public static void healOther(Player player, @Parameter(name = "target") Player target) {
		target.setHealth(20.0);
		target.setFoodLevel(20);
		target.updateInventory();
		target.sendMessage(Style.GOLD + "You have been healed.");
		player.sendMessage(Style.GOLD + "You healed " + Style.RESET + target.getDisplayName() + Style.GOLD + ".");
	}

	@Command(names = "rename", permissionNode = "nucleus.rename")
	public static void rename(Player player, @Parameter(name = "name", wildcard = true) String name) {
		if (player.getItemInHand() == null) {
			player.sendMessage(Style.RED + "There is nothing in your hand.");
			return;
		}

		final ItemStack itemStack = player.getItemInHand();
		final ItemMeta itemMeta = itemStack.getItemMeta();

		itemMeta.setDisplayName(Style.translate(name));
		itemStack.setItemMeta(itemMeta);

		player.updateInventory();
		player.sendMessage(Style.GREEN + "You renamed the item in your hand.");
	}

	@Command(names = "more", permissionNode = "nucleus.more")
	public static void more(Player player) {
		if (player.getItemInHand() == null) {
			player.sendMessage(Style.RED + "There is nothing in your hand.");
			return;
		}

		player.getItemInHand().setAmount(64);
		player.updateInventory();
		player.sendMessage(Style.GREEN + "You gave yourself more of the item in your hand.");
	}

	@Command(names = "setspawn", permissionNode = "nucleus.setspawn")
	public static void setSpawn(Player player) {
		player.getLocation().getWorld()
		      .setSpawnLocation((int) player.getLocation().getX(), (int) player.getLocation().getY(),
				      (int) player.getLocation().getZ()
		      );
		player.sendMessage(Style.GREEN + "You updated the world spawn.");
	}

	@Command(names = "spawn", permissionNode = "nucleus.spawn")
	public static void spawn(Player player) {
		player.teleport(player.getLocation().getWorld().getSpawnLocation());
		player.sendMessage(Style.GREEN + "You teleported to the world spawn.");
	}

	@Command(names = "updateinv", permissionNode = "nucleus.updateinv")
	public static void updateInventory(Player player) {
		player.updateInventory();
		player.sendMessage(Style.GOLD + "Updated your inventory.");
	}

	@Command(names = { "reset" }, permissionNode = "nucleus.reset")
	public static void reset(Player player) {
		PlayerUtil.reset(player, false);
		player.sendMessage(Style.GOLD + "Your player has been reset.");
	}

	@Command(names = { "ci", "clear", "clearinv" }, permissionNode = "nucleus.clearinv")
	public static void clearInventory(Player player) {
		player.getInventory().setContents(new ItemStack[36]);
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.sendMessage(Style.GOLD + "Your inventory has been cleared.");
	}

	@Command(names = { "gamemode", "gm" }, permissionNode = "nucleus.gamemode")
	public static void gamemodeSelf(Player player, @Parameter(name = "gamemode") GameMode gameMode) {
		player.setGameMode(gameMode);
		player.updateInventory();
		player.sendMessage(Style.GOLD + "You updated your game mode to " + Style.RESET + gameMode.name());
	}

	@Command(names = { "gamemodeother", "gmother" }, permissionNode = "nucleus.gamemode.other")
	public static void gamemodeOther(CommandSender sender, @Parameter(name = "target") Player target,
			@Parameter(name = "gamemode") GameMode gameMode) {
		String senderName;

		if (sender instanceof Player) {
			final Player player = (Player) sender;

			senderName = NucleusAPI.getColoredName(player);
		} else {
			senderName = Style.DARK_RED + "Console";
		}

		target.setGameMode(gameMode);
		target.updateInventory();
		target.sendMessage(Style.GOLD + "Your game mode has been updated by " + Style.RESET + senderName);
		sender.sendMessage(
				Style.GOLD + "You updated " + NucleusAPI.getColoredName(target) + Style.GOLD + "'s game mode to " +
				Style.RESET + gameMode.name());
	}

	@Command(names = { "setslots", "setmaxslots" }, permissionNode = "nucleus.setslots")
	public static void setSlots(CommandSender sender, @Parameter(name = "slots") int slots) {
		BukkitReflection.setMaxPlayers(Nucleus.getInstance().getServer(), slots);

		sender.sendMessage(Style.GREEN + "You set the max slots to " + slots + ".");
	}

	@Command(names = "ping")
	public static void ping(Player player, @Parameter(name = "target", defaultValue = "self") PlayerInfo targetInfo) {
		final Player target = targetInfo.toPlayer();

		if (target == null) {
			player.sendMessage(Style.RED + "That player could not be found.");
			return;
		}

		int ping = BukkitReflection.getPing(target);

		player.sendMessage(target.getDisplayName() + Style.YELLOW + "'s ping: " +
		                   (ping > 100 ? Style.RED : (ping > 50 ? Style.YELLOW : Style.GREEN)) + ping);
	}

	@Command(names = "showplayer", permissionNode = "nucleus.util.showplayer")
	public static void showPlayer(Player player, @Parameter(name = "target") Player target) {
		player.showPlayer(target);
	}

	@Command(names = "hidePlayer", permissionNode = "nucleus.util.hideplayer")
	public static void hidePlayer(Player player, @Parameter(name = "target") Player target) {
		player.hidePlayer(target);
	}

	@Command(names = "report")
	public static void reportPlayer(Player player, @Parameter(name = "target") Player target,
			@Parameter(name = "reason", wildcard = true) String reason) {
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

		if (Nucleus.getInstance().getChatManager().shouldFilter(reason)) {
			player.sendMessage(Style.RED + "If you continue to mis-use the report feature you will be banned.");
			return;
		}

		if (!nucleusPlayer.getReportCooldown().hasExpired()) {
			player.sendMessage(Style.RED + "You must wait before you can report another player.");
			return;
		}

		player.sendMessage(Style.GREEN + "Thanks, we will review your report.");

		nucleusPlayer.setReportCooldown(new Cooldown(60_000));

		NucleusJedis.getInstance().write(
				NucleusPayload.PLAYER_REPORT,
				new JsonChain()
						.addProperty("server", Nucleus.getInstance().getNucleusConfig().getServerId())
						.addProperty("sender_name", NucleusAPI.getColoredName(player))
						.addProperty("reported_name", NucleusAPI.getColoredName(target))
						.addProperty("report_reason", reason)
						.get()
		);
	}

	@Command(names = { "request", "helpop" })
	public static void reportPlayer(Player player, @Parameter(name = "reason", wildcard = true) String reason) {
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

		if (Nucleus.getInstance().getChatManager().shouldFilter(reason)) {
			player.sendMessage(Style.RED + "If you continue to mis-use the request feature you will be banned.");
			return;
		}

		if (!nucleusPlayer.getRequestCooldown().hasExpired()) {
			player.sendMessage(Style.RED + "You must wait before you can request assistance.");
			return;
		}

		player.sendMessage(Style.GREEN + "We have received your request and will help soon. Please be patient.");

		nucleusPlayer.setRequestCooldown(new Cooldown(60_000));

		NucleusJedis.getInstance().write(
				NucleusPayload.PLAYER_REQUEST,
				new JsonChain()
						.addProperty("server", Nucleus.getInstance().getNucleusConfig().getServerId())
						.addProperty("sender_name", NucleusAPI.getColoredName(player))
						.addProperty("request_reason", reason)
						.get()
		);
	}

}
