package cn.e3.user.jedis.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3.user.jedis.JedisDao;
import redis.clients.jedis.JedisCluster;
@Repository
public class ClusterRedisDaoImpl implements JedisDao {

		// 注入集群对象
		@Autowired
		private JedisCluster jCluster;

		@Override
		public String set(String key, String value) {
			String str = jCluster.set(key, value);
			return str;
		}

		@Override
		public String get(String key) {
			String str = jCluster.get(key);
			return str;
		}

		@Override
		public Long hset(String key, String field, String value) {
			Long hset = jCluster.hset(key, field, value);
			return hset;
		}

		@Override
		public String hget(String key, String field) {
			String str = jCluster.hget(key, field);
			return str;
		}

		@Override
		public Long hdel(String key, String fields) {
			Long hdel = jCluster.hdel(key, fields);
			return hdel;
		}

		@Override
		public Long expire(String key, int seconds) {
			Long expire = jCluster.expire(key, seconds);
			return expire;
		}

		@Override
		public Long ttl(String key) {
			Long ttl = jCluster.ttl(key);
			return ttl;
		}

}
