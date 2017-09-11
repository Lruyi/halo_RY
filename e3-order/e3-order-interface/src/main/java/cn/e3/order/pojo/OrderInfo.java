package cn.e3.order.pojo;

import java.io.Serializable;
import java.util.List;

import cn.e3.pojo.TbOrder;
import cn.e3.pojo.TbOrderItem;
import cn.e3.pojo.TbOrderShipping;

public class OrderInfo implements Serializable{

	private TbOrder orders;
	
	//订单明细
	private List<TbOrderItem>  orderItems;
	
	//收货人地址
	private TbOrderShipping orderShipping;

	public TbOrder getOrders() {
		return orders;
	}

	public void setOrders(TbOrder orders) {
		this.orders = orders;
	}

	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}

	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
	
	
}
