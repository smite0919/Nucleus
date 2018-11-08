package me.joeleoli.nucleus.player.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import me.joeleoli.nucleus.menu.Button;
import me.joeleoli.nucleus.menu.Menu;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.cosmetic.Color;
import me.joeleoli.nucleus.player.cosmetic.CosmeticEditType;
import me.joeleoli.nucleus.util.ItemBuilder;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class SelectColorMenu extends Menu {

	private CosmeticEditType editType;

	@Override
	public String getTitle(Player player) {
		if (this.editType == CosmeticEditType.TAG) {
			return Style.GOLD + Style.BOLD + "Select a Tag Color";
		} else {
			return Style.GOLD + Style.BOLD + "Select a Color";
		}
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		final Map<Integer, Button> buttons = new HashMap<>();

		for (Color color : Color.values()) {
			buttons.put(buttons.size(), new SelectColorButton(color, this.editType));
		}

		if (this.editType == CosmeticEditType.COLOR) {
			buttons.put(31, new ResetColorButton());
		}

		return buttons;
	}

	@AllArgsConstructor
	private class SelectColorButton extends Button {

		private Color color;
		private CosmeticEditType editType;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.WOOL)
					.durability(this.color.getVariant())
					.name(this.color.getDisplay() + this.color.getName())
					.lore(Arrays.asList(
							"",
							Style.translate("&eClick here to select &r" + this.color.getDisplay() + this.color.getName() + "&e.")
					))
					.build();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
			final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

			if (this.editType == CosmeticEditType.TAG) {
				nucleusPlayer.setTagColor(this.color);
			} else {
				nucleusPlayer.setColor(this.color);
			}

			player.closeInventory();
			player.sendMessage(
					Style.translate("&aYou set your color to: &r" + this.color.getDisplay() + this.color.getName()));
		}

	}

	private class ResetColorButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.WOOL)
					.name(Style.YELLOW + Style.BOLD + "Reset Color")
					.lore(Arrays.asList(
							"",
							Style.YELLOW + "Click here to reset your color."
					))
					.build();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
			final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

			nucleusPlayer.setColor(null);

			player.closeInventory();
			player.sendMessage(Style.GREEN + "You reset your color.");
		}

	}

}
