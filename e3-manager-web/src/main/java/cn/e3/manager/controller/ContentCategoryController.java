package cn.e3.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3.content.service.ContentCategoryService;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.TreeNode;

@Controller
public class ContentCategoryController {

	/**
	 * 注入广告服务对象:
	 * 	1. 引入广告服务接口
	 * 	2. dubbo引入服务
	 */
	@Autowired
	private ContentCategoryService contentCategoryService;
	
	
	/**
	 * 需求:根据父id查询树形菜单(门户系统分页分类系统)子节点
	 * 请求:/content/category/list
	 * 参数:Long parentId
	 * 返回值:List<TreeNode>
	 * 思考:服务是否引用?
	 * 业务:
	 * 	参数:树形节点id
	 * 	树形节点初始化:必须首先加载最顶级节点parentId=0
	 */
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<TreeNode> findContentCategoryWithTreeNodeList(@RequestParam(defaultValue="0",value="id") Long parentId){
		//调用广告服务对象
		List<TreeNode> list = contentCategoryService.findContentCategoryWithTreeNodeList(parentId);
		return list;
	}
	
	/**
	 * 需求:添加树形分类节点
	 * 请求:/content/category/create
	 * 参数:Long parentId,String name
	 * 返回值:json格式E3mallResult
	 * 业务:
	 * 	1. 新建节点一定是子节点,is_parent=false
	 * 	2. 如果新建节点的父节点是子节点,修改原子节点状态is_parent=true
	 * 	3. 如果新建节点的父节点是父节点,直接创建
	 */
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3mallResult createNode(Long parentId,String name){
		//调用远程广告服务对象
		E3mallResult result = contentCategoryService.createNode(parentId, name);
		return result;
	}
		
}
