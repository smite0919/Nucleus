package me.joeleoli.nucleus.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class PlayerEvent extends BaseEvent {

	private Player player;

}
