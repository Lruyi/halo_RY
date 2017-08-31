package cn.e3.content.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

import cn.e3.content.service.ContentCategoryService;
import cn.e3.mapper.TbContentCategoryMapper;
import cn.e3.mapper.TbContentMapper;
import cn.e3.pojo.TbContentCategory;
import cn.e3.pojo.TbContentCategoryExample;
import cn.e3.pojo.TbContentCategoryExample.Criteria;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.TreeNode;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	//输入分类接口代理对象
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	
	/**
	 * 需求:根据父id查询树形菜单(门户系统分页分类系统)子节点
	 * 参数:Long parentId
	 * 返回值:List<TreeNode>
	 * 思考:服务是否发布?
	 */
	public List<TreeNode> findContentCategoryWithTreeNodeList(Long parentId) {
		
		//创建List<TreeNode>集合对象,封装树形节点数据
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
		
		//创建TbContentCategoryExample对象
		TbContentCategoryExample example = new TbContentCategoryExample();
		//创建example的criteria对象,设置参数
		Criteria createCriteria = example.createCriteria();
		//根据父id查询树形菜单
		createCriteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		//遍历分类集合
		for (TbContentCategory tbContentCategory : list) {
			//创建TreeNode对象
			TreeNode node = new TreeNode();
			//设置树形节点id
			node.setId(tbContentCategory.getId());
			//设置树形节点名称
			node.setText(tbContentCategory.getName());
			//设置树形节点状态
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			
			//把单个树形节点放入树形节点集合中
			treeNodeList.add(node);
		}
		
		return treeNodeList;
	}


	/**
	 * 需求:添加树形分类节点
	 * 参数:Long parentId,String name
	 * 返回值:E3mallResult
	 * 业务:
	 * 	1. 新建节点一定是子节点,is_parent=false
	 * 	2. 如果新建节点的父节点是子节点,修改原子节点状态is_parent=true
	 * 	3. 如果新建节点的父节点是父节点,直接创建
	 */
	public E3mallResult createNode(Long parentId, String name) {
		
		//创建TbContentCategory对象
		TbContentCategory contentCategory = new TbContentCategory();
		//设置树形节点参数
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		contentCategory.setSortOrder(1);
		//该类目是否为父类目，1为true，0为false
		contentCategory.setIsParent(false);
		//节点创建数据
		Date date = new Date();
		//创建数据
		contentCategory.setCreated(date);
		//更新数据
		contentCategory.setUpdated(date);
		
		// 保存新建树形节点数据
		contentCategoryMapper.insert(contentCategory);
		
		//如果新建节点的父节点是子节点,修改原子节点状态is_parent=true
		//根据新建节点父id,查询父节点对象
		TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(parentId);
		//判断父节点是否是子节点
		if (!tbContentCategory.getIsParent()) {
			//表示次节点是子节点,修改次节点状态
			tbContentCategory.setIsParent(true);
			//调用接口修改方法
			contentCategoryMapper.updateByPrimaryKey(tbContentCategory);
		}
		//返回值
		return E3mallResult.ok(contentCategory);
	}

}
