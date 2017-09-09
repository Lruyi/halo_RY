package cn.e3.cart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jute.compiler.JRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;

import cn.e3.cart.jedis.JedisDao;
import cn.e3.cart.service.CartService;
import cn.e3.mapper.TbItemMapper;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbItemExample;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;
@Service
public class CartServiceImpl implements CartService {
	
	//注入jedisDao
	@Autowired
	private JedisDao jedisDao;
	
	//注入redis服务器中购物车唯一标识
	@Value("${REDIS_CART_KEY}")
	private String REDIS_CART_KEY;
	
	//注入redis服务器中购物车当中商品id排序唯一标识
	@Value("${REDIS_SORT_CART_KEY}")
	private String REDIS_SORT_CART_KEY;
	
	//注入商品接口的mapper接口代理对象
	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 需求:添加redis购物车
	 * @param id
	 * @param itemId
	 * @param num
	 * @returnE3mallResult
	 * 业务:
	 * 	1. 判断redis购物车里面是否有相同的商品
	 * 	2. 如果有,数量相加
	 * 	3. 没有,直接添加
	 * 	注意:要求后添加的商品,在购物车列表中先展示(最近添加的商品显示在前面)
	 */
	public E3mallResult addRedisCart(Long userId, Long itemId, Integer num) {
		
		// 1. 判断redis购物车里面是否有相同的商品
		Boolean hexists = jedisDao.hexists(REDIS_CART_KEY+":"+userId, itemId+"");
		//判断状态
		if (hexists) {
			//此时表示购物车中存在相同商品
			//获取商品数据
			String itemJson = jedisDao.hget(REDIS_CART_KEY+":"+userId, itemId+"");
			//把json格式数据转换成商品对象
			TbItem item = JsonUtils.jsonToPojo(itemJson, TbItem.class);
			//商品数据相加
			item.setNum(item.getNum()+num);
			
			//添加购物车并且给商品id排序
			this.addRedisWithSortCart(userId,item);
			
			/*//再放入购物车中
			jedisDao.hset(REDIS_CART_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(item));
			//获取当前时间毫秒
			Long currentTimeMillis = System.currentTimeMillis();
			//使用sorted-set 集合给添加商品id设置排序,根据时间(毫秒)排序
			jedisDao.zadd(REDIS_SORT_CART_KEY+":"+userId, currentTimeMillis.doubleValue(), itemId+"");
			*/
		}
		
		//如果没有相同商品
		if (!hexists) {
			//根据商品id查询新的商品
			TbItem item = itemMapper.selectByPrimaryKey(itemId);
			//设置购买数量
			item.setNum(num);
			
			//添加购物车并且给商品id排序
			this.addRedisWithSortCart(userId,item);
			
		}
		//默认返回status=200
		return E3mallResult.ok();
	}

	/**
	 * 抽取添加redis购物车方法
	 * @param userId
	 * @param item
	 */
	private void addRedisWithSortCart(Long userId, TbItem item) {
		
		//再放入购物车中
		jedisDao.hset(REDIS_CART_KEY+":"+userId, item.getId()+"", JsonUtils.objectToJson(item));
		//获取当前时间毫秒
		Long currentTimeMillis = System.currentTimeMillis();
		
		//使用sorted-set 集合给添加商品id设置排序,根据时间(毫秒)排序
		jedisDao.zadd(REDIS_SORT_CART_KEY+":"+userId, currentTimeMillis.doubleValue(), item.getId()+"");
		
	}

	/**
	 * 需求: 登录状态时把cookie购物车中的数据合并到redis购物车
	 * @param id
	 * @param cartList
	 * @return E3mallResult
	 */
	public E3mallResult mergeCart(Long userId, List<TbItem> cartList) {
		
		//循环购物车列表集合
		for (TbItem tbItem : cartList) {
			
			this.addRedisCart(userId, tbItem.getId(), tbItem.getNum());
		}
		
		
		return E3mallResult.ok();
	}

	/**
	 * 需求: 查询redis购物车所有商品数据,且进行有序的展示(后添加的先展示)
	 * @param userId
	 * @return List<TbItem>
	 * 业务:
	 * 	1. 先获取有序商品id集合
	 *  2. 根据有序商品id,获取购物车商品数据
	 */
	public List<TbItem> findRedisCartList(Long userId) {
		
		//创建商品集合List<TbItem> ,封装购物车商品数据
		List<TbItem> cartList = new ArrayList<TbItem>();
		
		//1. 先获取有序商品id集合
		Set<String> itemIds = jedisDao.zrevrange(REDIS_SORT_CART_KEY+":"+userId, 0l, -1l);
		//2. 循环集合获取每一个商品id
		for (String itemId : itemIds) {
			//3. 根据有序商品id,获取购物车商品数据
			String itemJson = jedisDao.hget(REDIS_CART_KEY+":"+userId, itemId);
			//把商品json格式转换成商品对象
			TbItem item = JsonUtils.jsonToPojo(itemJson, TbItem.class);
			
			//添加到商品集合中
			cartList.add(item);
		}
		
		return cartList;
	}

	/**
	 * 需求: 删除此用户下面此商品id对应的商品
	 * @param id
	 * @param itemId
	 * @return E3mallResult
	 * 业务:
	 * 	1. 先删除商品有序id
	 * 	2. 再删除redis购物车数据
	 */
	public E3mallResult deleteCart(Long userId, Long itemId) {
		
		//1. 先删除商品有序id
		jedisDao.zrem(REDIS_SORT_CART_KEY+":"+userId, itemId+"");
		//2. 再删除redis购物车数据
		jedisDao.hdel(REDIS_CART_KEY+":"+userId, itemId+"");
		return E3mallResult.ok();
	}

}
