package me.joeleoli.nucleus.listener;

import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.event.PreShutdownEvent;
import me.joeleoli.nucleus.player.NucleusPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener implements Listener {

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		event.setMotd(Nucleus.getInstance().getNucleusConfig().getServerMotd());
	}

	@EventHandler
	public void onPreShutdown(PreShutdownEvent event) {
		Nucleus.getInstance().getServer().getScheduler().runTaskAsynchronously(Nucleus.getInstance(), () -> {
			for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
				NucleusPlayer.getByUuid(player.getUniqueId()).save();
			}
		});

		Nucleus.getInstance().getNucleusMongo().close();
	}

	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		String command = event.getCommand().replace("/", "");

		if (command == null) {
			return;
		}

		if (command.split(" ")[0].equalsIgnoreCase("stop")) {
			PreShutdownEvent shutdownEvent = new PreShutdownEvent();

			shutdownEvent.call();

			if (shutdownEvent.isCancelled()) {
				return;
			}

			this.handleShutdown();
		}
	}

	private void handleShutdown() {
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
