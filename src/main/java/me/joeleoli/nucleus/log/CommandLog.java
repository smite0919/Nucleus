package me.joeleoli.nucleus.log;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommandLog {

	private UUID uuid;
	private String command;
	private long timestamp;

}
