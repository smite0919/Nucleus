package me.joeleoli.nucleus.command.param.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.joeleoli.nucleus.command.param.ParameterType;
import me.joeleoli.nucleus.server.ServerType;
import me.joeleoli.nucleus.util.Style;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerTypeParameterType implements ParameterType<ServerType> {

	public ServerType transform(CommandSender sender, String source) {
		try {
			return ServerType.valueOf(source.toUpperCase());
		} catch (Exception e) {
			sender.sendMessage(Style.RED + "A server type with that name does not exist.");
			return null;
		}
	}

	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
		List<String> completions = new ArrayList<>();

		for (ServerType serverType : ServerType.values()) {
			if (StringUtils.startsWithIgnoreCase(serverType.name(), source)) {
				completions.add(serverType.name());
			}
		}

		return (completions);
	}

}