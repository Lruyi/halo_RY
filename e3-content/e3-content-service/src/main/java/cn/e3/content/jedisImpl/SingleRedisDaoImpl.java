/*package cn.e3.content.jedisImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import cn.e3.content.jedis.JedisDao;
@Repository
public class SingleRedisDaoImpl implements JedisDao {

	//注入连接池对象
	@Autowired
	private JedisPool jp;
	
	@Override
	public String set(String key, String value) {
		Jedis jedis = jp.getResource();
		String set = jedis.set(key, value);
		return set;
	}

	@Override
	public String get(String key) {
		Jedis jedis = jp.getResource();
		String value = jedis.get(key);
		return value;
	}

	@Override
	public Long hset(String key, String field, String value) {
		Jedis jedis = jp.getResource();
		Long hset = jedis.hset(key, field, value);
		return hset;
	}

	@Override
	public String hget(String key, String field) {
		Jedis jedis = jp.getResource();
		String value = jedis.hget(key, field);
		return value;
	}

	@Override
	public Long hdel(String key, String fields) {
		Jedis jedis = jp.getResource();
		Long hdel = jedis.hdel(key, fields);
		return hdel;
	}

	@Override
	public Long expire(String key, int seconds) {
		Jedis jedis = jp.getResource();
		Long expire = jedis.expire(key, seconds);
		return expire;
	}

	@Override
	public Long ttl(String key) {
		Jedis jedis = jp.getResource();
		Long ttl = jedis.ttl(key);
		return ttl;
	}

}
*/