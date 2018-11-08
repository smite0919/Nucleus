package me.joeleoli.nucleus.command.param.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.joeleoli.nucleus.command.param.ParameterType;
import me.joeleoli.nucleus.rank.Rank;
import me.joeleoli.nucleus.util.Style;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankParameterType implements ParameterType<Rank> {

	public Rank transform(CommandSender sender, String source) {
		Rank rank = Rank.getRankByName(source);

		if (rank == null) {
			sender.sendMessage(Style.RED + "A rank with that name could not be found.");
			return null;
		}

		return rank;
	}

	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
		List<String> completions = new ArrayList<>();

		Rank.getRanks().forEach(rank -> {
			if (StringUtils.startsWithIgnoreCase(rank.getName(), source)) {
				completions.add(rank.getName());
			}
		});

		return (completions);
	}

}