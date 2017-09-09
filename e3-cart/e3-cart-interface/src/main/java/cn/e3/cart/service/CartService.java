package cn.e3.cart.service;

import java.util.List;

import cn.e3.pojo.TbItem;
import cn.e3.utils.E3mallResult;

public interface CartService {

	/**
	 * 需求:添加redis购物车
	 * @param id
	 * @param itemId
	 * @param num
	 * @returnE3mallResult
	 */
	E3mallResult addRedisCart(Long userId, Long itemId, Integer num);

	/**
	 * 需求: 登录状态时把cookie购物车中的数据合并到redis购物车
	 * @param id
	 * @param cartList
	 * @return E3mallResult
	 */
	E3mallResult mergeCart(Long userId, List<TbItem> cartList);

	/**
	 * 需求: 查询redis购物车所有商品数据,且进行有序的展示(后添加的先展示)
	 * @param userId
	 * @return List<TbItem>
	 */
	List<TbItem> findRedisCartList(Long userId);

	/**
	 * 需求: 删除此用户下面此商品id对应的商品
	 * @param id
	 * @param itemId
	 * @return E3mallResult
	 */
	E3mallResult deleteCart(Long userId, Long itemId);

}
