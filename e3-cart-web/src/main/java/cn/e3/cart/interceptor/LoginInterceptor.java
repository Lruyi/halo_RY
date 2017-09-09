package cn.e3.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3.cart.utils.CookieUtils;
import cn.e3.user.service.UserService;
import cn.e3.utils.E3mallResult;
/**
 * 需求:判断用户是否处于登录状态
 * 条件:
 * 	1. 未登录也可添加购物车,放行
 * 	2. 登录也可以添加购物车,放行
 * @author lry
 * 判断用户处于登录状态:
 * 	1. 如果cookie中存在token
 * 	2. 根据cookie查询redis中用户信息,且用户信息没过期,表示用户处于登录状态
 */
public class LoginInterceptor implements HandlerInterceptor{
	
	//注入用户登录cookie当中用户身份的唯一标识
	@Value("${E3_TOKEN}")
	private String E3_TOKEN;
	
	//注入用户服务对象
	@Autowired
	private UserService userService;
	

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		// 判断用户是否处于登录状态
		//1. 查看cookie中是否存在用户登录身份标识
		String token = CookieUtils.getCookieValue(request, E3_TOKEN, true);
		//2. 判断cookie中的token是否存在
		if (StringUtils.isNotBlank(token)) {
			//3. 根据token查询redis服务器
			E3mallResult result = userService.findUserWithToken(token);
			//4. 判断redis服务器中的用户身份信息是否过期
			if (result.getStatus()==200) {
				//5. 此时用户处于登录状态
				request.setAttribute("user", result.getData());
				return true;
			}
			
			
		}
		//否则,未登录状态,也可以添加购物车,也需要放行
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
