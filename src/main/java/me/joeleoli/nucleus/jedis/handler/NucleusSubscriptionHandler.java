package me.joeleoli.nucleus.jedis.handler;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.UUID;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.jedis.JedisSubscriptionHandler;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.punishment.Punishment;
import me.joeleoli.nucleus.rank.Rank;
import me.joeleoli.nucleus.util.PlayerUtil;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.util.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class NucleusSubscriptionHandler implements JedisSubscriptionHandler {

	@Override
	public void handleMessage(JsonObject object) {
		NucleusPayload payload;

		try {
			payload = NucleusPayload.valueOf(object.get("payload").getAsString());
		} catch (IllegalArgumentException e) {
			Nucleus.getInstance().getLogger().warning("Received a payload-type that could not be parsed");
			return;
		}

		JsonObject data = object.get("data").getAsJsonObject();

		switch (payload) {
			case CLEAR_PUNISHMENTS: {
				NucleusPlayer.getCached().values().forEach(playerData -> playerData.getPunishments().clear());
			}
			break;
			case PUNISHMENT: {
				final UUID uuid = UUID.fromString(data.get("punishment_uuid").getAsString());
				final Punishment punishment = new Punishment();

				punishment.setUuid(uuid);
				punishment.load();
				punishment.broadcast(data.get("target_name").getAsString(), data.get("staff_name").getAsString());

				final Player player = Bukkit.getPlayer(punishment.getTargetUuid());
				final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(punishment.getTargetUuid());

				nucleusPlayer.getPunishments().removeIf(check -> check.getUuid().equals(punishment.getUuid()));
				nucleusPlayer.getPunishments().add(punishment);

				if (player == null) {
					return;
				}

				if (nucleusPlayer.getActiveBan() != null) {
					final String kickMessage = punishment.getType().getMessage()
					                                     .replace("%PLAYER%", "SHARED")
					                                     .replace("%EXPIRATION%", punishment.getTimeLeft());

					TaskUtil.run(() -> player.kickPlayer(kickMessage));

					for (Player alt : Nucleus.getInstance().getServer().getOnlinePlayers()) {
						if (player.getAddress().getAddress().getHostAddress()
						          .equalsIgnoreCase(alt.getAddress().getAddress().getHostAddress())) {
							TaskUtil.run(() -> alt.kickPlayer(kickMessage));
						}
					}
				} else if (nucleusPlayer.getActiveMute() != null) {
					player.sendMessage(Style.RED + "You have been muted.");
					player.sendMessage(Style.RED + "Time left: " + nucleusPlayer.getActiveMute().getTimeLeft());
				}
			}
			break;
			case RANK_DELETE: {
				Rank rank = Rank.getRankByName(data.get("rank_name").getAsString());

				if (rank != null) {
					rank.delete(false);
				}
			}
			break;
			case RANK_UPDATE: {
				Rank rank = Rank.getRankByName(data.get("rank_name").getAsString());

				if (rank == null) {
					rank = new Rank(data.get("rank_name").getAsString());
				}

				rank.load(Nucleus.getInstance().getNucleusMongo().getRank(rank.getName()));
			}
			break;
			case BROADCAST_MESSAGE: {
				final String message = Style.GOLD + Style.BOLD + "[MXD] " + Style.YELLOW +
				                       Style.translate(data.get("message").getAsString());

				Bukkit.getOnlinePlayers().forEach(player -> {
					player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
					player.sendMessage(message);
				});
			}
			break;
			case STAFF_JOIN: {
				final String server = data.get("server").getAsString();
				final String playerName = data.get("player_name").getAsString();

				PlayerUtil.messageStaff(Style.formatStaffJoinMessage(playerName, server));
			}
			break;
			case STAFF_CHAT: {
				final String server = data.get("server").getAsString();
				final String playerName = data.get("player_name").getAsString();
				final String message = data.get("message").getAsString();

				PlayerUtil.messageStaff(Style.formatStaffChatMessage(server, playerName, message));
			}
			break;
			case PLAYER_UPDATE_RANK: {
				final UUID uuid = UUID.fromString(data.get("player_uuid").getAsString());
				final Player player = Bukkit.getPlayer(uuid);

				if (player != null) {
					final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());
					final Rank rank = Rank.getRankByName(data.get("rank_name").getAsString());

					if (rank != null && !nucleusPlayer.getActiveRank().equals(rank)) {
						nucleusPlayer.setRank(rank);

						player.sendMessage(
								Style.translate("&aYour rank has been updated to: &r" + rank.getColoredName()));
					}
				}
			}
			break;
			case PLAYER_REPORT: {
				final String server = data.get("server").getAsString();
				final String senderName = data.get("sender_name").getAsString();
				final String reportedName = data.get("reported_name").getAsString();
				final String reportedReason = data.get("report_reason").getAsString();

				PlayerUtil.messageStaff(Style.formatReportMessage(senderName, reportedName, reportedReason, server));
			}
			break;
			case PLAYER_REQUEST: {
				final String server = data.get("server").getAsString();
				final String senderName = data.get("sender_name").getAsString();
				final String requestReason = data.get("request_reason").getAsString();

				PlayerUtil.messageStaff(Style.formatRequestMessage(senderName, requestReason, server));
			}
			break;
		}
	}

}
