package cn.e3.order.service;

import cn.e3.order.pojo.OrderInfo;
import cn.e3.utils.E3mallResult;

public interface OrderService {

	/**
	 * 需求:保存订单数据
	 * 参数:OrderInfo
	 * 返回值:E3mallResult.ok(orderId)
	 */
	public E3mallResult createOrder(OrderInfo orderInfo);
}
