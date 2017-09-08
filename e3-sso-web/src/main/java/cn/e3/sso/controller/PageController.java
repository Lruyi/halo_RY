package cn.e3.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.pojo.TbUser;
import cn.e3.user.service.UserService;
import cn.e3.utils.E3mallResult;

@Controller
public class PageController {
	
	//注入userService
	@Autowired
	private UserService userService;

	/**
	 * 需求:用户注册
	 */
	@RequestMapping("/page/register")
	public String register(){
		//远程调用service服务对象
		return "register";
	}
	
	/**
	 * 需求:用户登录
	 */
	@RequestMapping("/page/login")
	public String login(){
		return "login";
	}
}
