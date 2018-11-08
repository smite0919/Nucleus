package me.joeleoli.nucleus.player.cosmetic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.joeleoli.nucleus.util.Style;

@Getter
@AllArgsConstructor
public enum Color {

	DARK_RED("Dark Red", Style.DARK_RED, 14),
	RED("Red", Style.RED, 14),
	PURPLE("Purple", Style.DARK_PURPLE, 10),
	PINK("Pink", Style.PINK, 2),
	GOLD("Gold", Style.GOLD, 1),
	YELLOW("Yellow", Style.YELLOW, 4),
	GREEN("Green", Style.GREEN, 5),
	DARK_GREEN("Dark Green", Style.DARK_GREEN, 13),
	LIGHT_BLUE("Light Blue", Style.AQUA, 11),
	AQUA("Aqua", Style.DARK_AQUA, 9),
	BLUE("Blue", Style.BLUE, 3);

	private String name;
	private String display;
	private int variant;

}
