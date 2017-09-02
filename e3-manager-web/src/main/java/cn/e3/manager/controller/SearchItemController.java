package cn.e3.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.search.service.SearchItemService;
import cn.e3.utils.E3mallResult;

@Controller
public class SearchItemController {

	//注入SearchItemService
	@Autowired
	private SearchItemService searchItemService;
	
	/**
	 * 需求:查询数据库数据,把数据导入索引库
	 * 请求:/search/dataImport
	 * 参数:无
	 * 返回值:json格式E3mallResult
	 * 思考:服务引用没?
	 */
	@RequestMapping("/search/dataImport")
	@ResponseBody
	public E3mallResult dataImport(){
		//调用远程搜索服务,导入索引库数据
		E3mallResult result = searchItemService.importSolrIndexWithDatabase();
		return result;
	}
}
