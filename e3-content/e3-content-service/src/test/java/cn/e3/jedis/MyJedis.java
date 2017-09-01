package cn.e3.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MyJedis {
	
	/**
	 * 需求:测试单机版redis服务
	 * 条件:不使用连接池
	 */
	@Test
	public void testSingleRedisWithOutJedisPool(){
		//创建jedis对象
		Jedis jedis = new Jedis("192.168.80.131", 6379);
		jedis.set("username", "哈哈");
		String value = jedis.get("username");
		System.out.println(value);
	}
	
	
	/**
	 * 需求:测试单机版redis服务
	 * 条件:使用连接池
	 */
	@Test
	public void testSingleRedisWithJedisPool(){
		//创建连接池配置对象
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		//设置最大连接数
		poolConfig.setMaxTotal(2000);
		poolConfig.setMaxIdle(20);
		
		//创建连接池对象
		JedisPool jp = new JedisPool(poolConfig, "192.168.80.131", 6379);
		//从连接池中获取jedis对象
		Jedis jedis = jp.getResource();
		jedis.set("username","凤姐喜欢我吗?");
		String value = jedis.get("username");
		System.out.println(value);
	}
	
	
	/**
	 * 需求:测试jedis连接redis集群服务器
	 */
	@Test
	public void tesClusterRedis(){
		//创建连接池配置对象
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		//设置最大连接数
		poolConfig.setMaxTotal(2000);
		poolConfig.setMaxIdle(20);
		
		//创建set集合封装集群节点
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		//添加节点
		nodes.add(new HostAndPort("192.168.80.131", 7001));
		nodes.add(new HostAndPort("192.168.80.131", 7002));
		nodes.add(new HostAndPort("192.168.80.131", 7003));
		nodes.add(new HostAndPort("192.168.80.131", 7004));
		nodes.add(new HostAndPort("192.168.80.131", 7005));
		nodes.add(new HostAndPort("192.168.80.131", 7006));
		nodes.add(new HostAndPort("192.168.80.131", 7007));
		nodes.add(new HostAndPort("192.168.80.131", 7008));
		
		//创建集群核心对象
		JedisCluster jcCluster = new JedisCluster(nodes, poolConfig);
		//向集群节点设置值 redis  CRC算法 随机
		jcCluster.set("username", "林心如");
		//获取集群节点地址
		String value = jcCluster.get("username");
		System.out.println(value);
	}
	
}
