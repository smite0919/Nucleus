package me.joeleoli.nucleus.player.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import me.joeleoli.nucleus.menu.Button;
import me.joeleoli.nucleus.menu.Menu;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.cosmetic.Tag;
import me.joeleoli.nucleus.util.ItemBuilder;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SelectTagMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return Style.GOLD + "Select a Tag";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		final Map<Integer, Button> buttons = new HashMap<>();

		for (Tag tag : Tag.values()) {
			buttons.put(buttons.size(), new SelectTagButton(tag));
		}

		buttons.put(40, new ResetTagButton());

		return buttons;
	}

	@AllArgsConstructor
	private class SelectTagButton extends Button {

		private Tag tag;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.NAME_TAG)
					.name(this.tag.getSelectionDisplay())
					.lore(Arrays.asList(
							"",
							Style.translate("&eClick here to select &d" + this.tag.getName() + "&e.")
					))
					.build();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
			final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

			nucleusPlayer.setTag(this.tag);

			player.closeInventory();
			player.sendMessage(
					Style.translate("&aYou set your tag to: &r" + this.tag.getSelectionDisplay()));
		}

	}

	private class ResetTagButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.NAME_TAG)
					.name(Style.YELLOW + Style.BOLD + "Reset Tag")
					.lore(Arrays.asList(
							"",
							Style.YELLOW + "Click here to reset your tag."
					))
					.build();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
			final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

			nucleusPlayer.setTag(null);

			player.closeInventory();
			player.sendMessage(Style.GREEN + "You reset your tag.");
		}

	}

}
