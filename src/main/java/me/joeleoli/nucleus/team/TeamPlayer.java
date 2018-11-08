package me.joeleoli.nucleus.team;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.nucleus.player.PlayerInfo;

@Getter
public class TeamPlayer extends PlayerInfo {

	@Setter
	private boolean alive;

	public TeamPlayer(UUID uuid, String name) {
		super(uuid, name);
	}

}
