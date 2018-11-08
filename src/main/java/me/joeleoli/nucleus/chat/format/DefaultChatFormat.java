package me.joeleoli.nucleus.chat.format;

import me.joeleoli.nucleus.NucleusAPI;
import me.joeleoli.nucleus.chat.ChatFormat;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.entity.Player;

public class DefaultChatFormat implements ChatFormat {

	@Override
	public String format(Player sender, Player receiver, String message) {
		return NucleusAPI.getPrefixedName(sender) + Style.WHITE + ": " + message;
	}

}
