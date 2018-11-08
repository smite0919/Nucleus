package me.joeleoli.nucleus.log;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

@AllArgsConstructor
@Getter
public class ConnectionLog {

	private UUID uuid;
	private String ipAddress;
	private AsyncPlayerPreLoginEvent.Result result;
	private String resultReason;
	private long timestamp;

}
