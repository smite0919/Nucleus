package me.joeleoli.nucleus.settings;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import me.joeleoli.nucleus.player.DefinedSetting;

@Data
public class Settings {

	private static Map<String, DefinedSetting> transformer = new HashMap<>();
	private static Map<DefinedSetting, Object> defaultSettings = new HashMap<>();

	private Map<DefinedSetting, Object> settings = new HashMap<>();

	public static void register(DefinedSetting definedSetting, Object value) {
		transformer.put(definedSetting.name(), definedSetting);
		defaultSettings.put(definedSetting, value);
	}

	public static DefinedSetting transform(String name) {
		return transformer.get(name);
	}

	public Object get(DefinedSetting definedSetting) {
		if (this.settings.containsKey(definedSetting)) {
			return this.settings.get(definedSetting);
		} else {
			this.settings.put(definedSetting, defaultSettings.get(definedSetting));
			return defaultSettings.get(definedSetting);
		}
	}

	public boolean getBoolean(DefinedSetting definedSetting) {
		return (boolean) this.get(definedSetting);
	}

}
