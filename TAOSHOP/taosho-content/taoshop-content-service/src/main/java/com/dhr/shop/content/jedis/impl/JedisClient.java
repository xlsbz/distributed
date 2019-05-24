package com.dhr.shop.content.jedis.impl;

/**
 * 定义jedis接口
 * 
 * @author Mr DU
 *
 */
public interface JedisClient {
	// 设置
	String set(String key, String value);

	// 获取
	String get(String key);

	// 是否存在
	Boolean exists(String key);

	// 过期时间
	Long expire(String key, int seconds);

	// 剩余时间
	Long ttl(String key);

	// 自增
	Long incr(String key);

	// 设置hash
	Long hset(String key, String filed, String value);

	// 获取hash
	String hget(String key, String value);

	// 删除
	Long del(String key, String... filed);

}
