package cn.e3.portal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3.content.service.ContentService;
import cn.e3.utils.AdItem;
import cn.e3.utils.JsonUtils;

@Controller
public class PageController {
	
	//注入service
	//1. 引入内容服务接口
	//2. 引入内容服务 dubbo
	@Autowired
	private ContentService contentService;
	
	//输入大广告为分类id
	@Value("${BIG_AD_CATEGORY_ID}")
	private Long BIG_AD_CATEGORY_ID;

	/**
	 * 需求:跳转到门户系统首页
	 * 业务:
	 * 跳转到首页之前,进行一些初始化工作
	 * 1,加载左侧菜单
	 * 2,加载大广告位信息
	 * 3,加载小广告位信息
	 * 4,加载商品楼层列表
	 * 5,商品快报
	 */
	@RequestMapping("index")
	public String showIndex(Model model){
		
		//1,加载左侧菜单  分类id=87
		//2,加载大广告位信息 id=89
		//3,加载小广告位信息 id=90
		//4,加载商品楼层列表 id=91
		//5,商品快报 id=92

		//查询大广告位信息,轮播图展示
		List<AdItem> adList = contentService.findContentAdItemWithCategoryId(BIG_AD_CATEGORY_ID);
		//把广告数据集合转成json格式
		String adJson = JsonUtils.objectToJson(adList);
		//把广告放入model中
		model.addAttribute("ad1", adJson);
		
		return "index";
	}
	
}
