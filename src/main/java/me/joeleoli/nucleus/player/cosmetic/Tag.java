package me.joeleoli.nucleus.player.cosmetic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.joeleoli.nucleus.util.Style;
import org.apache.commons.lang3.StringEscapeUtils;

@Getter
@AllArgsConstructor
public enum Tag {

	SUMO_GOD("Sumo God", Style.BOLD + "SumoGod", false),
	RIP_HQ("RIP MineHQ", Style.BOLD + "RIP-MHQ", false),
	PEDO_HALT("PedoHalt", Style.BOLD + "PedoHalt", false),
	HASH_TAG_LEAN("#LEAN", Style.BOLD + "#LEAN", false),
	HASH_TAG_300("#300", Style.BOLD + "#300", false),
	HASH_TAG_600("#600", Style.BOLD + "#600", false),
	HACKER("Hacker", "Hacker", false),
	ARABIC_STAR("Fancy Star", StringEscapeUtils.unescapeJava("\u06DE"), true),
	YIN_YANG("Yin Yang", StringEscapeUtils.unescapeJava("\u0FCA"), true),
	RADIOACTIVE("Radioactive", StringEscapeUtils.unescapeJava("\u2622"), true),
	BIOHAZARD("Biohazard", StringEscapeUtils.unescapeJava("\u2623"), true),
	GEAR("Gear", Style.BOLD + StringEscapeUtils.unescapeJava("\u2699"), true),
	CHECK_MARK("Check Mark", Style.BOLD + StringEscapeUtils.unescapeJava("\u2713"), true),
	X_MARK("X Mark", StringEscapeUtils.unescapeJava("\u2717"), true),
	STAR_OF_DAVID("Star of David", StringEscapeUtils.unescapeJava("\u2721"), true),
	MALTESE_CROSS("Maltese Cross", StringEscapeUtils.unescapeJava("\u2720"), true),
	CIRCLED_STAR("Circled Star", StringEscapeUtils.unescapeJava("\u272A"), true),
	POINTED_STAR("Pointed Star", StringEscapeUtils.unescapeJava("\u2726"), true),
	FLORETTE("Florette", Style.BOLD + StringEscapeUtils.unescapeJava("\u273F"), true),
	HEART("Heart", Style.BOLD + Style.UNICODE_HEART, true),
	CAUTION("Caution", Style.BOLD + Style.UNICODE_CAUTION, true);

	private String name;
	private String display;
	private boolean icon;

	public String getSelectionDisplay() {
		return Style.translate("&e" + this.name + (this.icon ? " &7(&d" + this.display + "&7)" : ""));
	}

}
