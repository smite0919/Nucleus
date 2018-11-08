package me.joeleoli.nucleus.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

@Getter
@Setter
public class CancellableEvent extends BaseEvent implements Cancellable {

	private boolean cancelled;

}
