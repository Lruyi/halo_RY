package cn.e3.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.pojo.TbUser;
import cn.e3.sso.utils.CookieUtils;
import cn.e3.user.service.UserService;
import cn.e3.utils.E3mallResult;

@Controller
public class UserController {
	
	//注入用户服务对象
	@Autowired
	private UserService userService;
	
	//注入cookie中用户身份唯一标识
	@Value("${E3_TOKEN}")
	private String E3_TOKEN;
	
	//注入cookie中用户身份唯一标识过期时间
	@Value("${E3_TOKEN_EXPIRE_TIME}")
	private Integer E3_TOKEN_EXPIRE_TIME;
	
	
	/**
	 * 需求:校验数据是否可用.(校验用户名,手机号,邮箱是否被占用)
	 * 请求: user/check/{param}/{type}
	 * 参数:String param,Integer type
	 * 返回值:
	 * {
		status: 200 //200 成功
		msg: "OK" // 返回信息消息
		data: false // 返回数据，true：数据可用，false：数据不可用
		}
		业务:
		type=1: param=username
		type=2: param=phone
		type=3: param=email
	     思考: 服务是否引用?
	 */
	@RequestMapping("user/check/{param}/{type}")
	@ResponseBody
	public E3mallResult dataCheck(@PathVariable String param,
			@PathVariable Integer type){
		//调用远程service服务
		E3mallResult result = userService.dataCheck(param, type);
		return result;
	}
	
	/**
	 * 需求:用户注册
	 * 请求:/user/register
	 * 参数:TbUser user
	 * 返回值:
	 * 成功时:
	 * {
		status: 200
		msg: "注册成功!"
		data: null
		}

	 * 失败时:
	 * {
		status: 400
		msg: "注册失败. 请校验数据后请再提交数据."
		data: null
		}
	 */
	@RequestMapping("/user/register")
	@ResponseBody
	public E3mallResult register(TbUser user){
		
		//调用远程服务
		E3mallResult register = userService.register(user);
		return register;
	}
	
	/**
	 * 需求:用户登录
	 * 请求:/user/login
	 * 参数:String username,String password
	 * 返回值:
	 * 成功时:
	 * {
		status: 200
		msg: "OK"
		data: "fe5cb546aeb3ce1bf37abcb08a40493e" //登录成功，返回token
	   }
	 * 失败时:
	 * {
		status: 201
		msg: "登录失败!"
		data:null
	   }
	   业务:
	 1,获取服务层返回token
	 2,把token写入cookie中
	 */
	@RequestMapping("/user/login")
	@ResponseBody
	public E3mallResult login(String username, String password,
			HttpServletRequest request, HttpServletResponse response){
		//调用远程service服务
		E3mallResult result = userService.login(username, password);
		//获取token
		String token = result.getData().toString();
		//把token放到cookie中
		CookieUtils.setCookie(request, response, E3_TOKEN, token, E3_TOKEN_EXPIRE_TIME, true);
		
		return result;
	}
	
	/**
	 * 需求:根据token查询redis服务器用户唯一session
	 * 请求:/user/token/{token}
	 * 参数:String token
	 * 返回值:E3mallResult.ok(user)
	 * 过期时间重置:
	 * 1,重新根据token查询用户身份信息'
	 * 2,重新访问系统,过期时间重新设置
	 */
	@RequestMapping("/user/token/{token}")
	@ResponseBody
	public Object findUserWithToken(@PathVariable String token,String callback){
		//调用service服务,查询用户身份信息
		E3mallResult result = userService.findUserWithToken(token);
		
		//判断ajax请求是否是跨域的
		if (StringUtils.isBlank(callback)) {
			//直接返回
			return result;
		}
		//否则就是跨域请求
		//return "callback("++")"
		//Jackson支持跨域设置
		MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
		//设置跨域函数
		mappingJacksonValue.setJsonpFunction(callback);
		
		//返回
		return mappingJacksonValue;
		
	}

}
