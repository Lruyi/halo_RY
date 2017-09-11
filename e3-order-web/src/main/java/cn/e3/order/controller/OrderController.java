package cn.e3.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3.cart.service.CartService;
import cn.e3.order.pojo.OrderInfo;
import cn.e3.order.service.OrderService;
import cn.e3.order.utils.CookieUtils;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbUser;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;

@Controller
public class OrderController {
	
	//注入购物车服务对象
	@Autowired
	private CartService cartService;
	
	//注入cookie购物车唯一标识
	@Value("${COOKIE_CART}")
	private String COOKIE_CART;
	
	//注入订单服务对象
	@Autowired
	private OrderService orderService;

	/**
	 * 需求: 去到订单结算页面
	 * 请求: http://localhost:8092/order/order-cart.html
	 * 参数: 无
	 * 返回值: 订单结算页面 order-cart.jsp
	 */
	@RequestMapping("/order/order-cart")
	public String orderCart(HttpServletRequest request, HttpServletResponse response){
		//从request域中获取用户身份信息
		TbUser user = (TbUser) request.getAttribute("user");
		//1,初始化收货人地址(默认)
		
		//2,初始化付款方式(货到付款)
		
		//先获取cookie购物车列表,查询cookie购物车是否有商品,如果有,必须进行合并
		List<TbItem> cookieCartList = this.getCookieCartList(request);
		//判断此购物车是否为空
		if (!cookieCartList.isEmpty()) {
			//如果不为空,合并购物车
			E3mallResult result = cartService.mergeCart(user.getId(), cookieCartList);
			//清空cookie
			CookieUtils.setCookie(request, 
					response, 
					COOKIE_CART, 
					"", 
					0, 
					true);
		}
		
		//3,送货清单(购物车购买清单)
		List<TbItem> cartList = cartService.findRedisCartList(user.getId());
		//把购物车商品清单放到redis域中
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	
	/**
	 * 需求:保存订单数据
	 * 请求:/order/create.html
	 * 参数:OrderInfo orderInfo
	 * 返回值:success
	 * 服务引用?
	 */
	@RequestMapping("/order/create")
	public String createOrder(OrderInfo orderInfo,Model model){
		
		//调用订单远程服务方法,创建订单
		E3mallResult result = orderService.createOrder(orderInfo);
		
		//页面回显订单号
		model.addAttribute("orderId", result.getData().toString());
		//回显支付金额
		model.addAttribute("payment", orderInfo.getOrders().getPayment());
		
		//商品送达时间: 3天后
		DateTime date = new DateTime();
		DateTime days = date.plusDays(3);
		model.addAttribute("date", days);
		
		//返回订单成功页面
		return "success";
	}
	
	
	/**
	 * 获取cookie中的购物车列表
	 * @param request
	 * @return
	 */
	private List<TbItem> getCookieCartList(HttpServletRequest request) {
		
		// 
		String cartJson = CookieUtils.getCookieValue(request, COOKIE_CART, true);
		//判断cookie购物车中是否有数据
		if (StringUtils.isBlank(cartJson)) {
			//返回一个空的购物车列表
			return new ArrayList<TbItem>();
		}
		
		//否则,购物车中有值
		//把json格式的数据转换成list集合数据
		List<TbItem> cartList = JsonUtils.jsonToList(cartJson, TbItem.class);
		return cartList;
	}
}
