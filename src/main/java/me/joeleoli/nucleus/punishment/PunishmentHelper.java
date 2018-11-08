package me.joeleoli.nucleus.punishment;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.Getter;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.NucleusAPI;
import me.joeleoli.nucleus.jedis.handler.NucleusPayload;
import me.joeleoli.nucleus.json.JsonChain;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.player.PlayerInfo;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.util.TimeUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class handles everything that a punishment command handler needs to do.
 */
@Getter
public class PunishmentHelper {

	private Punishment punishment;
	private CommandSender sender;
	private PlayerInfo targetInfo;
	private NucleusPlayer targetData;
	private Long duration;
	private String reason;
	private boolean silent;
	private boolean undo;
	private boolean targetNotFound;
	private boolean brokenProfile;
	private boolean invalidPunishment;

	public PunishmentHelper(PunishmentType punishmentType, CommandSender sender, PlayerInfo targetInfo, String duration, String reason, String flags, boolean undo) {
		this.targetInfo = targetInfo;
		this.targetData = NucleusPlayer.getByName(targetInfo.getName());

		if (this.targetData == null) {
			this.targetNotFound = true;
			return;
		}

		if (!this.targetData.isLoaded()) {
			this.targetData.load();
		}

		if (!this.targetData.isLoaded()) {
			this.brokenProfile = true;
			return;
		}

		if (undo) {
			if (punishmentType == PunishmentType.BAN || punishmentType == PunishmentType.TEMPBAN) {
				this.punishment = this.targetData.getActiveBan();
			} else {
				this.punishment = this.targetData.getActiveMute();
			}

			if (this.punishment == null) {
				this.invalidPunishment = true;
			}
		} else {
			if (punishmentType == PunishmentType.BAN || punishmentType == PunishmentType.TEMPBAN) {
				if (this.targetData.getActiveBan() != null) {
					this.invalidPunishment = true;
				}
			} else {
				if (this.targetData.getActiveMute() != null) {
					this.invalidPunishment = true;
				}
			}

			this.punishment = new Punishment();
			this.punishment.setUuid(UUID.randomUUID());
			this.punishment.setType(punishmentType);
		}

		if (this.invalidPunishment) {
			return;
		}

		this.sender = sender;
		this.duration = duration != null ? TimeUtil.parseTime(duration) : null;
		this.reason = reason;
		this.undo = undo;

		for (char c : flags.replace("-", "").toCharArray()) {
			if (c == 's') {
				this.silent = true;
			} else if (c == 'p') {
				this.silent = false;
			}
		}
	}

	public void execute() {
		UUID staffUuid;
		String staffName;

		if (this.sender instanceof Player) {
			final Player player = ((Player) this.sender);

			staffUuid = player.getUniqueId();
			staffName = NucleusAPI.getColoredName(player);
		} else {
			staffUuid = null;
			staffName = Style.DARK_RED + "Console";
		}

		String targetName = this.targetInfo.getName();

		if (this.targetData.getName().equalsIgnoreCase(this.targetInfo.getName())) {
			targetName = Style.RESET + this.targetData.getActiveRank().getColor() + this.targetData.getName();
		}

		this.punishment.setTargetUuid(this.targetData.getUuid());
		this.punishment.setSilent(this.silent);

		if (this.undo) {
			this.punishment.setRemovedBy(staffUuid);
			this.punishment.setRemoveReason(this.reason);
		} else {
			this.punishment.setAddedBy(staffUuid);
			this.punishment.setAddedReason(this.reason);
			this.punishment.setTimestamp(new Timestamp(System.currentTimeMillis()));

			if (this.duration != null) {
				this.punishment.setExpiration(new Timestamp(System.currentTimeMillis() + this.duration));
			}
		}

		this.punishment.save();

		Nucleus.getInstance().getNucleusJedis().write(
				NucleusPayload.PUNISHMENT,
				new JsonChain()
						.addProperty("punishment_uuid", this.punishment.getUuid().toString())
						.addProperty("staff_name", staffName)
						.addProperty("target_name", targetName)
						.get()
		);
	}

	public String getTargetName() {
		if (this.targetData != null && !this.brokenProfile) {
			if (this.targetData.getName() != null && this.targetInfo.getName().equalsIgnoreCase(this.targetData.getName())) {
				return Style.RESET + this.targetData.getActiveRank().getColor() + this.targetData.getName();
			} else {
				return Style.RESET + this.targetData.getActiveRank().getColor() + this.targetInfo.getName();
			}
		} else {
			return Style.RESET + this.targetInfo.getName();
		}
	}

}
