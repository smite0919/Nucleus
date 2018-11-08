package me.joeleoli.nucleus.jedis;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import me.joeleoli.nucleus.Nucleus;
import org.apache.commons.lang3.Validate;

@RequiredArgsConstructor
public class JedisPublisher {

	private final JedisSettings jedisSettings;

	public void write(String channel, JsonObject payload) {
		Validate.notNull(Nucleus.getInstance().getNucleusJedis().getPool());

		Nucleus.getInstance().getNucleusJedis().runCommand(redis -> {
			if (jedisSettings.hasPassword()) {
				redis.auth(jedisSettings.getPassword());
			}

			redis.publish(channel, payload.toString());

			return null;
		});
	}

}
