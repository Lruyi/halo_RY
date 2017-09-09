package cn.e3.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.support.Parameter;

import cn.e3.cart.service.CartService;
import cn.e3.cart.utils.CookieUtils;
import cn.e3.manager.service.ItemService;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbUser;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;

@Controller
public class CartController {

	//注入购物车服务
	@Autowired
	private CartService cartService;
	
	//注入cookie中购物车的唯一标识
	@Value("${COOKIE_CART}")
	private String COOKIE_CART;
	
	//注入商品服务
	@Autowired
	private ItemService itemService;
	
	//注入cookie购物车过期时间
	@Value("${COOKIE_CART_EXPIRE_TIME}")
	private Integer COOKIE_CART_EXPIRE_TIME;
	
	/**
	 * 需求:添加购物车
	 * 请求:/cart/add/150451608269030.html?num=7
	 * 参数:Long itemId,Integer num
	 * 返回值:cartSuccess
	 * 业务:
	 * 	1. 登录状态
	 * 	> 判断购物车中是否有相同的商品
	 * 	> 如果有,商品数量相加
	 * 	> 没有,商品直接添加
	 * 	2. 未登录状态
	 * 	> 获取cookie中的购物车列表
	 * 	> 判断购物车是否有相同商品
	 *  > 如果有,商品数量相加
	 *  > 没有,直接添加即可
	 */
	@RequestMapping("/cart/add/{itemId}")
	public String addCart(@PathVariable Long itemId,Integer num,
			HttpServletRequest request,HttpServletResponse response){
		
		//从request域中获取用户身份信息,判断用户是否处于登录状态
		TbUser user = (TbUser) request.getAttribute("user");
		//判断用户是否登录
		if (user!=null) {
			//用户此时处于登录状态,添加redis购物车
			E3mallResult result = cartService.addRedisCart(user.getId(),itemId,num);
			
			//返回购物车成功页面
			return "cartSuccess";
		}
		
		//未登录
		//先获取cookie中的购物车列表
		List<TbItem> cartList = this.getCookieCartList(request);
		
		//定义一个标识
		boolean flag = false;
		
		//判断此购物车列表中是否存在相同的列表
		for (TbItem tbItem : cartList) {
			//如果添加的商品id和cookie购物车中商品id相等,表示有相同商品
			if (tbItem.getId()==itemId.longValue()) {
				//商品数量相加
				tbItem.setNum(tbItem.getNum()+num);
				//设置标识
				flag = true;
				
				//结束循环
				break;
			}
		}
		
		//否则没有相同商品
		if (!flag) {
			//直接购买,查询新的商品信息
			TbItem item = itemService.findItemByDI(itemId);
			//设置购买数量
			item.setNum(num);
			//放入购物车列表
			cartList.add(item);
		}
		
		//把购物车列表写回到cookie购物车
		CookieUtils.setCookie(request, 
				response, 
				COOKIE_CART, 
				JsonUtils.objectToJson(cartList), 
				COOKIE_CART_EXPIRE_TIME, 
				true);
		
		
		//返回购物车成功页面
		return "cartSuccess";
	}
	
	
	/**
	 * 需求: 查询购物车列表,展示购物车清单
	 * 请求: /cart/cart.html
	 * 参数: 无
	 * 返回值:cart.jsp
	 * 业务:
	 * 	1. 登录状态(redis购物车)
	 * 	> 查询cookie购物车,如果cookie购物车有数据,需要合并购物车.把cookie购物车合并到redis
	 * 	> 清空cookie购物车数据
	 * 	> 查询redis购物车列表
	 * 	2. 未登录状态(cookie购物车)
	 * 	> 查询购物车列表即可
	 */
	@RequestMapping("/cart/cart")
	public String showCart(HttpServletRequest request,
			HttpServletResponse response){
		
		//从request域中获取用户身份信息,判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		
		//先获取cookie购物车列表
		List<TbItem> cartList = this.getCookieCartList(request);
		
		//判断用户是否登录
		if (user!=null) {
			//此时,用户处于登录状态
			//判断cookie购物车是否为空,如果不为空,合并购物车
			if (!cartList.isEmpty()) {
				//合并购物车
				E3mallResult result = cartService.mergeCart(user.getId(),cartList);
				//清空cookie购物车
				CookieUtils.setCookie(request, response, COOKIE_CART, "", 0, true);
				
			}
			
			//查询redis购物车
			cartList = cartService.findRedisCartList(user.getId());
		}
		
		//把购物车列表放到request域中,页面回显
		request.setAttribute("cartList", cartList);
		
		
		return "cart";
	}
	
	
	/**
	 * 需求: 删除购物车中不需要的商品数据
	 * 请求: /cart/delete/150459383829244.html
	 * 参数: Long itemId
	 * 返回值:return "redirect:cart/cart.html"
	 * 业务:
	 * 	1. 登录(删除redis购物车)
	 * 	2. 未登录(删除cookie购物车)
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCart(@PathVariable Long itemId,
			HttpServletRequest request,
			HttpServletResponse response){
		
		//从request域中获取用户身份信息,判断用户是否登录
		TbUser user = (TbUser) request.getAttribute("user");
		//判断用户是否处于登录状态
		if (user!=null) {
			//处于登录状态,删除redis购物车
			E3mallResult result = cartService.deleteCart(user.getId(),itemId);
			//跳转购物车列表
			return "redirect:/cart/cart.html";
		}
		
		//未登录状态
		//获取购物车列表
		List<TbItem> cartList = this.getCookieCartList(request);
		//判断应该删除哪个商品
		for (TbItem tbItem : cartList) {
			//如果需要删除的商品id等于集合列表的商品id,表示此商品需要被删除
			if (tbItem.getId()==itemId.longValue()) {
				//删除
				cartList.remove(tbItem);
				break;
			}
		}
		
		//把购物车列表写入到cookie购物车中
		CookieUtils.setCookie(request, 
				response, 
				COOKIE_CART, 
				JsonUtils.objectToJson(cartList),
				COOKIE_CART_EXPIRE_TIME, 
				true);
		
		return "redirect:/cart/cart.html";
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
