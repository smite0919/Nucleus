package me.joeleoli.nucleus.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.NucleusAPI;
import me.joeleoli.nucleus.player.NucleusPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtil {

	public static final Comparator<Player> VISIBLE_RANK_ORDER = (a, b) -> {
		NucleusPlayer playerA = NucleusPlayer.getByUuid(a.getUniqueId());
		NucleusPlayer playerB = NucleusPlayer.getByUuid(b.getUniqueId());

		return -playerA.getActiveRank().compareTo(playerB.getActiveRank());
	};

	public static List<Player> getPlayersSortedByRank() {
		final List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());

		list.sort(VISIBLE_RANK_ORDER);

		return list;
	}

	public static void spawn(Player player) {
		player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
	}

	public static void reset(Player player) {
		reset(player, true);
	}

	public static void reset(Player player, boolean resetHeldSlot) {
		if (!NucleusAPI.isFrozen(player)) {
			player.setWalkSpeed(0.2F);
			player.setFlySpeed(0.0001F);
		}

		player.setHealth(20.0D);
		player.setSaturation(20.0F);
		player.setFallDistance(0.0F);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setMaximumNoDamageTicks(20);
		player.setExp(0.0F);
		player.setLevel(0);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

		if (resetHeldSlot) {
			player.getInventory().setHeldItemSlot(0);
		}

		player.updateInventory();
	}

	public static void denyMovement(Player player) {
		player.setWalkSpeed(0.0F);
		player.setFlySpeed(0.0F);
		player.setFoodLevel(0);
		player.setSprinting(false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
	}

	public static void allowMovement(Player player) {
		player.setWalkSpeed(0.2F);
		player.setFlySpeed(0.0001F);
		player.setFoodLevel(20);
		player.setSprinting(true);
		player.removePotionEffect(PotionEffectType.JUMP);
	}

	public static void messageAll(String message) {
		for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
			player.sendMessage(message);
		}
	}

	public static void messageStaff(String message) {
		for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
			if (player.hasPermission("nucleus.staff")) {
				player.sendMessage(message);
			}
		}
	}

	public static List<Player> convertUUIDListToPlayerList(List<UUID> list) {
		return list.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public List<String> getPlayerList() {
		return PlayerUtil.getPlayersSortedByRank().stream()
		                 .map(Player::getUniqueId)
		                 .map(NucleusPlayer::getByUuid)
		                 .map(nucleusPlayer -> nucleusPlayer.getActiveRank().getColor() +
		                                       nucleusPlayer.getDisplayName() + Style.RESET)
		                 .collect(Collectors.toList());
	}

}
