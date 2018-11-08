package me.joeleoli.nucleus.log;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class LogQueue {

	@Getter
	private static List<CommandLog> commandLogs = new ArrayList<>();
	@Getter
	private static List<ConnectionLog> connectionLogs = new ArrayList<>();
	@Getter
	private static List<PrivateMessageLog> privateMessageLogs = new ArrayList<>();
	@Getter
	private static List<PublicMessageLog> publicMessageLogs = new ArrayList<>();

}
