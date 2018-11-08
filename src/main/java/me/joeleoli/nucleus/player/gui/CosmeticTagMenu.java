package me.joeleoli.nucleus.player.gui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import me.joeleoli.nucleus.menu.Button;
import me.joeleoli.nucleus.menu.Menu;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.cosmetic.CosmeticEditType;
import me.joeleoli.nucleus.util.ItemBuilder;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CosmeticTagMenu extends Menu {

	@Override
	public int getSize() {
		return 45;
	}

	@Override
	public String getTitle(Player player) {
		return Style.GOLD + Style.BOLD + "Edit your Tag";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		final Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(11, new EditButton(CosmeticEditType.TAG));
		buttons.put(15, new EditButton(CosmeticEditType.COLOR));
		buttons.put(31, new ResetTagButton());

		return buttons;
	}

	@AllArgsConstructor
	private class EditButton extends Button {

		private CosmeticEditType editType;

		@Override
		public ItemStack getButtonItem(Player player) {
			return this.editType.getItemStack();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
			if (this.editType == CosmeticEditType.TAG) {
				new SelectTagMenu().openMenu(player);
			} else {
				new SelectColorMenu(CosmeticEditType.TAG).openMenu(player);
			}
		}

	}

	private class ResetTagButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.NETHER_STAR)
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
			nucleusPlayer.setTagColor(null);

			player.closeInventory();
			player.sendMessage(Style.GREEN + "You reset your tag.");
		}

	}

}
