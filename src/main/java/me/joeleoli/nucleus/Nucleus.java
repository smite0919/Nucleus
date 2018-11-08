package me.joeleoli.nucleus;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.nucleus.board.BoardManager;
import me.joeleoli.nucleus.chat.ChatManager;
import me.joeleoli.nucleus.command.CommandHandler;
import me.joeleoli.nucleus.config.ConfigCursor;
import me.joeleoli.nucleus.config.FileConfig;
import me.joeleoli.nucleus.jedis.JedisSettings;
import me.joeleoli.nucleus.jedis.NucleusJedis;
import me.joeleoli.nucleus.listener.ListenerHandler;
import me.joeleoli.nucleus.mongo.NucleusMongo;
import me.joeleoli.nucleus.player.DefinedSetting;
import me.joeleoli.nucleus.settings.Settings;
import me.joeleoli.nucleus.rank.Rank;
import me.joeleoli.nucleus.rave.Rave;
import me.joeleoli.nucleus.server.ServerType;
import me.joeleoli.nucleus.task.InsertLogsTask;
import me.joeleoli.nucleus.task.MenuUpdateTask;
import me.joeleoli.nucleus.task.ShutdownTask;
import me.joeleoli.nucleus.task.UpdateCacheTask;
import me.joeleoli.nucleus.util.ItemUtil;
import me.joeleoli.nucleus.util.Style;
import me.joeleoli.nucleus.util.TaskUtil;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Nucleus extends JavaPlugin {

	public static final Random RANDOM = new Random();

	@Getter
	private static Nucleus instance;

	private FileConfig mainFileConfig;

	private NucleusConfig nucleusConfig;
	private NucleusMongo nucleusMongo;
	private NucleusJedis nucleusJedis;

	@Setter
	private ShutdownTask shutdownTask;

	private ChatManager chatManager;
	private BoardManager boardManager;

	@Setter
	private Rave rave;

	private boolean loaded;

	@Override
	public void onEnable() {
		instance = this;

		this.mainFileConfig = new FileConfig(this, "config.yml");
		this.nucleusConfig = new NucleusConfig();
		this.nucleusConfig.load();

		final ConfigCursor cursor = new ConfigCursor(this.mainFileConfig, null);

		final JedisSettings settings = new JedisSettings(
				cursor.getString("redis.host"),
				cursor.getInt("redis.port"),
				cursor.getString("redis.password")
		);

		this.nucleusMongo = new NucleusMongo();
		this.nucleusJedis = new NucleusJedis(settings);

		this.chatManager = new ChatManager();

		this.loadRanks();

		Settings.register(DefinedSetting.GlobalPlayerSetting.RECEIVE_PRIVATE_MESSAGES, true);
		Settings.register(DefinedSetting.GlobalPlayerSetting.RECEIVE_GLOBAL_MESSAGES, true);
		Settings.register(DefinedSetting.GlobalPlayerSetting.PLAY_MESSAGE_SOUNDS, true);

		ItemUtil.load();

		CommandHandler.init();
		CommandHandler.loadCommandsFromPackage(this, "me.joeleoli.nucleus.command.commands");
		ListenerHandler.loadListenersFromPackage(this, "me.joeleoli.nucleus.listener");
		ListenerHandler.loadListenersFromPackage(this, "me.joeleoli.nucleus.menu");

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getScheduler().runTaskTimer(this, new InsertLogsTask(), 20L * 60L * 2L, 20L * 60L * 2L);
		this.getServer().getScheduler().runTaskTimer(this, new UpdateCacheTask(), 0L, 20L * 60L * 2L);
		this.getServer().getScheduler().runTaskTimer(this, new MenuUpdateTask(), 20L, 20L);

		TaskUtil.runLater(() -> this.loaded = true, 60L);
	}

	private void loadRanks() {
		try (MongoCursor<Document> cursor = Nucleus.getInstance().getNucleusMongo().getRanks()) {
			cursor.forEachRemaining(document -> {
				List<String> permissions = new ArrayList<>();

				for (JsonElement element : new JsonParser().parse(document.getString("permissions")).getAsJsonArray()) {
					permissions.add(element.getAsString());
				}

				Rank rank = new Rank(document.getString("name"));

				rank.setPrefix(Style.translate(document.getString("prefix")));
				rank.setColor(Style.translate(document.getString("color")));
				rank.setGlobal(document.getBoolean("global"));
				rank.setWeight(document.getInteger("weight"));
				rank.setPermissions(permissions);

				if (document.containsKey("inherits")) {
					List<String> inherits = new ArrayList<>();

					for (JsonElement element : new JsonParser().parse(document.getString("inherits"))
					                                           .getAsJsonArray()) {
						inherits.add(element.getAsString());
					}

					rank.setInherits(inherits);
				}

				if (!rank.isGlobal()) {
					rank.setServerType(ServerType.valueOf(document.getString("server_type")));
				}
			});
		}

		if (Rank.getDefaultRank() == null) {
			Rank rank = new Rank("Default");

			rank.setColor(Style.WHITE);
			rank.setPrefix(Style.WHITE);
			rank.setGlobal(true);
			rank.setWeight(-1);
			rank.setPermissions(new ArrayList<>());
			rank.save();
		}
	}

	public void setBoardManager(BoardManager manager) {
		this.boardManager = manager;
		this.boardManager.runTaskTimerAsynchronously(this, manager.getAdapter().getInterval(), manager.getAdapter().getInterval());
	}

}
