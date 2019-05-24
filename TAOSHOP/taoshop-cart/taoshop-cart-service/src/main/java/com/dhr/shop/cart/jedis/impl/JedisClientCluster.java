package com.dhr.shop.cart.jedis.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.JedisCluster;

/**
 * 集群实现
 * 
 * @author Mr DU
 *
 */
public class JedisClientCluster implements JedisClient {

	// 注入集群对象
	@Autowired
	private JedisCluster cluster;

	@Override
	public String set(String key, String value) {
		return cluster.set(key, value);
	}

	@Override
	public String get(String key) {
		return cluster.get(key);
	}

	@Override
	public Boolean exists(String key) {
		return cluster.exists(key);
	}

	@Override
	public Long expire(String key, int seconds) {
		return cluster.expire(key, seconds);
	}

	@Override
	public Long ttl(String key) {
		return cluster.ttl(key);
	}

	@Override
	public Long incr(String key) {
		return cluster.incr(key);
	}

	@Override
	public Long hset(String key, String filed, String value) {
		return cluster.hset(key, filed, value);
	}

	@Override
	public String hget(String key, String value) {
		return cluster.hget(key, value);
	}

	@Override
	public Long del(String key, String... filed) {
		return cluster.hdel(key, filed);
	}

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Map<String, String> getAll(String key) {
		return cluster.hgetAll(key);
	}

}
