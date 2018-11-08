package me.joeleoli.nucleus.punishment;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.chat.ChatComponentBuilder;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.util.TimeUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bson.Document;
import org.bukkit.entity.Player;

@Data
@NoArgsConstructor
public class Punishment {

	public static final String APPEAL_FOOTER = "To appeal, visit http://" + Style.SERVER_SITE;

	private UUID uuid;
	private PunishmentType type;
	private UUID targetUuid;
	private UUID addedBy;
	private String addedReason;
	private UUID removedBy;
	private String removeReason;
	private Timestamp timestamp;
	private Timestamp expiration;
	private boolean silent = false;

	public boolean isBan() {
		return this.type == PunishmentType.TEMPBAN || this.type == PunishmentType.BAN;
	}

	public boolean isActive() {
		return !this.isRemoved() && (this.isPermanent() || System.currentTimeMillis() - this.expiration.getTime() < 0);
	}

	public boolean isRemoved() {
		return this.removeReason != null;
	}

	public boolean isPermanent() {
		return this.expiration == null;
	}

	public String getTimeLeft() {
		if (this.isRemoved()) {
			return "Removed";
		}

		if (this.isPermanent()) {
			return "Permanent";
		}

		if (!(this.isActive())) {
			return "Expired";
		}

		return TimeUtil.millisToRoundedTime(this.expiration.getTime() - System.currentTimeMillis());
	}

	public void load() {
		this.load(Nucleus.getInstance().getNucleusMongo().getPunishmentByUuid(this.uuid));
	}

	public void load(Document document) {
		if (document == null) {
			return;
		}

		this.uuid = UUID.fromString(document.getString("uuid"));
		this.type = PunishmentType.valueOf(document.getString("type"));
		this.targetUuid = UUID.fromString(document.getString("target_uuid"));
		this.addedReason = document.getString("added_reason");
		this.timestamp = new Timestamp(document.getLong("timestamp"));
		this.silent = document.getBoolean("silent");

		if (document.containsKey("added_by")) {
			this.addedBy = UUID.fromString(document.getString("added_by"));
		}

		if (document.containsKey("removed_by")) {
			this.removedBy = UUID.fromString(document.getString("removed_by"));
		}

		if (document.containsKey("remove_reason")) {
			this.removeReason = document.getString("remove_reason");
		}

		if (document.containsKey("expiration")) {
			this.expiration = new Timestamp(document.getLong("expiration"));
		}
	}

	public void save() {
		Document document = new Document();

		document.put("uuid", this.uuid.toString());
		document.put("type", this.type.name());
		document.put("target_uuid", this.targetUuid.toString());
		document.put("added_reason", this.addedReason);
		document.put("timestamp", this.timestamp.getTime());
		document.put("silent", this.silent);

		if (this.addedBy != null) {
			document.put("added_by", this.addedBy.toString());
		}

		if (this.expiration != null) {
			document.put("expiration", this.expiration.getTime());
		}

		if (this.removedBy != null) {
			document.put("removed_by", this.removedBy.toString());
		}

		if (this.removeReason != null) {
			document.put("remove_reason", this.removeReason);
		}

		Nucleus.getInstance().getNucleusMongo().replacePunishment(this, document);
	}

	public void broadcast(String targetName, String staffName) {
		final boolean undo = this.removeReason != null;
		final String broadcast = (this.silent ? Style.GRAY + "[Silent] " : "") +
		                         Style.formatPunishmentMessage(staffName, targetName,
				                         undo ? this.type.getUndoContext() : this.type.getContext()
		                         );

		for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
			if (this.silent && !player.hasPermission("nucleus.staff")) {
				continue;
			}

			final HoverEvent hoverEvent;

			if (player.hasPermission("nucleus.staff")) {
				hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse("&eReason: &c" + (undo ? this.removeReason : this.addedReason))
						.parse("\n")
						.parse("&eTime left: &c" + this.getTimeLeft())
						.create());
			} else {
				hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
						.parse(undo ? "&eWelcome back to the Big XD!" : "&ePress F to pay respects...")
						.create());
			}

			final BaseComponent[] components = new ChatComponentBuilder("")
					.parse(broadcast)
					.attachToEachPart(hoverEvent)
					.create();

			player.spigot().sendMessage(components);
		}
	}

}
