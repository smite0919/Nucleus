package me.joeleoli.nucleus.punishment;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SharedBan {

	private Punishment punishment;
	private String altName;
	private UUID altUuid;

}
