package cn.e3.search.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3.search.pojo.SolrPageBean;
import cn.e3.search.service.SearchItemService;

@Controller
public class SearchItemController {
	
	//注入搜索服务
	@Autowired
	private SearchItemService searchItemService;

	/**
	 * 需求:业务处理层,查询索引库数据
	 * 请求:http://localhost:8085/search.html?q=
	 * 参数:String qName,Integer page,Integer rows
	 * 返回值:search.jsp页面
	 * 方法:search
	 * 思考:服务是否引用?
	 */
	@RequestMapping("search")
	public String search(@RequestParam(value="q") String qName,
			@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue="60") Integer rows,
			Model model){
		
		//解决查询参数乱码
		try {
			qName = new String(qName.getBytes("ISO8859-1"),"UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//调用远程搜索服务,查询索引库数据
		SolrPageBean pageBean = searchItemService.findSolrIndexWithConditionPage(qName, page, rows);
		//回显查询参数
		model.addAttribute("query", qName);
		//回显当前页
		model.addAttribute("page", page);
		//回显总页码
		model.addAttribute("totalPages", pageBean.getTotalPages());
		//回显商品列表
		model.addAttribute("itemList", pageBean.getsList());
		
		return "search";
	}
}
