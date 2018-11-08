package me.joeleoli.nucleus.log;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PublicMessageLog {

	private UUID sender;
	private String message;
	private Long timestamp;

}
