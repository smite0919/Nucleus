package me.joeleoli.nucleus.util;

import me.joeleoli.nucleus.Nucleus;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskUtil {

	public static void run(Runnable runnable) {
		Nucleus.getInstance().getServer().getScheduler().runTask(Nucleus.getInstance(), runnable);
	}

	public static void runTimer(Runnable runnable, long delay, long timer) {
		Nucleus.getInstance().getServer().getScheduler().runTaskTimer(Nucleus.getInstance(), runnable, delay, timer);
	}

	public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
		runnable.runTaskTimer(Nucleus.getInstance(), delay, timer);
	}

	public static void runLater(Runnable runnable, long delay) {
		Nucleus.getInstance().getServer().getScheduler().runTaskLater(Nucleus.getInstance(), runnable, delay);
	}

	public static void runAsync(Runnable runnable) {
		Nucleus.getInstance().getServer().getScheduler().runTaskAsynchronously(Nucleus.getInstance(), runnable);
	}

}
