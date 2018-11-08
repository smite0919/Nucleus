package me.joeleoli.nucleus.task;

import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.uuid.UUIDCache;
import org.bukkit.entity.Player;

public class UpdateCacheTask implements Runnable {

	@Override
	public void run() {
		try {
			UUIDCache.fetch();
		} catch (Exception e) {
			e.printStackTrace();
		}

		NucleusPlayer.getCached().entrySet().removeIf(entry -> {
			final Player player = entry.getValue().toPlayer();

			if (!entry.getValue().isLoaded()) {
				return true;
			}

			if (player == null || !player.isOnline()) {
				return System.currentTimeMillis() - entry.getValue().getLoadedAt() >= 2500;
			}

			return false;
		});
	}

}
