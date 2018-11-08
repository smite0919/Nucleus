package me.joeleoli.nucleus.player;

import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.cooldown.Cooldown;
import me.joeleoli.nucleus.event.player.RankUpdateEvent;
import me.joeleoli.nucleus.player.cosmetic.Tag;
import me.joeleoli.nucleus.punishment.Punishment;
import me.joeleoli.nucleus.punishment.SharedBan;
import me.joeleoli.nucleus.rank.Rank;
import me.joeleoli.nucleus.server.ServerType;
import me.joeleoli.nucleus.settings.Settings;
import me.joeleoli.nucleus.player.cosmetic.Color;
import me.joeleoli.nucleus.util.ObjectUtil;
import me.joeleoli.nucleus.uuid.UUIDCache;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

@Getter
public class NucleusPlayer extends PlayerInfo {

	@Getter
	private static Map<UUID, NucleusPlayer> cached = new HashMap<>();

	/* General */
	private Long firstLoginTimestamp;
	@Setter
	private String ipAddress;
	private Settings settings = new Settings();
	private List<UUID> ignored = new ArrayList<>();

	/* Ranks */
	private Rank activeRank;
	private Rank globalRank;
	private Map<ServerType, Rank> serverRanks = new HashMap<>();

	/* Punishments */
	private List<Punishment> punishments = new ArrayList<>();
	private SharedBan sharedBan;
	private List<UUID> alternates = new ArrayList<>();

	/* Customization */
	private Color color;
	@Setter
	private Tag tag;
	@Setter
	private Color tagColor;

	/* Session */
	@Setter
	private Long sessionLoginTimestamp;
	@Setter
	private UUID replyTo;
	@Setter
	private Cooldown chatCooldown = new Cooldown(1000);
	@Setter
	private Cooldown reportCooldown = new Cooldown(0);
	@Setter
	private Cooldown requestCooldown = new Cooldown(0);
	@Setter
	private boolean frozen;
	private boolean loaded;
	private long loadedAt = System.currentTimeMillis();

	public NucleusPlayer(UUID uuid) {
		super(uuid, null);

		NucleusPlayer.getCached().put(uuid, this);
	}

	/**
	 * Retrieves a cached instance of NucleusPlayer or creates and returns a new instance.
	 *
	 * @param uuid the player uuid
	 *
	 * @return the player's NucleusPlayer instance
	 */
	public static NucleusPlayer getByUuid(UUID uuid) {
		final NucleusPlayer toReturn = cached.get(uuid);
		return toReturn == null ? new NucleusPlayer(uuid) : toReturn;
	}

	/**
	 * This method should only be called asynchronously as it could fetch results from Redis.
	 *
	 * @param name the name
	 *
	 * @return A NucleusPlayer instance if results were fetched
	 */
	public static NucleusPlayer getByName(String name) {
		if (Bukkit.isPrimaryThread()) {
			throw new RuntimeException("Cannot use NucleusPlayer#getByName on primary thread!");
		}

		final Player target = Bukkit.getPlayer(name);
		final NucleusPlayer nucleusPlayer;

		if (target == null) {
			UUID uuid = UUIDCache.getUuid(name);

			if (uuid != null) {
				nucleusPlayer = NucleusPlayer.getByUuid(uuid);
			} else {
				return null;
			}
		} else {
			nucleusPlayer = NucleusPlayer.getByUuid(target.getUniqueId());
		}

		return nucleusPlayer;
	}

	/**
	 * Sets the player's color and updates the tab list.
	 *
	 * @param color the color
	 */
	public void setColor(Color color) {
		this.color = color;

		if (this.color != null) {
			this.refreshDisplayName();
		}
	}

