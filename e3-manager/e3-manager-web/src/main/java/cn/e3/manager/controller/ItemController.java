package cn.e3.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.manager.service.ItemService;
import cn.e3.pojo.TbItem;

@Controller
public class ItemController {
	
	//注入service
	@Autowired
	private ItemService itemService;

	/**
	 * 需求:根据id查询商品信息
	 * 请求:/item/{itemId}
	 * 参数:Long itemId
	 * 返回值:TbItem
	 * 方法:findItemByDI
	 */
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem findItemByDI(@PathVariable Long itemId){
		TbItem tbItem = itemService.findItemByDI(itemId);
		return tbItem;
	}
}
