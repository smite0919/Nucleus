package me.joeleoli.nucleus.command.commands;

import java.util.Comparator;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.command.CommandHelp;
import me.joeleoli.nucleus.command.param.Parameter;
import me.joeleoli.nucleus.jedis.handler.NucleusPayload;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.PlayerInfo;
import me.joeleoli.nucleus.rank.Rank;
import me.joeleoli.nucleus.server.ServerType;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommands {

	private static final CommandHelp[] HELP = new CommandHelp[]{
			new CommandHelp("/rank create <global/server> <name> [server]", "Create a rank"),
			new CommandHelp("/rank delete <name>", "Delete a rank"),
			new CommandHelp("/rank setprefix <name> <prefix>", "Set prefix of a rank"),
			new CommandHelp("/rank setcolor <name> <color>", "Set colo of a rank"),
			new CommandHelp("/rank setweight <name> <number>", "Set the weight of a rank"),
			new CommandHelp("/rank addperm <name> <perm>", "Add a permission to a rank"),
			new CommandHelp("/rank delperm <name> <perm>", "Delete a permission from a rank"),
			new CommandHelp("/rank dump <name>", "Dump a rank's permissions"),
			new CommandHelp("/rank inherit <parent> <child>", "Inherit a child rank"),
			new CommandHelp("/setrank <player> <rank>", "Set a player's rank")
	};

	@Command(
			names = { "rank", "rank help" },
			permissionNode = "nucleus.ranks"
	)
	public static void help(Player player) {
		for (CommandHelp help : HELP) {
			player.sendMessage(Style.translate("&e" + help.getSyntax() + " &7- &d" + help.getDescription()));
		}
	}

	@Command(
			names = "rank list",
			permissionNode = "nucleus.ranks"
	)
	public static void list(CommandSender sender) {
		sender.sendMessage(Style.GOLD + "Ranks:");

		Rank.getRanks().sort(Comparator.comparingInt(Rank::getWeight));

		for (Rank rank : Rank.getRanks()) {
			sender.sendMessage(Style.translate("&7 - &r" + rank.getRankInfo()));
		}
	}

	@Command(
			names = "rank create global",
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void createGlobal(Player player,
			@Parameter(name = "rankName") String rankName) {
		if (Rank.getRankByName(rankName) != null) {
			player.sendMessage(Style.RED + "A rank with that name already exists.");
			return;
		}

		Rank rank = new Rank(rankName);

		rank.setGlobal(true);
		rank.save();

		player.sendMessage(Style.translate("&aYou created a new global rank: &r" + rank.getColoredName()));
	}

	@Command(
			names = "rank create server",
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void createServer(Player player,
			@Parameter(name = "rankName") String rankName,
			@Parameter(name = "serverType") ServerType serverType) {
		if (Rank.getRankByName(rankName) != null) {
			player.sendMessage(Style.RED + "A rank with that name already exists.");
			return;
		}

		Rank rank = new Rank(rankName);

		rank.setServerType(serverType);
		rank.save();

		player.sendMessage(Style.GREEN + "Created new server rank: " + rank.getColoredName());
	}

	@Command(
			names = { "rank delete", "rank remove" },
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void delete(CommandSender sender,
			@Parameter(name = "rank") Rank rank) {
		rank.delete(true);

		sender.sendMessage(Style.translate("&aYou deleted &r" + rank.getColoredName()));
	}

	@Command(
			names = "rank setprefix",
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void setPrefix(CommandSender sender,
			@Parameter(name = "rank") Rank rank,
			@Parameter(name = "prefix", wildcard = true) String prefix) {
		rank.setPrefix(Style.translate(prefix));
		rank.save();

		sender.sendMessage(Style.translate(
				"&aYou updated the prefix of &r" + rank.getColoredName() + " &ato: &r" + rank.getPrefix() +
				sender.getName()));
	}

	@Command(
			names = "rank setcolor",
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void setColor(CommandSender sender,
			@Parameter(name = "rank") Rank rank,
			@Parameter(name = "color") String color) {
		if (!Style.strip(color).equalsIgnoreCase("")) {
			sender.sendMessage(Style.RED + "A rank's color must contain only valid color codes.");
			sender.sendMessage(Style.RED + "Example: &6&l");
			return;
		}

		final String oldColoredName = rank.getColoredName();

		rank.setColor(Style.translate(color));
		rank.save();

		sender.sendMessage(Style.translate("&aUpdated color of " + oldColoredName + " &ato: " + rank.getColoredName()));
	}

	@Command(
			names = "rank setweight",
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void setWeight(CommandSender sender,
			@Parameter(name = "rank") Rank rank,
			@Parameter(name = "weight") int weight) {
		rank.setWeight(weight);
		rank.save();

		sender.sendMessage(
				Style.translate("&aUpdated weight of &r" + rank.getColoredName() + " &ato: &r" + rank.getWeight()));
	}

	@Command(
			names = { "rank addperm", "rank addpermission" },
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void addPermission(CommandSender sender,
			@Parameter(name = "rank") Rank rank,
			@Parameter(name = "permission") String permission) {
		if (rank.getPermissions().contains(permission)) {
			sender.sendMessage(rank.getColoredName() + Style.RED + " already had the `" + permission + "` permission.");
			return;
		}

		rank.getPermissions().add(permission);
		rank.save();

		sender.sendMessage(Style.translate(
				"&aYou added `" + permission + "` to &r" + rank.getColoredName() + "&a's permissions."));
	}

	@Command(
			names = { "rank delperm", "rank deleteperm" },
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void deletePermission(CommandSender sender,
			@Parameter(name = "rank") Rank rank,
			@Parameter(name = "permission") String permission) {
		if (!rank.getPermissions().remove(permission)) {
			sender.sendMessage(
					rank.getColoredName() + Style.RED + " did not have the `" + permission + "` permission.");
			return;
		}

		rank.save();

		sender.sendMessage(Style.translate(
				"&aYou removed `" + permission + "` from &r" + rank.getColoredName() + "&a's permissions."));
	}

	@Command(
			names = "rank inherit",
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void inherit(CommandSender sender,
			@Parameter(name = "rank") Rank rank,
			@Parameter(name = "otherRank") Rank otherRank) {
		boolean removed = false;

		if (rank.getInherits().remove(otherRank.getName())) {
			removed = true;
		} else {
			rank.getInherits().add(otherRank.getName());
		}

		sender.sendMessage(Style.translate(
				"&aYou made &r" + rank.getColoredName() + " &a" + (removed ? "no longer " : "") + "inherit &r" +
				otherRank.getColoredName()));

		rank.save();
	}

	@Command(
			names = "rank dump",
			permissionNode = "nucleus.ranks",
			async = true
	)
	public static void dump(CommandSender sender,
			@Parameter(name = "rank") Rank rank) {
		sender.sendMessage(rank.getColoredName() + "'s Permissions:");

		for (String permission : rank.getEffectivePermissions()) {
			sender.sendMessage(Style.GRAY + " * " + permission);
		}

		sender.sendMessage(rank.getColoredName() + " Inherits:");

		for (String inherit : rank.getInherits()) {
			Rank otherRank = Rank.getRankByName(inherit);

			if (otherRank == null) {
				sender.sendMessage(Style.GRAY + " - " + Style.RESET + inherit);
			} else {
				sender.sendMessage(Style.GRAY + " - " + Style.RESET + otherRank.getColoredName());
			}
		}
	}

	@Command(
			names = "setrank",
			permissionNode = "nucleus.setrank",
			async = true
	)
	public static void setRank(CommandSender sender,
			@Parameter(name = "target") PlayerInfo playerInfo,
			@Parameter(name = "rank") Rank rank) {
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
			targetName = Style.RESET + targetData.getActiveRank().getColor() + targetData.getName();
		}

		targetData.setRank(rank);
		targetData.save();

		sender.sendMessage(Style.GREEN + "You updated " + Style.RESET + targetName + Style.GREEN + "'s " +
		                   (rank.isGlobal() ? "global" : rank.getServerType().name() + " server") + " rank to: " +
		                   rank.getColoredName());

		Nucleus.getInstance().getNucleusJedis().write(
				NucleusPayload.PLAYER_UPDATE_RANK,
				new me.joeleoli.nucleus.json.JsonChain()
						.addProperty("player_uuid", targetData.getUuid().toString())
						.addProperty("rank_name", rank.getName())
						.get()
		);
	}

}
