package me.joeleoli.nucleus.event;

import lombok.Getter;
import me.joeleoli.nucleus.player.NucleusPlayer;

@Getter
public class NucleusPlayerEvent extends PlayerEvent {

	private final NucleusPlayer nucleusPlayer;

	public NucleusPlayerEvent(NucleusPlayer nucleusPlayer) {
		super(nucleusPlayer.toPlayer());

		this.nucleusPlayer = nucleusPlayer;
	}

}