package cn.e3.item.controller;

import javax.management.loading.MLet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3.manager.service.ItemService;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbItemDesc;

@Controller
public class ItemDetailController {
	
	//注入商品服务对象
	@Autowired
	private ItemService itemService;

	/**
	 * 需求:跳转到商品详情页面
	 * 请求:http://localhost:8086/${item.id }.html
	 * 参数:Long itemId
	 * 返回值:item.jsp
	 */
	@RequestMapping("{itemId}")
	public String showItem(@PathVariable Long itemId,Model model){
		//查询商品详情页面需要的数据
		//查询商品数据
		TbItem item = itemService.findItemByDI(itemId);
		//查询商品描述数据
		TbItemDesc itemDesc = itemService.findItemDescByID(itemId);
		
		//页面回显商品数据
		model.addAttribute("item", item);
		model.addAttribute("itemDesc", itemDesc);
		
		return "item";
	}
}
