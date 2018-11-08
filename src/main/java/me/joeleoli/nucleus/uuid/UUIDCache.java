package me.joeleoli.nucleus.uuid;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.util.AtomicString;

public class UUIDCache {

	private static Map<String, UUID> nameToUuid = new HashMap<>();
	private static Map<UUID, String> uuidToName = new HashMap<>();

	public static String getName(UUID uuid) {
		if (uuidToName.containsKey(uuid)) {
			return uuidToName.get(uuid);
		}

		// Fetch from database
		return "Unknown";
	}

	public static UUID getUuid(String name) {
		if (nameToUuid.containsKey(name.toLowerCase())) {
			return nameToUuid.get(name.toLowerCase());
		}

		AtomicString atomic = new AtomicString();

		Nucleus.getInstance().getNucleusJedis().runCommand(redis -> {
			atomic.setString(redis.hget("name-to-uuid", name.toLowerCase()));
			return null;
		});

		if (atomic.getString() == null) {
			return null;
		} else {
			return UUID.fromString(atomic.getString());
		}
	}

	public static void fetch() {
		Nucleus.getInstance().getNucleusJedis().runCommand((redis) -> {
			Map<String, String> cached = redis.hgetAll("name-to-uuid");

			if (cached == null || cached.isEmpty()) {
				return null;
			}

			Map<String, UUID> ntu = new HashMap<>();
			Map<UUID, String> utn = new HashMap<>();

			for (Map.Entry<String, String> entry : cached.entrySet()) {
				ntu.put(entry.getKey(), UUID.fromString(entry.getValue()));
				utn.put(UUID.fromString(entry.getValue()), entry.getKey());
			}

			nameToUuid = ntu;
			uuidToName = utn;

			return null;
		});
	}

	public static void update(String name, UUID uuid) {
		nameToUuid.put(name.toLowerCase(), uuid);
		uuidToName.put(uuid, name);

		Nucleus.getInstance().getNucleusJedis().runCommand((redis) -> {
			redis.hset("name-to-uuid", name.toLowerCase(), uuid.toString());
			redis.hset("uuid-to-name", uuid.toString(), name);
			return null;
		});
	}

}
