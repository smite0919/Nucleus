package me.joeleoli.nucleus.punishment.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import me.joeleoli.nucleus.menu.Button;
import me.joeleoli.nucleus.menu.pagination.PaginatedMenu;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.punishment.Punishment;
import me.joeleoli.nucleus.util.ItemBuilder;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.util.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PunishmentHistoryMenu extends PaginatedMenu {

	private NucleusPlayer nucleusPlayer;

	@Override
	public String getPrePaginatedTitle(Player player) {
		return this.nucleusPlayer.getActiveRank().getColor() + this.nucleusPlayer.getName() + Style.GOLD;
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		final Map<Integer, Button> buttons = new HashMap<>();

		this.nucleusPlayer.getPunishments().forEach(punishment -> {
			buttons.put(buttons.size(), new PunishmentDisplayButton(punishment));
		});

		return buttons;
	}

	@AllArgsConstructor
	private static class PunishmentDisplayButton extends Button {

		private Punishment punishment;

		@Override
		public ItemStack getButtonItem(Player player) {
			String status;
			String addedByName;

			if (this.punishment.isActive()) {
				status = Style.GREEN + "Active";
			} else if (this.punishment.isRemoved()) {
				status = Style.YELLOW + "Removed";
			} else {
				status = Style.GRAY + "Expired";
			}

			if (this.punishment.getAddedBy() == null) {
				addedByName = Style.DARK_RED + "Console";
			} else {
				final NucleusPlayer staffData = NucleusPlayer.getByUuid(this.punishment.getAddedBy());

				if (!staffData.isLoaded()) {
					staffData.load();
				}

				addedByName = staffData.getActiveRank().getColor() + staffData.getName();
			}

			final List<String> lore = new ArrayList<>();

			lore.add(Style.YELLOW + "Type: " + Style.GRAY + this.punishment.getType().name());
			lore.add(Style.YELLOW + "Status: " + Style.GRAY + status);
			lore.add(Style.YELLOW + "Added by: " + Style.GRAY + addedByName);
			lore.add(Style.YELLOW + "Added for: " + Style.GRAY + this.punishment.getAddedReason());

			if (this.punishment.isRemoved()) {
				final String removedByName;

				if (this.punishment.getRemovedBy() == null) {
					removedByName = Style.DARK_RED + "Console";
				} else {
					final NucleusPlayer staffData = NucleusPlayer.getByUuid(this.punishment.getRemovedBy());

					if (!staffData.isLoaded()) {
						staffData.load();
					}

					removedByName = staffData.getActiveRank().getColor() + staffData.getName();
				}

				lore.add(Style.YELLOW + "Removed by: " + Style.GRAY + removedByName);
				lore.add(Style.YELLOW + "Removed for: " + Style.GRAY + this.punishment.getRemoveReason());
			}

			lore.add(0, Style.BORDER_LINE_SCOREBOARD);
			lore.add(Style.BORDER_LINE_SCOREBOARD);

			return new ItemBuilder(Material.PAPER)
					.name(Style.GOLD + TimeUtil.dateToString(new Date(this.punishment.getTimestamp().getTime())))
					.lore(lore)
					.build();
		}

	}

}
