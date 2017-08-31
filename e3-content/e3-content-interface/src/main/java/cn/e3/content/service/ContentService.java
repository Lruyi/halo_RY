package cn.e3.content.service;

import cn.e3.pojo.TbContent;
import cn.e3.utils.DatagridPageBean;
import cn.e3.utils.E3mallResult;

public interface ContentService {
	
	/**
	 * 需求:根据id分类查询
	 * 参数:Long categoryId,Integer page,Integer rows
	 * 返回值:DatagridPageBean
	 */
	public DatagridPageBean findContentWithCategoryID(Long categoryId,Integer page,Integer rows);
	
	/**
	 * 需求:添加广告内容数据
	 * 参数:TbContent content
	 * 返回值:E3mallResult
	 */
	public E3mallResult saveContent(TbContent content);
}
