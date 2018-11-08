package me.joeleoli.nucleus.command.commands;

import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.command.param.Parameter;
import me.joeleoli.nucleus.player.PlayerInfo;
import me.joeleoli.nucleus.punishment.PunishmentHelper;
import me.joeleoli.nucleus.punishment.PunishmentType;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.command.CommandSender;

public class PunishmentCommands {

	@Command(names = "ban", permissionNode = "nucleus.ban", async = true)
	public static void ban(CommandSender sender,
			@Parameter(name = "target") PlayerInfo target,
			@Parameter(name = "reason", wildcard = true) String reason) {
		String flags = "-p";
		final String[] split = reason.split(" ");

		if (split[split.length - 1].startsWith("-")) {
			flags = split[split.length - 1];
			reason = reason.substring(0, reason.length() - flags.length());
		}

		final PunishmentHelper helper = new PunishmentHelper(PunishmentType.BAN, sender, target, null, reason, flags, false);

		if (helper.isTargetNotFound()) {
			sender.sendMessage(Style.formatPlayerNotFoundMessage(helper.getTargetName()));
			return;
		}

		if (helper.isBrokenProfile()) {
			sender.sendMessage(Style.formatBrokenProfileMessage(helper.getTargetName()));
			return;
		}

		if (helper.isInvalidPunishment()) {
			sender.sendMessage(helper.getTargetName() + Style.RED + " is already banned.");
			return;
		}

		helper.execute();
	}

	@Command(names = "tempban", permissionNode = "nucleus.tempban", async = true)
	public static void temporaryBan(CommandSender sender,
			@Parameter(name = "target") PlayerInfo target,
			@Parameter(name = "duration") String duration,
			@Parameter(name = "reason", wildcard = true) String reason) {
		String flags = "-p";
		final String[] split = reason.split(" ");

		if (split[split.length - 1].startsWith("-")) {
			flags = split[split.length - 1];
			reason = reason.substring(0, reason.length() - flags.length());
		}

		final PunishmentHelper helper = new PunishmentHelper(PunishmentType.TEMPBAN, sender, target, duration, reason, flags, false);

		if (helper.isTargetNotFound()) {
			sender.sendMessage(Style.formatPlayerNotFoundMessage(helper.getTargetName()));
			return;
		}

		if (helper.isBrokenProfile()) {
			sender.sendMessage(Style.formatBrokenProfileMessage(helper.getTargetName()));
			return;
		}

		if (helper.isInvalidPunishment()) {
			sender.sendMessage(helper.getTargetName() + Style.RED + " is already banned.");
			return;
		}

		if (helper.getDuration() == -1) {
			sender.sendMessage(Style.RED + "Failed to parse the given duration string.");
			sender.sendMessage(Style.RED + "Example syntax: 2m3w1d2h (2 months, 3 weeks, 1 day, and 2 hours)");
			return;
		}

		helper.execute();
	}

	@Command(names = "mute", permissionNode = "nucleus.mute", async = true)
	public static void mute(CommandSender sender,
			@Parameter(name = "target") PlayerInfo target,
			@Parameter(name = "duration") String duration,
			@Parameter(name = "reason", wildcard = true) String reason) {
		String flags = "-p";
		final String[] split = reason.split(" ");

		if (split[split.length - 1].startsWith("-")) {
			flags = split[split.length - 1];
			reason = reason.substring(0, reason.length() - flags.length());
		}

		final PunishmentHelper helper = new PunishmentHelper(PunishmentType.MUTE, sender, target, duration, reason, flags, false);

		if (helper.isTargetNotFound()) {
			sender.sendMessage(Style.formatPlayerNotFoundMessage(helper.getTargetName()));
			return;
		}

		if (helper.isBrokenProfile()) {
			sender.sendMessage(Style.formatBrokenProfileMessage(helper.getTargetName()));
			return;
		}

		if (helper.isInvalidPunishment()) {
			sender.sendMessage(helper.getTargetName() + Style.RED + " is already muted.");
			return;
		}

		if (helper.getDuration() == -1) {
			sender.sendMessage(Style.RED + "Failed to parse the given duration string.");
			sender.sendMessage(Style.RED + "Example syntax: 2m3w1d2h (2 months, 3 weeks, 1 day, and 2 hours)");
			return;
		}

		helper.execute();
	}

	@Command(names = "unban", permissionNode = "nucleus.unban", async = true)
	public static void unban(CommandSender sender,
			@Parameter(name = "target") PlayerInfo target,
			@Parameter(name = "reason", wildcard = true) String reason) {
		String flags = "-p";
		final String[] split = reason.split(" ");

		if (split[split.length - 1].startsWith("-")) {
			flags = split[split.length - 1];
			reason = reason.substring(0, reason.length() - flags.length());
		}

		final PunishmentHelper helper = new PunishmentHelper(PunishmentType.BAN, sender, target, null, reason, flags, true);

		if (helper.isTargetNotFound()) {
			sender.sendMessage(Style.formatPlayerNotFoundMessage(helper.getTargetName()));
			return;
		}

		if (helper.isBrokenProfile()) {
			sender.sendMessage(Style.formatBrokenProfileMessage(helper.getTargetName()));
			return;
		}

		if (helper.isInvalidPunishment()) {
			sender.sendMessage(Style.RESET + helper.getTargetName() + Style.RED + " isn't banned.");
			return;
		}

		helper.execute();
	}

	@Command(names = "unmute", permissionNode = "nucleus.unmute", async = true)
	public static void unmute(CommandSender sender,
			@Parameter(name = "target") PlayerInfo target,
			@Parameter(name = "reason", wildcard = true) String reason) {
		String flags = "-p";
		final String[] split = reason.split(" ");

		if (split[split.length - 1].startsWith("-")) {
			flags = split[split.length - 1];
			reason = reason.substring(0, reason.length() - flags.length());
		}

		final PunishmentHelper helper = new PunishmentHelper(PunishmentType.MUTE, sender, target, null, reason, flags, true);

		if (helper.isTargetNotFound()) {
			sender.sendMessage(Style.formatPlayerNotFoundMessage(helper.getTargetName()));
			return;
		}

		if (helper.isBrokenProfile()) {
			sender.sendMessage(Style.formatBrokenProfileMessage(helper.getTargetName()));
			return;
		}

		if (helper.isInvalidPunishment()) {
			sender.sendMessage(Style.RESET + helper.getTargetName() + Style.RED + " isn't muted.");
			return;
		}

		helper.execute();
	}

}
