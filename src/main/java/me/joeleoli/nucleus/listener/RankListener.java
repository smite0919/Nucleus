package me.joeleoli.nucleus.listener;

import me.joeleoli.nucleus.event.player.RankUpdateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RankListener implements Listener {

	@EventHandler
	public void onRankUpdate(RankUpdateEvent event) {
		if (event.getPlayer() == null || event.getTo() == null) {
			return;
		}

		event.getNucleusPlayer().refreshDisplayName();
	}

}
