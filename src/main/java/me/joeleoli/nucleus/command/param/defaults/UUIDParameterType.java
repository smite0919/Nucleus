package me.joeleoli.nucleus.command.param.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.command.param.ParameterType;
import me.joeleoli.nucleus.util.Style;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UUIDParameterType implements ParameterType<UUID> {

	public UUID transform(CommandSender sender, String source) {
		if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
			return (((Player) sender).getUniqueId());
		}

		try {
			return UUID.fromString(source);
		} catch (Exception e) {
			sender.sendMessage(Style.RED + "That UUID could not be parsed.");
		}

		return null;
	}

	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
		List<String> completions = new ArrayList<>();

		for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
			if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
				completions.add(player.getName());
			}
		}

		return (completions);
	}

}