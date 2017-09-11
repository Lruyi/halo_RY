package cn.e3.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3.order.utils.CookieUtils;
import cn.e3.user.service.UserService;
import cn.e3.utils.E3mallResult;
/**
 * 需求:判断用户是否登录
 * @author lry
 * 业务:
 * 	1. 从cookie中获取token,如果token存在,说明用户曾经登录过
 * 	2. 如果token不存在,跳转到登录页面登录,登录成功后回到历史页面,不放行
 * 	3. 如果token存在,根据token查询redis用户身份信息
 * 	4. 如果用户身份信息过期,跳转登录页面,重新登录,不放行
 * 	5. 登录成功,返回历史页面
 * 	6. 登录成功,把用户身份信息放到request域中,放行
 *
 */
public class LoginInterceptor implements HandlerInterceptor{
	
	//注入cookie中用户身份唯一标识
	@Value("${E3_TOKEN}")
	private String E3_TOKEN;
	
	//注入单点登录系统登录页面地址
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	//注入订单登录服务对象
	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 1. 从cookie中获取token
		String token = CookieUtils.getCookieValue(request, E3_TOKEN, true);
		//2. 判断cookie中token身份存在
		if (StringUtils.isBlank(token)) {
			//3. 说明未登录,跳转登录页面,重新登录,登录成功,回到历史页面
			//获取历史页面操作地址
			String hurl = request.getRequestURL().toString();
			//重定向到登录页面
			response.sendRedirect(SSO_URL+"?url="+hurl);
			
			//拦截
			return false;
		}
		
		//否则,token不为空,根据token查询redis用户身份信息
		E3mallResult result = userService.findUserWithToken(token);
		//判断redis中用户身份信息身份过期
		if (result.getStatus()!=200) {
			//表示用户身份信息过期
			//3. 说明未登录,跳转登录页面,重新登录,登录成功,回到历史页面
			//获取历史页面操作地址
			String hurl = request.getRequestURL().toString();
			//重定向到登录页面
			response.sendRedirect(SSO_URL+"?url="+hurl);
			//拦截
			return false;
		}
		
		//登录成功,把用户信息放到request域中
		request.setAttribute("user", result.getData());
		//放行
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
