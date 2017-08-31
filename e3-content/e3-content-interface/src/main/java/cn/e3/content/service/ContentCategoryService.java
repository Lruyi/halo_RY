package cn.e3.content.service;

import java.util.List;

import cn.e3.utils.E3mallResult;
import cn.e3.utils.TreeNode;

public interface ContentCategoryService {

	/**
	 * 需求:根据父id查询树形菜单(门户系统分页分类系统)子节点
	 * 参数:Long parentId
	 * 返回值:List<TreeNode>
	 */
	public List<TreeNode> findContentCategoryWithTreeNodeList(Long parentId);
	
	/**
	 * 需求:添加树形分类节点
	 * 参数:Long parentId,String name
	 * 返回值:E3mallResult
	 */
	public E3mallResult createNode(Long parentId,String name);
}
