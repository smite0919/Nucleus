package me.joeleoli.nucleus.log;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PrivateMessageLog {

	private UUID sender;
	private UUID receiver;
	private String message;
	private Long timestamp;

}
