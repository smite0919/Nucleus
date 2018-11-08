package me.joeleoli.nucleus.player.cosmetic;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.joeleoli.nucleus.util.ItemBuilder;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum CosmeticEditType {

	TAG(
			new ItemBuilder(Material.NAME_TAG)
					.name(Style.YELLOW + Style.BOLD + "Select a Tag")
					.lore(Arrays.asList(
							"",
							Style.YELLOW + "Click here to select a Tag."
					))
					.build()
	),
	COLOR(
			new ItemBuilder(Material.WOOL)
					.durability(2)
					.name(Style.YELLOW + Style.BOLD + "Select a Tag Color")
					.lore(Arrays.asList(
							"",
							Style.YELLOW + "Click here to select a Tag Color."
					))
					.build()
	);

	private ItemStack itemStack;

}