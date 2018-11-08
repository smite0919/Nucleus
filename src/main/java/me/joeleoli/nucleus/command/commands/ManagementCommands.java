package me.joeleoli.nucleus.command.commands;

import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.jedis.NucleusJedis;
import me.joeleoli.nucleus.jedis.handler.NucleusPayload;
import me.joeleoli.nucleus.reflection.BukkitReflection;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManagementCommands {

	@Command(names = "nucleus reload", permissionNode = "nucleus.owner")
	public static void reload(CommandSender sender) {
		Nucleus.getInstance().getNucleusConfig().load();
		sender.sendMessage(Style.GREEN + "Reloaded Nucleus configuration.");
	}

	@Command(names = "nucleus clearpunishments", permissionNode = "nucleus.owner")
	public static void clearDatabase(CommandSender sender) {
		Nucleus.getInstance().getNucleusMongo().dropPunishments();
		NucleusJedis.getInstance().write(NucleusPayload.CLEAR_PUNISHMENTS, null);
		sender.sendMessage(Style.GREEN + "Cleared punishments database.");
	}

	@Command(names = "playerdebug", permissionNode = "nucleus.debug")
	public static void debugPlayer(Player player) {
		player.sendMessage(Style.GOLD + "Your values:");

		player.sendMessage(new String[]{
				Style.GRAY + " * XYZ: " + Math.round(player.getLocation().getX()) + ", " +
				Math.round(player.getLocation().getY()) + ", " + Math.round(player.getLocation().getZ()),
				Style.GRAY + " * Health: " + player.getHealth(),
				Style.GRAY + " * Walk Speed: " + player.getWalkSpeed(),
				Style.GRAY + " * Fly Speed: " + player.getFlySpeed(),
				Style.GRAY + " * Ping: " + BukkitReflection.getPing(player)
		});
	}

}
