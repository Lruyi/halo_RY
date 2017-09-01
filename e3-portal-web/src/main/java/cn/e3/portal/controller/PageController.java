package cn.e3.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

	/**
	 * 需求:跳转门户首页
	 */
	@RequestMapping("index")
	public String showIndex(){
		return "index";
	}
	
}