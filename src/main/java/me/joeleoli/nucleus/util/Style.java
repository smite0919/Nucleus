package me.joeleoli.nucleus.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import me.joeleoli.nucleus.NucleusAPI;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class Style {

	public static final String API_FAILED =
			ChatColor.RED.toString() + "The API failed to retrieve your information. Try again later.";
	public static final String BLUE = ChatColor.BLUE.toString();
	public static final String AQUA = ChatColor.AQUA.toString();
	public static final String YELLOW = ChatColor.YELLOW.toString();
	public static final String RED = ChatColor.RED.toString();
	public static final String GRAY = ChatColor.GRAY.toString();
	public static final String GOLD = ChatColor.GOLD.toString();
	public static final String GREEN = ChatColor.GREEN.toString();
	public static final String WHITE = ChatColor.WHITE.toString();
	public static final String BLACK = ChatColor.BLACK.toString();
	public static final String BOLD = ChatColor.BOLD.toString();
	public static final String ITALIC = ChatColor.ITALIC.toString();
	public static final String UNDER_LINE = ChatColor.UNDERLINE.toString();
	public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
	public static final String RESET = ChatColor.RESET.toString();
	public static final String MAGIC = ChatColor.MAGIC.toString();
	public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
	public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
	public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
	public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
	public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
	public static final String DARK_RED = ChatColor.DARK_RED.toString();
	public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
	public static final String BLANK_LINE = "§a §b §c §d §e §f §0 §r";
	public static final String BORDER_LINE_SCOREBOARD = Style.GRAY + Style.STRIKE_THROUGH + "----------------------";
	public static final String UNICODE_VERTICAL_BAR = Style.GRAY + StringEscapeUtils.unescapeJava("\u2503");
	public static final String UNICODE_CAUTION = StringEscapeUtils.unescapeJava("\u26a0");
	public static final String UNICODE_ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25C0");
	public static final String UNICODE_ARROW_RIGHT = StringEscapeUtils.unescapeJava("\u25B6");
	public static final String UNICODE_ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
	public static final String UNICODE_ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");
	public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764");
	private static final FontRenderer FONT_RENDERER = new FontRenderer();
	private static final String MAX_LENGTH = "11111111111111111111111111111111111111111111111111111";
	public static final String SERVER_NAME = "MineXD";
	public static final String SERVER_SITE = "www.minexd.com";

	private Style() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	public static String strip(String in) {
		return ChatColor.stripColor(translate(in));
	}

	public static String translate(String in) {
		return ChatColor.translateAlternateColorCodes('&', in);
	}

	public static List<String> translateLines(List<String> lines) {
		List<String> toReturn = new ArrayList<>();

		for (String line : lines) {
			toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
		}

		return toReturn;
	}

	public static String formatPlayerNotFoundMessage(String player) {
		return Style.RED + "Couldn't find a player with the name " + Style.RESET + player +
		       Style.RED + ". Have they joined the network?";
	}

	public static String formatBrokenProfileMessage(String player) {
		return Style.RED + "Couldn't load " + Style.RESET + player + Style.RED + "'s profile. Try again later.";
	}

	public static String formatFilteredPublicMessage(Player player, String message) {
		return new MessageFormat("{0}[Filtered] {1}{2}: {3}")
				.format(new Object[]{
						Style.RED + Style.BOLD, Style.RESET + NucleusAPI.getPrefixedName(player), Style.WHITE, message
				});
	}

	public static String[] formatPrivateMessage(String from, String to, String message) {
		String toMessage = Style.YELLOW + "(To " + to + Style.YELLOW + ") " + message;
		String fromMessage = Style.YELLOW + "(From " + from + Style.YELLOW + ") " + message;
		return new String[]{ toMessage, fromMessage };
	}

	public static String formatFreezeMessage(String sender, String target, String context) {
		return new MessageFormat("{2} {1}{3}{0} has been {5} by {1}{4}").format(new Object[]{
				Style.YELLOW, Style.PINK, Style.GOLD + Style.BOLD + Style.UNICODE_CAUTION, target, sender, context
		});
	}

	public static String formatMuteChatMessage(String sender, String context) {
		return new MessageFormat("{0}Public chat has been {3} by {1}{2}")
				.format(new Object[]{ Style.YELLOW, Style.PINK, sender, context });
	}

	public static String formatClearChatMessage(String sender) {
		return new MessageFormat("{0}Public chat has been cleared by {1}{2}")
				.format(new Object[]{ Style.YELLOW, Style.PINK, sender });
	}

	public static String formatStaffJoinMessage(String player, String server) {
		return new MessageFormat("{0}[S] {2}{3} {1}joined {4}")
				.format(new Object[]{ Style.AQUA, Style.AQUA, Style.RESET, player, server });
	}

	public static String formatStaffChatMessage(String server, String player, String message) {
		return new MessageFormat("{0}[S] [{5}] {2}{3}{1}: {4}")
				.format(new Object[]{ Style.AQUA, Style.AQUA, Style.RESET, player, message, server });
	}

	public static String formatReportMessage(String player, String target, String message, String server) {
		return new MessageFormat("{0}[R] [{6}] {2}{4} {1}reported by {2}{3} {1}for: {0}{5}")
				.format(new Object[]{
						Style.AQUA, Style.GRAY, Style.RESET, player, target, message, server
				});
	}

	public static String formatRequestMessage(String player, String message, String server) {
		return new MessageFormat("{0}[R] [{5}] {2}{3} {1}requested assistance: {0}{4}")
				.format(new Object[]{ Style.AQUA, Style.GRAY, Style.RESET, player, message, server });
	}

	public static String formatArrowHitMessage(String damaged, double health) {
		return Style.YELLOW + "You shot " + Style.PINK + damaged + Style.YELLOW + "!" + Style.GRAY + " (" + Style.RED +
		       health + Style.DARK_RED + " " + Style.UNICODE_HEART + Style.GRAY + ")";
	}

	public static String formatPunishmentMessage(String staff, String target, String context) {
		return new MessageFormat("&r{0} &ahas been {1} by &r{2}").format(new Object[]{ target, context, staff });
	}

	public static String getBorderLine() {
		int chatWidth = FONT_RENDERER.getWidth(MAX_LENGTH) / 10 * 9;

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 100; i++) {
			sb.append("-");

			if (FONT_RENDERER.getWidth(sb.toString()) >= chatWidth) {
				break;
			}
		}

		return Style.GRAY + Style.STRIKE_THROUGH + sb.toString();
	}

	public static String center(String string) {
		StringBuilder preColors = new StringBuilder();

		while (string.startsWith(ChatColor.COLOR_CHAR + "")) {
			preColors.append(string.substring(0, 2));
			string = string.substring(2, string.length());
		}

		int width = FONT_RENDERER.getWidth(string);
		int chatWidth = FONT_RENDERER.getWidth(MAX_LENGTH);

		if (width == chatWidth) {
			return string;
		} else if (width > chatWidth) {
			String[] words = string.split(" ");

			if (words.length == 1) {
				return string;
			}

			StringBuilder sb = new StringBuilder();
			int total = 0;

			for (String word : words) {
				int wordWidth = FONT_RENDERER.getWidth(word + " ");

				if (total + wordWidth > chatWidth) {
					sb.append("\n");
					total = 0;
				}

				total += wordWidth;
				sb.append(word).append(" ");
			}

			return center(preColors + sb.toString().trim());
		}

		StringBuilder sb = new StringBuilder();

		int diff = (chatWidth) - (width);
		diff /= 3;

		for (int i = 0; i < 100; i++) {
			sb.append(" ");
			if (FONT_RENDERER.getWidth(sb.toString()) >= diff) {
				break;
			}
		}

		sb.append(string);

		return preColors + sb.toString();
	}

}
