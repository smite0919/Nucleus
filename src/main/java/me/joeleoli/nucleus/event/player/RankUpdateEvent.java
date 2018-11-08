package me.joeleoli.nucleus.event.player;

import lombok.Getter;
import me.joeleoli.nucleus.event.NucleusPlayerEvent;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.rank.Rank;

@Getter
public class RankUpdateEvent extends NucleusPlayerEvent {

	private Rank from;
	private Rank to;

	public RankUpdateEvent(NucleusPlayer nucleusPlayer, Rank from, Rank to) {
		super(nucleusPlayer);

		this.from = from;
		this.to = to;
	}

}
