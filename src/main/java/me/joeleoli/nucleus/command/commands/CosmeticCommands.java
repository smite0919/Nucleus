package me.joeleoli.nucleus.command.commands;

import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.player.cosmetic.CosmeticEditType;
import me.joeleoli.nucleus.player.gui.CosmeticTagMenu;
import me.joeleoli.nucleus.player.gui.SelectColorMenu;
import org.bukkit.entity.Player;

public class CosmeticCommands {

	@Command(names = "color", permissionNode = "nucleus.donor.color")
	public static void color(Player player) {
		new SelectColorMenu(CosmeticEditType.COLOR).openMenu(player);
	}

	@Command(names = "tag", permissionNode = "nucleus.donor.tag")
	public static void tag(Player player) {
		new CosmeticTagMenu().openMenu(player);
	}

}
