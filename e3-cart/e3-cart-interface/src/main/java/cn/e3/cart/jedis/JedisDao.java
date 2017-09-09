package cn.e3.cart.jedis;

import java.util.Set;

public interface JedisDao {

	// 抽取jedis常用方法
	// String类型
	public String set(String key, String value);

	// String get
	public String get(String key);

	// hash类型
	public Long hset(String key, String field, String value);

	// hash get
	public String hget(String key, String field);

	// hash delete
	public Long hdel(String key, String fields);

	// 设置数据过期
	public Long expire(String key, int seconds);

	// 测试过期时间过程
	public Long ttl(String key);
	
	//hash   判断key filed 是否存在
	public Boolean hexists(String key, String field);
	
	//sorted-set  排序集合,添加排序元素
	public Long zadd(String key, Double score,String member);
	
	//从高到低获取有序的商品id
	public Set<String> zrevrange(String key, Long start, Long end);
	
	//删除sorted-set 成员数据
	public Long zrem(String key, String member);
	
}
