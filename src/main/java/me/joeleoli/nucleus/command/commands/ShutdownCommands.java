package me.joeleoli.nucleus.command.commands;

import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.command.param.Parameter;
import me.joeleoli.nucleus.task.ShutdownTask;
import me.joeleoli.nucleus.util.Style;

import org.bukkit.command.CommandSender;

public class ShutdownCommands {

	@Command(names = { "shutdown", "restart" }, permissionNode = "nucleus.shutdown")
	public static void shutdown(CommandSender sender) {
		sender.sendMessage(Style.RED + "Usage: /shutdown <check|cancel|time> [seconds]");
	}

	@Command(names = "shutdown check", permissionNode = "nucleus.shutdown")
	public static void time(CommandSender sender) {
		if (Nucleus.getInstance().getShutdownTask() == null) {
			sender.sendMessage(Style.RED + "The server is not scheduled to shut down.");
		} else {
			sender.sendMessage(Style.GREEN + "The server will shutdown in " +
			                   Nucleus.getInstance().getShutdownTask().getSecondsUntilShutdown() + " seconds.");
		}
	}

	@Command(names = { "shutdown cancel", "shutdown stop" }, permissionNode = "nucleus.shutdown")
	public static void cancel(CommandSender sender) {
		if (Nucleus.getInstance().getShutdownTask() == null) {
			sender.sendMessage(Style.RED + "The server is not scheduled to shut down.");
		} else {
			Nucleus.getInstance().getShutdownTask().cancel();
			Nucleus.getInstance().setShutdownTask(null);
			sender.sendMessage(Style.RED + "The server shutdown has been canceled.");
		}
	}

	@Command(names = "shutdown time", permissionNode = "nucleus.shutdown")
	public static void time(CommandSender sender, @Parameter(name = "seconds") int seconds) {
		if (seconds <= 0) {
			sender.sendMessage(Style.RED + "You must input a number greater than 0!");
			return;
		}

		if (Nucleus.getInstance().getShutdownTask() == null) {
			Nucleus.getInstance().setShutdownTask(new ShutdownTask(Nucleus.getInstance(), seconds));
			Nucleus.getInstance().getShutdownTask().runTaskTimer(Nucleus.getInstance(), 20L, 20L);
		} else {
			Nucleus.getInstance().getShutdownTask().setSecondsUntilShutdown(seconds);
		}

		sender.sendMessage(Style.GREEN + "The server will shutdown in " + seconds + " seconds.");
	}

}
