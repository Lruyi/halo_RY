package cn.e3.order.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

import cn.e3.mapper.TbOrderItemMapper;
import cn.e3.mapper.TbOrderMapper;
import cn.e3.mapper.TbOrderShippingMapper;
import cn.e3.order.pojo.OrderInfo;
import cn.e3.order.service.OrderService;
import cn.e3.pojo.TbOrder;
import cn.e3.pojo.TbOrderItem;
import cn.e3.pojo.TbOrderShipping;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.IDUtils;
@Service
public class OrderServiceImpl implements OrderService {
	
	//注入订单mapper接口代理对象
	@Autowired
	private TbOrderMapper orderMapper;
	
	//注入订单明细mapper接口代理对象
	@Autowired
	private TbOrderItemMapper orderItemMapper;
	
	//注入订单收货人地址mapper接口代理对象
	@Autowired
	private TbOrderShippingMapper orderShippingMapper;

	/**
	 * 需求:保存订单数据
	 * 参数:OrderInfo
	 * 返回值:E3mallResult.ok(orderId)
	 * 思考:服务发布?
	 */
	public E3mallResult createOrder(OrderInfo orderInfo) {
		
		//保存订单数据
		//获取订单对象
		TbOrder orders = orderInfo.getOrders();
		//生成订单id 毫秒+时间
		Long orderId = IDUtils.genItemId();
		//设置到订单对象中
		orders.setOrderId(orderId+"");
		//邮费。精确到2位小数;单位:元。如:200.07，表示:200元7分
		orders.setPostFee("0");
		//状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
		orders.setStatus(1);
		//订单时间
		Date date = new Date();
		orders.setCreateTime(date);
		orders.setUpdateTime(date);
		//保存订单对象
		orderMapper.insert(orders);
		
		//保存订单明细
		//获取订单明细对象
		List<TbOrderItem> list = orderInfo.getOrderItems();
		//循环保存
		for (TbOrderItem tbOrderItem : list) {
			//生成订单明细id
			String id = UUID.randomUUID().toString();
			tbOrderItem.setId(id);
			//设置订单id
			tbOrderItem.setOrderId(orderId+"");
			
			//保存订单明细
			orderItemMapper.insert(tbOrderItem);
			
		}
		
		//保存收货人地址
		//获取收货人地址对象
		TbOrderShipping orderShipping = orderInfo.getOrderShipping();
		//设置订单id
		orderShipping.setOrderId(orderId+"");
		//设置时间
		orderShipping.setUpdated(date);
		orderShipping.setCreated(date);
		
		//保存收货人地址
		orderShippingMapper.insert(orderShipping);
		
		//返回订单id
		return E3mallResult.ok(orderId);
	}

}
