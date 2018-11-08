package me.joeleoli.nucleus;

import lombok.Getter;
import me.joeleoli.nucleus.config.ConfigCursor;
import me.joeleoli.nucleus.server.ServerType;
import me.joeleoli.nucleus.util.Style;

@Getter
public class NucleusConfig {

	private String serverId;
	private ServerType serverType;
	private String serverMotd;

	public void load() {
		ConfigCursor cursor = new ConfigCursor(Nucleus.getInstance().getMainFileConfig(), "server");

		this.serverId = cursor.getString("id");
		this.serverType = ServerType.valueOf(cursor.getString("type"));
		this.serverMotd = Style.translate(cursor.getString("motd"));

		cursor.setPath("settings");
	}

}
