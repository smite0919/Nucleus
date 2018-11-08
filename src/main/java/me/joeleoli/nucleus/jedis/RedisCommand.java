package me.joeleoli.nucleus.jedis;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {

	T execute(Jedis redis);

}
