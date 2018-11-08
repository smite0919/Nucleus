package me.joeleoli.nucleus.jedis;

import com.google.gson.JsonObject;
import lombok.Getter;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.jedis.handler.NucleusPayload;
import me.joeleoli.nucleus.jedis.handler.NucleusSubscriptionHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Getter
public class NucleusJedis {

	private JedisSettings settings;
	private JedisPool pool;
	private JedisPublisher publisher;
	private JedisSubscriber subscriber;

	public NucleusJedis(JedisSettings settings) {
		this.settings = settings;
		this.pool = new JedisPool(this.settings.getAddress(), this.settings.getPort());

		try (Jedis jedis = this.pool.getResource()) {
			if (this.settings.hasPassword()) {
				jedis.auth(this.settings.getPassword());
			}

			this.publisher = new JedisPublisher(this.settings);
			this.subscriber = new JedisSubscriber("nucleus", this.settings, new NucleusSubscriptionHandler());
		}
	}

	public static NucleusJedis getInstance() {
		return Nucleus.getInstance().getNucleusJedis();
	}

	public boolean isActive() {
		return this.pool != null && !this.pool.isClosed();
	}

	public void write(NucleusPayload payload, JsonObject data) {
		JsonObject object = new JsonObject();

		object.addProperty("payload", payload.name());
		object.add("data", data == null ? new JsonObject() : data);

		this.publisher.write("nucleus", object);
	}

	public <T> T runCommand(RedisCommand<T> redisCommand) {
		Jedis jedis = this.pool.getResource();
		T result = null;

		try {
			result = redisCommand.execute(jedis);
		} catch (Exception e) {
			e.printStackTrace();

			if (jedis != null) {
				this.pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (jedis != null) {
				this.pool.returnResource(jedis);
			}
		}

		return result;
	}

}
