package me.joeleoli.nucleus.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.config.ConfigCursor;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.punishment.Punishment;
import me.joeleoli.nucleus.rank.Rank;
import org.bson.Document;

public class NucleusMongo implements Closeable {

	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> punishments;
	private MongoCollection<Document> ranks;
	private MongoCollection<Document> players;
	private MongoCollection<Document> playerAlts;
	private MongoCollection<Document> connectionLogs;
	private MongoCollection<Document> commandLogs;
	private MongoCollection<Document> privateMessagesLogs;
	private MongoCollection<Document> publicMessagesLogs;

	public NucleusMongo() {
		ConfigCursor cursor = new ConfigCursor(Nucleus.getInstance().getMainFileConfig(), "mongo");

		if (!cursor.exists("host")
		    || !cursor.exists("port")
		    || !cursor.exists("database")
		    || !cursor.exists("authentication.enabled")
		    || !cursor.exists("authentication.username")
		    || !cursor.exists("authentication.password")
		    || !cursor.exists("authentication.database")) {
			throw new RuntimeException("Missing configuration option");
		}

		if (cursor.getBoolean("authentication.enabled")) {
			final MongoCredential credential = MongoCredential.createCredential(
					cursor.getString("authentication.username"),
					cursor.getString("authentication.database"),
					cursor.getString("authentication.password").toCharArray()
			);

			this.client = new MongoClient(new ServerAddress(cursor.getString("host"), cursor.getInt("port")),
					Collections.singletonList(credential)
			);
		} else {
			this.client = new MongoClient(new ServerAddress(cursor.getString("host"), cursor.getInt("port")));
		}

		this.database = this.client.getDatabase("nucleus");
		this.punishments = this.database.getCollection("punishments");
		this.ranks = this.database.getCollection("ranks");
		this.players = this.database.getCollection("players");
		this.playerAlts = this.database.getCollection("player_alts");
		this.connectionLogs = this.database.getCollection("connection_logs");
		this.commandLogs = this.database.getCollection("command_logs");
		this.privateMessagesLogs = this.database.getCollection("private_messages");
		this.publicMessagesLogs = this.database.getCollection("global_messages");
	}

	public Document getPlayer(UUID uuid) {
		return this.players.find(Filters.eq("uuid", uuid.toString())).first();
	}

	public MongoCursor<Document> getPlayersByIpAddress(String ipAddress) {
		return this.players.find(Filters.eq("ip_address", ipAddress)).iterator();
	}

	public void replacePlayer(NucleusPlayer player, Document document) {
		this.players.replaceOne(Filters.eq("uuid", player.getUuid().toString()), document,
				new ReplaceOptions().upsert(true)
		);
	}

	public List<UUID> getPlayerAlts(UUID uuid) {
		final BasicDBObject query = new BasicDBObject();
		final List<BasicDBObject> conditions = new ArrayList<>();

		conditions.add(new BasicDBObject("uuid1", uuid.toString()));
		conditions.add(new BasicDBObject("uuid2", uuid.toString()));
		query.put("$or", conditions);

		final List<UUID> alts = new ArrayList<>();

		try (MongoCursor<Document> iterator = this.playerAlts.find(query).iterator()) {
			while (iterator.hasNext()) {
				final Document document = iterator.next();
				final UUID uuid1 = UUID.fromString(document.getString("uuid1"));
				final UUID uuid2 = UUID.fromString(document.getString("uuid2"));
				final UUID alt;

				if (uuid.equals(uuid1)) {
					alt = uuid2;
				} else {
					alt = uuid1;
				}

				if (!alts.contains(alt)) {
					alts.add(alt);
				}
			}
		}

		return alts;
	}

	public void replacePlayerAlt(UUID uuid1, UUID uuid2) {
		final Document document = new Document();

		document.put("uuid1", uuid1.toString());
		document.put("uuid2", uuid2.toString());

		this.playerAlts.replaceOne(document, document, new ReplaceOptions().upsert(true));
	}

	public Document getPunishmentByUuid(UUID uuid) {
		return this.punishments.find(Filters.eq("uuid", uuid.toString())).first();
	}

	public MongoCursor<Document> getPunishmentsByTarget(UUID uuid) {
		return this.punishments.find(Filters.eq("target_uuid", uuid.toString())).iterator();
	}

	public void replacePunishment(Punishment punishment, Document document) {
		this.punishments.replaceOne(Filters.eq("uuid", punishment.getUuid().toString()), document,
				new ReplaceOptions().upsert(true)
		);
	}

	public Document getRank(String name) {
		return this.ranks.find(Filters.eq("name", name)).first();
	}

	public MongoCursor<Document> getRanks() {
		return this.ranks.find().iterator();
	}

	public void replaceRank(Rank rank, Document document) {
		this.ranks.replaceOne(Filters.eq("name", rank.getName()), document, new ReplaceOptions().upsert(true));
	}

	public void deleteRank(Rank rank) {
		this.ranks.deleteOne(Filters.eq("name", rank.getName()));
	}

	public void insertCommandLogs(List<Document> documents) {
		this.commandLogs.insertMany(documents);
	}

	public void insertConnectionLogs(List<Document> documents) {
		this.connectionLogs.insertMany(documents);
	}

	public void insertPrivateMessageLogs(List<Document> documents) {
		this.privateMessagesLogs.insertMany(documents);
	}

	public void insertPublicMessageLogs(List<Document> documents) {
		this.publicMessagesLogs.insertMany(documents);
	}

	public void dropPunishments() {
		this.punishments.drop();
	}

	@Override
	public void close() {
		if (this.client != null) {
			this.client.close();
		}
	}

}