	/**
	 * Sets the player's global rank or server rank based on the rank type.
	 * If a new active rank is applied, a RankUpdateEvent will be broadcast.
	 *
	 * @param rank the rank
	 */
	public void setRank(Rank rank) {
		final Rank oldRank = this.activeRank;

		if (rank.isGlobal()) {
			this.globalRank = rank;
		} else {
			this.serverRanks.put(rank.getServerType(), rank);
		}

		this.recalculateRank();

		if (!this.activeRank.equals(oldRank)) {
			new RankUpdateEvent(this, oldRank, this.activeRank).call();

			final Player player = this.toPlayer();

			if (player != null) {
				for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
					if (attachmentInfo.getAttachment() == null) {
						continue;
					}

					attachmentInfo.getAttachment().getPermissions().forEach((permission, value) -> {
						attachmentInfo.getAttachment().unsetPermission(permission);
					});
				}

				PermissionAttachment attachment = null;

				for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
					if (attachmentInfo.getAttachment() == null) {
						continue;
					}

					if (attachmentInfo.getAttachment().getPlugin() instanceof Nucleus) {
						attachment = attachmentInfo.getAttachment();
						break;
					}
				}

				if (attachment == null) {
					attachment = player.addAttachment(Nucleus.getInstance());
				}

				for (String effectivePermission : this.activeRank.getEffectivePermissions()) {
					attachment.setPermission(effectivePermission, true);
				}

				player.recalculatePermissions();
			}
		}
	}

	private void recalculateRank() {
		ServerType serverType = Nucleus.getInstance().getNucleusConfig().getServerType();
		Rank serverRank = this.serverRanks.getOrDefault(serverType, null);

		if (this.globalRank == null && serverRank == null) {
			this.activeRank = Rank.getDefaultRank();
		} else if (this.globalRank != null && serverRank != null) {
			this.activeRank = this.globalRank.getWeight() >= serverRank.getWeight() ? this.globalRank : serverRank;
		} else {
			if (this.globalRank == null) {
				this.activeRank = serverRank;
			} else {
				this.activeRank = this.globalRank;
			}
		}

		if (this.activeRank == null) {
			this.activeRank = Rank.getDefaultRank();
		}
	}

	public void refreshDisplayName() {
		final Player player = this.toPlayer();

		if (player != null) {
			if (player.hasPermission("nucleus.donor.color") && this.color != null) {
				player.setDisplayName(this.color.getDisplay() + player.getName());
				player.setPlayerListName(this.color.getDisplay() + player.getName());
			} else {
				if (this.activeRank == null) {
					player.setDisplayName(player.getName());
					player.setPlayerListName(player.getName());
				} else {
					player.setDisplayName(this.activeRank.getColor() + player.getName());
					player.setPlayerListName(this.activeRank.getColor() + player.getName());
				}
			}
		}
	}

	public boolean isIgnored(UUID uuid) {
		return this.ignored.contains(uuid);
	}

	public Punishment getActiveBan() {
		for (Punishment punishment : this.punishments) {
			if (punishment.isBan() && punishment.isActive()) {
				return punishment;
			}
		}

		return null;
	}

	public Punishment getActiveMute() {
		for (Punishment punishment : this.punishments) {
			if (!punishment.isBan() && punishment.isActive()) {
				return punishment;
			}
		}

		return null;
	}

	public void load() {
		if (Bukkit.isPrimaryThread()) {
			throw new RuntimeException("Attempting to query on main thread!");
		}

		Document document = Nucleus.getInstance().getNucleusMongo().getPlayer(this.getUuid());

		if (document == null) {
			this.loaded = true;
			this.activeRank = Rank.getDefaultRank();
			this.firstLoginTimestamp = System.currentTimeMillis();

			this.save();

			return;
		}

		if (this.getName() == null) {
			this.setName(document.getString("name"));
		}

		this.firstLoginTimestamp = document.getLong("first_login");
		this.ipAddress = document.containsKey("ip_address") ? document.getString("last_address") : this.ipAddress;
		this.alternates.addAll(Nucleus.getInstance().getNucleusMongo().getPlayerAlts(this.getUuid()));

		if (document.containsKey("cosmetics")) {
			Document cosmetics = (Document) document.get("cosmetics");

			if (cosmetics.containsKey("color")) {
				try {
					this.color = Color.valueOf(cosmetics.getString("color"));
				} catch (Exception ignore) {
				}
			}

			if (cosmetics.containsKey("tag")) {
				try {
					this.tag = Tag.valueOf(cosmetics.getString("tag"));
				} catch (Exception ignore) {
				}
			}

			if (cosmetics.containsKey("tag_color")) {
				try {
					this.tagColor = Color.valueOf(cosmetics.getString("tag_color"));
				} catch (Exception ignore) {
				}
			}
		}

		if (document.containsKey("global_rank")) {
			final Rank rank = Rank.getRankByName(document.getString("global_rank"));

			if (rank != null) {
				this.globalRank = rank;
			}
		}

		if (document.containsKey("server_ranks")) {
			Document serverRanksDocument = (Document) document.get("server_ranks");

			serverRanksDocument.keySet().forEach(key -> {
				final ServerType serverType = ServerType.valueOf(key);
				final Rank rank = Rank.getRankByName(serverRanksDocument.getString(key));

				if (rank != null) {
					this.serverRanks.put(serverType, rank);
				}
			});
		}

		if (document.containsKey("settings")) {
			Document settingsDocument = (Document) document.get("settings");

			settingsDocument.keySet().forEach(key -> {
				if (Settings.transform(key) != null) {
					this.settings.getSettings().put(
							Settings.transform(key),
							ObjectUtil.transform(settingsDocument.getString(key))
					);
				}
			});
		}

		try (MongoCursor<Document> cursor = Nucleus.getInstance().getNucleusMongo()
		                                           .getPunishmentsByTarget(this.getUuid())) {
			cursor.forEachRemaining(punishmentDocument -> {
				Punishment punishment = new Punishment();
				punishment.load(punishmentDocument);

				this.punishments.add(punishment);
			});
		}

		this.recalculateRank();

		new RankUpdateEvent(this, null, this.activeRank).call();

		this.loaded = true;
	}

	/**
	 * Finds alternate accounts by IP address.
	 * This should only be ran on player login.
	 */
	public void searchForAlts() {
		if (this.ipAddress != null) {
			try (MongoCursor<Document> cursor = Nucleus.getInstance().getNucleusMongo().getPlayersByIpAddress(this.ipAddress)) {
				cursor.forEachRemaining(document -> {
					final UUID uuid = UUID.fromString(document.getString("uuid"));

					if (!uuid.equals(this.getUuid())) {
						if (!this.alternates.contains(uuid)) {
							System.out.println("Found alt: " + uuid.toString());
							this.alternates.add(uuid);
						}
					}
				});
			}
		}
	}

	/**
	 * Finds an active ban from this player's alternate accounts.
	 */
	public void findSharedBan() {
		for (UUID altUuid : this.alternates) {
			final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(altUuid);

			if (!nucleusPlayer.isLoaded()) {
				nucleusPlayer.load();
			}

			final Punishment ban = nucleusPlayer.getActiveBan();

			if (ban != null) {
				this.sharedBan = new SharedBan(ban, nucleusPlayer.getName(), nucleusPlayer.getUuid());
			}
		}
	}

	/**
	 * Saves the player's profile.
	 */
	public void save() {
		Document document = Nucleus.getInstance().getNucleusMongo().getPlayer(this.getUuid());
		Document settingsDocument;

		if (document == null) {
			document = new Document();
			settingsDocument = new Document();
		} else {
			if (document.containsKey("settings")) {
				settingsDocument = (Document) document.get("settings");
			} else {
				settingsDocument = new Document();
			}
		}

		Document serverRanksDocument = new Document();
		Document cosmeticsDocument = new Document();

		if (this.color != null) {
			cosmeticsDocument.put("color", this.color.name());
		}

		if (this.tag != null) {
			cosmeticsDocument.put("tag", this.tag.name());
		}

		if (this.tagColor != null) {
			cosmeticsDocument.put("tag_color", this.tagColor.name());
		}

		this.settings.getSettings().forEach((key, value) -> settingsDocument.put(key.name(), String.valueOf(value)));
		this.serverRanks.forEach((serverType, rank) -> serverRanksDocument.put(serverType.name(), rank.getName()));

		document.put("uuid", this.getUuid().toString());
		document.put("name", this.getName());
		document.put("first_login", this.firstLoginTimestamp);
		document.put("last_login", System.currentTimeMillis());
		document.put("last_address", this.ipAddress);
		document.put("server_ranks", serverRanksDocument);
		document.put("settings", settingsDocument);
		document.put("cosmetics", cosmeticsDocument);

		if (this.globalRank != null) {
			document.put("global_rank", this.globalRank.getName());
		}

		Nucleus.getInstance().getNucleusMongo().replacePlayer(this, document);

		this.alternates.forEach(uuid -> {
			Nucleus.getInstance().getNucleusMongo().replacePlayerAlt(this.getUuid(), uuid);
		});
	}

}
