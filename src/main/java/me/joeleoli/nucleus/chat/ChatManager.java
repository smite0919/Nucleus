package me.joeleoli.nucleus.chat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.chat.format.DefaultChatFormat;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.entity.Player;

@Getter
public class ChatManager {

	private static final Pattern URL_REGEX = Pattern.compile(
			"^(http://www\\.|https://www\\.|http://|https://)?[a-z0-9]+([\\-.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?$");
	private static final Pattern IP_REGEX = Pattern.compile(
			"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])([.,])){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
	private static final List<String> LINK_WHITELIST = Arrays.asList(
			"minexd.com", "eu.minexd.com", "sa.minexd.com",
			"youtube.com", "youtu.be", "discord.gg", "twitter.com",
			"prnt.sc", "gyazo.com", "imgur.com"
	);

	@Setter
	private ChatFormat chatFormat = new DefaultChatFormat();
	private int delayTime = 3;
	private boolean chatMuted = false;
	@Getter
	private List<String> filteredWords = Arrays.asList("nigger", "faggot", "queer", "paki", "slut");
	@Getter
	private List<String> filteredPhrases = Arrays.asList("adviser is fat", "adviser fat");

	public void toggleMuteChat() {
		this.chatMuted = !this.chatMuted;
	}

	public boolean shouldFilter(String message) {
		String msg = message.toLowerCase()
		                    .replace("3", "e")
		                    .replace("1", "i")
		                    .replace("!", "i")
		                    .replace("@", "a")
		                    .replace("7", "t")
		                    .replace("0", "o")
		                    .replace("5", "s")
		                    .replace("8", "b")
		                    .replaceAll("\\p{Punct}|\\d", "").trim();

		String[] words = msg.trim().split(" ");

		for (String word : words) {
			for (String filteredWord : this.filteredWords) {
				if (word.contains(filteredWord)) {
					return true;
				}
			}
		}

		for (String word : message.replace("(dot)", ".").replace("[dot]", ".").trim().split(" ")) {
			boolean continueIt = false;

			for (String phrase : ChatManager.LINK_WHITELIST) {
				if (word.toLowerCase().contains(phrase)) {
					continueIt = true;
					break;
				}
			}

			if (continueIt) {
				continue;
			}

			Matcher matcher = ChatManager.IP_REGEX.matcher(word);

			if (matcher.matches()) {
				return true;
			}

			matcher = ChatManager.URL_REGEX.matcher(word);

			if (matcher.matches()) {
				return true;
			}
		}

		Optional<String> optional = this.filteredPhrases.stream().filter(msg::contains).findFirst();

		return optional.isPresent();
	}

}
