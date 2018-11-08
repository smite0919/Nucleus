package me.joeleoli.nucleus.command.commands;

import java.util.UUID;
import me.joeleoli.nucleus.NucleusAPI;
import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.command.param.Parameter;
import me.joeleoli.nucleus.log.LogQueue;
import me.joeleoli.nucleus.log.PrivateMessageLog;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.PlayerInfo;
import me.joeleoli.nucleus.player.DefinedSetting;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.uuid.UUIDCache;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SocialCommands {

	@Command(names = { "togglepm", "togglepms", "tpm" })
	public static void toggleMessages(Player player) {
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());
		final boolean toggled = !NucleusAPI.<Boolean>getSetting(player, DefinedSetting.GlobalPlayerSetting.RECEIVE_PRIVATE_MESSAGES);

		nucleusPlayer.getSettings().getSettings().put(DefinedSetting.GlobalPlayerSetting.RECEIVE_PRIVATE_MESSAGES, toggled);

		if (toggled) {
			player.sendMessage(Style.GREEN + "You enabled private messages.");
		} else {
			player.sendMessage(Style.RED + "You disabled private messages.");
		}
	}

	@Command(names = { "togglesounds", "sounds" })
	public static void toggleSounds(Player player) {
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());
		final boolean toggled = !NucleusAPI.<Boolean>getSetting(player, DefinedSetting.GlobalPlayerSetting.PLAY_MESSAGE_SOUNDS);

		nucleusPlayer.getSettings().getSettings().put(DefinedSetting.GlobalPlayerSetting.PLAY_MESSAGE_SOUNDS, toggled);

		if (toggled) {
			player.sendMessage(Style.YELLOW + "You enabled message sounds.");
		} else {
			player.sendMessage(Style.YELLOW + "You disabled message sounds.");
		}
	}

	@Command(names = { "ignore" })
	public static void ignore(Player player, @Parameter(name = "target") PlayerInfo target) {
		final UUID targetUuid = UUIDCache.getUuid(target.getName());

		if (targetUuid == null) {
			player.sendMessage(Style.formatPlayerNotFoundMessage(target.getName()));
			return;
		}

		if (targetUuid.equals(player.getUniqueId())) {
			player.sendMessage(Style.RED + "You cannot ignore yourself.");
			return;
		}

		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());
		final boolean removed = nucleusPlayer.getIgnored().remove(targetUuid);

		if (!removed) {
			nucleusPlayer.getIgnored().add(targetUuid);
		}

		player.sendMessage(
				Style.YELLOW + "You " + (!removed ? "are now" : "are no longer") + " ignoring " + Style.PINK +
				target.getName());
	}

	@Command(names = { "message", "msg", "m", "tell", "whisper" })
	public static void message(Player player, @Parameter(name = "target") Player target,
			@Parameter(name = "message", wildcard = true) String message) {
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());
		final NucleusPlayer targetData = NucleusPlayer.getByUuid(target.getUniqueId());

		if (!nucleusPlayer.getSettings().getBoolean(DefinedSetting.GlobalPlayerSetting.RECEIVE_PRIVATE_MESSAGES)) {
			player.sendMessage(Style.RED + "Your private messages are currently disabled.");
			return;
		}

		final String senderName = Style.RESET + NucleusAPI.getColoredName(player);
		final String targetName = Style.RESET + NucleusAPI.getColoredName(target);

		if (nucleusPlayer.isIgnored(target.getUniqueId())) {
			player.sendMessage(Style.RED + "You are ignoring " + targetName + Style.RED + ".");
			return;
		}

		if (targetData.isIgnored(player.getUniqueId())) {
			player.sendMessage(Style.RED + "You cannot send messages to " + targetName + Style.RED + ".");
			return;
		}

		if (!NucleusAPI.<Boolean>getSetting(target, DefinedSetting.GlobalPlayerSetting.RECEIVE_PRIVATE_MESSAGES)) {
			player.sendMessage(targetName + Style.RED + " is not accepting private messages right now.");
			return;
		}

		nucleusPlayer.setReplyTo(target.getUniqueId());
		targetData.setReplyTo(player.getUniqueId());

		final String[] messages = Style.formatPrivateMessage(senderName, targetName, message);

		player.sendMessage(messages[0]);
		target.sendMessage(messages[1]);

		if (NucleusAPI.getSetting(target, DefinedSetting.GlobalPlayerSetting.PLAY_MESSAGE_SOUNDS)) {
			target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
		}

		LogQueue.getPrivateMessageLogs().add(
				new PrivateMessageLog(
						player.getUniqueId(),
						target.getUniqueId(),
						message,
						System.currentTimeMillis()
				)
		);
	}

	@Command(names = { "reply", "r" })
	public static void reply(Player player, @Parameter(name = "message", wildcard = true) String message) {
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

		if (nucleusPlayer.getReplyTo() == null) {
			player.sendMessage(Style.RED + "You have nobody to reply to.");
			return;
		}

		final Player target = Bukkit.getPlayer(nucleusPlayer.getReplyTo());

		if (target == null || !target.isOnline()) {
			player.sendMessage(Style.RED + "That player is no longer online.");
			return;
		}

		final NucleusPlayer targetData = NucleusPlayer.getByUuid(target.getUniqueId());

		if (!nucleusPlayer.getSettings().getBoolean(DefinedSetting.GlobalPlayerSetting.RECEIVE_PRIVATE_MESSAGES)) {
			player.sendMessage(Style.RED + "Your private messages are currently disabled.");
			return;
		}

		final String senderName = Style.RESET + NucleusAPI.getColoredName(player);
		final String targetName = Style.RESET + NucleusAPI.getColoredName(target);

		if (nucleusPlayer.isIgnored(target.getUniqueId())) {
			player.sendMessage(Style.RED + "You are ignoring " + targetName + Style.RED + ".");
			return;
		}

		if (targetData.isIgnored(player.getUniqueId())) {
			player.sendMessage(Style.RED + "You cannot send messages to " + targetName + Style.RED + ".");
			return;
		}

		if (!NucleusAPI.<Boolean>getSetting(target, DefinedSetting.GlobalPlayerSetting.RECEIVE_PRIVATE_MESSAGES)) {
			player.sendMessage(targetName + Style.RED + " is not accepting private messages right now.");
			return;
		}

		nucleusPlayer.setReplyTo(target.getUniqueId());
		targetData.setReplyTo(player.getUniqueId());

		final String[] messages = Style.formatPrivateMessage(senderName, targetName, message);

		player.sendMessage(messages[0]);
		target.sendMessage(messages[1]);

		if (NucleusAPI.getSetting(target, DefinedSetting.GlobalPlayerSetting.PLAY_MESSAGE_SOUNDS)) {
			target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
		}

		LogQueue.getPrivateMessageLogs().add(
				new PrivateMessageLog(
						player.getUniqueId(),
						target.getUniqueId(),
						message,
						System.currentTimeMillis()
				)
		);
	}

}
