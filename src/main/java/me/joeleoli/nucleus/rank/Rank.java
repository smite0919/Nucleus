package me.joeleoli.nucleus.rank;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.jedis.handler.NucleusPayload;
import me.joeleoli.nucleus.json.JsonChain;
import me.joeleoli.nucleus.player.NucleusPlayer;
import me.joeleoli.nucleus.server.ServerType;
import me.joeleoli.nucleus.util.Style;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Rank implements Comparable<Rank> {

	@Getter
	private static List<Rank> ranks = new ArrayList<>();

	private ServerType serverType;
	private String name;
	private String prefix = Style.WHITE;
	private String color = Style.WHITE;
	private boolean global;
	private int weight;
	private List<String> permissions = new ArrayList<>();
	private List<String> inherits = new ArrayList<>();

	public Rank(String name) {
		this.name = name;

		ranks.add(this);
	}

	public static Rank getRankByName(String name) {
		for (Rank rank : ranks) {
			if (rank.getName().equalsIgnoreCase(name)) {
				return rank;
			}
		}

		return null;
	}

	public static Rank getDefaultRank() {
		for (Rank rank : ranks) {
			if (rank.getName().equalsIgnoreCase("Default")) {
				return rank;
			}
		}

		return null;
	}

	public String getRankInfo() {
		return this.getColoredName() + Style.RESET + " (" + (this.isGlobal() ? "G" : this.getServerType().name()) +
		       ") (W: " + this.getWeight() + ")";
	}

	public String getColoredName() {
		return Style.RESET + this.color + this.name;
	}

	public void setColor(String color) {
		this.color = color;

		Bukkit.getOnlinePlayers().forEach(player -> {
			final NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

			if (nucleusPlayer.getActiveRank().equals(this)) {
				nucleusPlayer.refreshDisplayName();
			}
		});
	}

	public List<String> getEffectivePermissions() {
		List<String> permissions = new ArrayList<>();

		permissions.addAll(this.permissions);

		this.inherits.forEach(inheritName -> {
			Rank rank = Rank.getRankByName(inheritName);

			if (rank != null) {
				permissions.addAll(rank.getEffectivePermissions());
			}
		});

		return permissions;
	}

	public void load(Document document) {
		if (document == null) {
			ranks.remove(this);
			return;
		}

		List<String> permissions = new ArrayList<>();
		List<String> inherits = new ArrayList<>();

		for (JsonElement element : new JsonParser().parse(document.getString("permissions")).getAsJsonArray()) {
			permissions.add(element.getAsString());
		}

		for (JsonElement element : new JsonParser().parse(document.getString("inherits")).getAsJsonArray()) {
			inherits.add(element.getAsString());
		}

		this.prefix = Style.translate(document.getString("prefix"));
		this.color = Style.translate(document.getString("color"));
		this.global = document.getBoolean("global");
		this.weight = document.getInteger("weight");
		this.permissions = permissions;
		this.inherits = inherits;

		if (!this.global) {
			this.serverType = ServerType.valueOf(document.getString("server_type"));
		}
	}

	public void save() {
		this.save(true);
	}

	public void save(boolean broadcast) {
		JsonArray permissions = new JsonArray();

		for (String permission : this.permissions) {
			permissions.add(permission);
		}

		Document document = new Document();
		JsonArray inherits = new JsonArray();

		this.inherits.forEach(inherits::add);

		if (!this.global) {
			document.put("server_type", this.serverType.name());
		}

		document.put("name", this.name);
		document.put("prefix", this.prefix.replace(ChatColor.COLOR_CHAR, '&'));
		document.put("color", this.color.replace(ChatColor.COLOR_CHAR, '&'));
		document.put("global", this.global);
		document.put("weight", this.weight);
		document.put("permissions", permissions.toString());
		document.put("inherits", inherits.toString());

		Nucleus.getInstance().getNucleusMongo().replaceRank(this, document);

		if (broadcast) {
			Nucleus.getInstance().getNucleusJedis().write(
					NucleusPayload.RANK_UPDATE,
					new JsonChain()
							.addProperty("rank_name", this.name)
							.get()
			);
		}
	}

	public void delete(boolean updateDatabase) {
		for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
			NucleusPlayer nucleusPlayer = NucleusPlayer.getByUuid(player.getUniqueId());

			if (nucleusPlayer.getActiveRank().equals(this) ||
			    (nucleusPlayer.getGlobalRank() != null && nucleusPlayer.getGlobalRank().equals(this))) {
				nucleusPlayer.setRank(Rank.getDefaultRank());
				player.sendMessage(
						Style.GREEN + "Your rank has been deleted and you have been given the default rank.");
			}

			nucleusPlayer.getServerRanks().entrySet().removeIf(entry -> entry.getValue().equals(this));
		}

		Rank.getRanks().remove(this);

		if (updateDatabase) {
			Nucleus.getInstance().getNucleusMongo().deleteRank(this);
		}

		Nucleus.getInstance().getNucleusJedis()
		       .write(NucleusPayload.RANK_DELETE, new JsonChain().addProperty("rank_name", this.name).get());
	}

	@Override
	public int compareTo(Rank other) {
		return Integer.compare(this.weight, other.weight);
	}

}
