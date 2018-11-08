package me.joeleoli.nucleus.rave;

import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.chat.ChatFormat;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.entity.Player;

public class RaveChatFormat implements ChatFormat {

	@Override
	public String format(Player sender, Player receiver, String message) {
		StringBuilder nameBuilder = new StringBuilder();

		for (char c : sender.getName().toCharArray()) {
			nameBuilder.append(Nucleus.getInstance().getRave().getRandomChatColor());
			nameBuilder.append(c);
		}

		return nameBuilder.toString() + Style.RESET + ": " + message;
	}

}
