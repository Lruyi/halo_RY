package cn.e3.cart.jedis.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3.cart.jedis.JedisDao;
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
		
		//hash   判断key filed 是否存在
		public Boolean hexists(String key, String field) {
			Boolean hexists = jCluster.hexists(key, field);
			return hexists;
		}
		
		//sorted-set  排序集合,添加排序元素
		public Long zadd(String key, Double score,String member) {
			Long zadd = jCluster.zadd(key, score, member);
			return zadd;
		}
		
		//从高到低获取有序的商品id
		public Set<String> zrevrange(String key, Long start, Long end) {
			Set<String> zrevrange = jCluster.zrevrange(key, start, end);
			return zrevrange;
		}
		
		//删除sorted-set 成员数据
		public Long zrem(String key, String member) {
			Long zrem = jCluster.zrem(key, member);
			return zrem;
		}

}
