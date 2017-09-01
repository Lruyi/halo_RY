package cn.e3.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SpringJedis {

	/**
	 * 需求:测试spring整合redis单机版
	 */
	@Test
	public void testSingleJedis(){
		//加载spring配置文件
		//classpath*:  因为配置文件在src/main/resources下而不是在src/test/resources,所以要加*
		ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-redis.xml");
		//获取连接池对象
		JedisPool jedisPool = app.getBean(JedisPool.class);
		//从连接池中获取jedis对象
		Jedis jedis = jedisPool.getResource();
		jedis.set("username", "张梓琳");
		String value = jedis.get("username");
		System.out.println(value);
	}
	
	
	/**
	 * 需求:测试spring整合redis集群版
	 */
	@Test
	public void testClusterJedis(){
		//加载spring配置文件
		//classpath*:  因为配置文件在src/main/resources下而不是在src/test/resources,所以要加*
		ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:applicationContext-redis.xml");
		//获取集群对象
		JedisCluster jedisCluster = app.getBean(JedisCluster.class);
		jedisCluster.set("username", "林心如");
		String value = jedisCluster.get("username");
		System.out.println(value);
	}
}
