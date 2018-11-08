package me.joeleoli.nucleus.command.param.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.joeleoli.nucleus.command.param.ParameterType;
import me.joeleoli.nucleus.player.PlayerInfo;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerInfoParameterType implements ParameterType<PlayerInfo> {

	@Override
	public PlayerInfo transform(CommandSender sender, String source) {
		final Player player = source.equalsIgnoreCase("self") && sender instanceof Player ? (Player) sender
				: Bukkit.getPlayer(source);

		if (player != null) {
			return new PlayerInfo(player.getUniqueId(), player.getName());
		} else {
			return new PlayerInfo(null, source);
		}
	}

	@Override
	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
		List<String> completions = new ArrayList<>();

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
				completions.add(player.getName());
			}
		}

		return completions;
	}

}
