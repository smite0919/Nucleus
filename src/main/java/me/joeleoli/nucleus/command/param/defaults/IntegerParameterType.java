package me.joeleoli.nucleus.command.param.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.joeleoli.nucleus.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IntegerParameterType implements ParameterType<Integer> {

	public Integer transform(CommandSender sender, String source) {
		try {
			return (Integer.parseInt(source));
		} catch (NumberFormatException exception) {
			sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
			return (null);
		}
	}

	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
		return (new ArrayList<>());
	}

}