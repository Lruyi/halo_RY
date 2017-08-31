package cn.e3.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	
	/**
	 * 需求：页面跳转
	 * 请求:
	 * 	首页:localhost:8081/index
	 * 	商品添加:localhost:8081/item-add
	 * 	商品编辑:localhost:8081/itme-edit
	 * 	商品列表:localhost:8081/item-list
	 */
	@RequestMapping("{page}")
	public String showPage(@PathVariable String page){
		
		return page;
	}

}
