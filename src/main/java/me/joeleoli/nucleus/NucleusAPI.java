package me.joeleoli.nucleus;

import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.DefinedSetting;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.entity.Player;

public class NucleusAPI {

	public static String getColoredName(Player player) {
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

		if (nucleusPlayer.getActiveRank() == null) {
			return Style.RESET + player.getName();
		} else {
			if (player.hasPermission("nucleus.donor.color") && nucleusPlayer.getColor() != null) {
				return Style.RESET + nucleusPlayer.getColor().getDisplay() + player.getName();
			} else {
				return Style.RESET + nucleusPlayer.getActiveRank().getColor() + player.getName();
			}
		}
	}

	public static String getPrefixedName(Player player) {
		final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

		if (nucleusPlayer.getActiveRank() == null) {
			return Style.RESET + player.getName();
		} else {
			final String prefix = nucleusPlayer.getActiveRank().getPrefix();
			String color = null;
			String tag = null;

			if (player.hasPermission("nucleus.donor.color") && nucleusPlayer.getColor() != null) {
				color = nucleusPlayer.getColor().getDisplay();
			}

			if (player.hasPermission("nucleus.donor.tag")) {
				if (nucleusPlayer.getTag() != null) {
					if (nucleusPlayer.getTagColor() != null) {
						tag = Style.GRAY + "[" + nucleusPlayer.getTagColor().getDisplay() + nucleusPlayer.getTag().getDisplay() + Style.GRAY + "]";
					} else {
						tag = Style.GRAY + "[" + nucleusPlayer.getTag().getDisplay() + Style.GRAY + "]";
					}
				}
			}

			return (tag == null ? "" : tag + " ") + prefix + (color == null ? "" : Style.BOLD + color) + player.getName();
		}
	}

	public static boolean isFrozen(Player player) {
		return NucleusPlayer.getByUuid(player.getUniqueId()).isFrozen();
	}

	public static <T> T getSetting(Player player, DefinedSetting definedSetting) {
		return (T) NucleusPlayer.getByUuid(player.getUniqueId()).getSettings().get(definedSetting);
	}

}
