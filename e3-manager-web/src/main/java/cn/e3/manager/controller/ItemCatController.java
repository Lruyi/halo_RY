package cn.e3.manager.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.manager.service.ItemCatService;
import cn.e3.utils.TreeNode;

@Controller
public class ItemCatController {
	
	//注入service
	@Autowired
	private ItemCatService itemCatService;

	/**
	 * 需求:根据父id查询此节点下子节点
	 * 请求:/item/cat/list   (在jslib/common.js中找)
	 * 参数:Long parentId
	 * 返回值:List<TreeNode>
	 * 思考:服务是否发布?
	 * 		需要发布,要在service和web分别配置发布服务和引用服务
	 * 业务分析:
	 * 	1. 前端框架easyUI  treeNode传递参数 :   id
	 * 	2. 初始化顶级节点parent_id=0
	 */
	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<TreeNode> findItemCatWtihTreeNodeList(@RequestParam(value="id",defaultValue="0") Long patentId){
		//调用远程service服务对象
		List<TreeNode> treeNodeList = itemCatService.findItemCatWithTreeNodeList(patentId);
		return treeNodeList;
	}
	
}
