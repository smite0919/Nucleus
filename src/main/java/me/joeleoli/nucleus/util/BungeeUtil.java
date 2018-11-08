package me.joeleoli.nucleus.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.joeleoli.nucleus.Nucleus;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

public class BungeeUtil {

	private BungeeUtil() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	public static void sendMessage(Player source, String target, String message) {
		Validate.notNull(source, target, message, "Input values cannot be null!");

		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Message");
			out.writeUTF(target);
			out.writeUTF(message);

			source.sendPluginMessage(Nucleus.getInstance(), "BungeeCord", out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void kickPlayer(Player source, String target, String reason) {
		Validate.notNull(source, target, reason, "Input values cannot be null!");

		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("KickPlayer");
			out.writeUTF(target);
			out.writeUTF(reason);

			source.sendPluginMessage(Nucleus.getInstance(), "BungeeCord", out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendToServer(Player player, String server) {
		Validate.notNull(player, server, "Input values cannot be null!");

		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(server);

			player.sendPluginMessage(Nucleus.getInstance(), "BungeeCord", out.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
