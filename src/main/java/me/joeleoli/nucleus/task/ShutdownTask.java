package me.joeleoli.nucleus.task;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.event.PreShutdownEvent;
import me.joeleoli.nucleus.util.BungeeUtil;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter
@AllArgsConstructor
public class ShutdownTask extends BukkitRunnable {

	private final static List<Integer> BROADCAST_TIMES =
			Arrays.asList(3600, 1800, 900, 600, 300, 180, 120, 60, 45, 30, 15, 10, 5, 4, 3, 2, 1);

	private Nucleus plugin;
	private int secondsUntilShutdown;

	@Override
	public void run() {
		if (BROADCAST_TIMES.contains(secondsUntilShutdown)) {
			this.plugin.getServer().broadcastMessage(
					Style.GOLD + "[MXD] " + Style.YELLOW + "The server will shutdown in " + Style.PINK +
					secondsUntilShutdown + Style.YELLOW + " seconds.");
		}

		if (this.secondsUntilShutdown <= 5) {
			this.plugin.getServer().getOnlinePlayers().forEach(player -> BungeeUtil.sendToServer(player, "hub"));
		}

		if (this.secondsUntilShutdown <= 0) {
			PreShutdownEvent event = new PreShutdownEvent();
			this.plugin.getServer().getPluginManager().callEvent(event);

			if (event.isCancelled()) {
				return;
			}

			this.plugin.getServer().getOnlinePlayers()
			           .forEach(player -> player.sendMessage(Style.PINK + "The server has shut down."));
			this.plugin.getServer().shutdown();
		}

		this.secondsUntilShutdown--;
	}

}