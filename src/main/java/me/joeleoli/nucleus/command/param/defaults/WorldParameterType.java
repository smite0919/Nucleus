package me.joeleoli.nucleus.command.param.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldParameterType implements ParameterType<World> {

	public World transform(CommandSender sender, String source) {
		World world = Nucleus.getInstance().getServer().getWorld(source);

		if (world == null) {
			sender.sendMessage(ChatColor.RED + "No world with the name " + source + " found.");
			return (null);
		}

		return (world);
	}

	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
		List<String> completions = new ArrayList<>();

		for (World world : Nucleus.getInstance().getServer().getWorlds()) {
			if (StringUtils.startsWithIgnoreCase(world.getName(), source)) {
				completions.add(world.getName());
			}
		}

		return (completions);
	}

}