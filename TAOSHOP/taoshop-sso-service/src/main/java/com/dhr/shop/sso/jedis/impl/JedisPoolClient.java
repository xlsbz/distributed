package com.dhr.shop.sso.jedis.impl;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 单机版实现类
 * 
 * @author Mr DU
 *
 */
public class JedisPoolClient implements JedisClient {

	// 将创建jedisPool对象权放到spring管理，注入
	@Autowired
	private JedisPool jedisPool;

	@Override
	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		String set = jedis.set(key, value);
		jedis.close();
		return set;
	}

	@Override
	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		String string = jedis.get(key);
		jedis.close();
		return string;
	}

	@Override
	public Boolean exists(String key) {
		Jedis jedis = jedisPool.getResource();
		Boolean flag = jedis.exists(key);
		jedis.close();
		return flag;
	}

	@Override
	public Long expire(String key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		Long expire = jedis.expire(key, seconds);
		jedis.close();
		return expire;
	}

	@Override
	public Long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		Long t = jedis.ttl(key);
		jedis.close();
		return t;
	}

	@Override
	public Long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		Long incr = jedis.incr(key);
		jedis.close();
		return incr;
	}

	@Override
	public Long hset(String key, String filed, String value) {
		Jedis jedis = jedisPool.getResource();
		Long hs = jedis.hset(key, filed, value);
		jedis.close();
		return hs;
	}

	@Override
	public String hget(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		String hs = jedis.hget(key, value);
		jedis.close();
		return hs;
	}

	@Override
	public Long del(String key, String... filed) {
		Jedis jedis = jedisPool.getResource();
		Long del = jedis.hdel(key, filed);
		jedis.close();
		return del;
	}

}
