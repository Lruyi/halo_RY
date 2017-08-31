package cn.e3.content.serviceImpl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3.content.service.ContentService;
import cn.e3.mapper.TbContentMapper;
import cn.e3.pojo.TbContent;
import cn.e3.pojo.TbContentExample;
import cn.e3.pojo.TbContentExample.Criteria;
import cn.e3.utils.DatagridPageBean;
import cn.e3.utils.E3mallResult;
@Service
public class ContentServiceImpl implements ContentService {
	
	//注入内容表mapper接口代理对象
	@Autowired
	private TbContentMapper contentMapper;

	/**
	 * 需求:根据id分类查询
	 * 参数:Long categoryId,Integer page,Integer rows
	 * 返回值:DatagridPageBean
	 */
	public DatagridPageBean findContentWithCategoryID(Long categoryId,
			Integer page, Integer rows) {
		
		//创建TbContentMapperExample对象
		TbContentExample example = new TbContentExample();
		//创建example的criteria对象,设置参数
		Criteria createCriteria = example.createCriteria();
		//根据id分类查询
		createCriteria.andCategoryIdEqualTo(categoryId);
		
		//查询之前,设置分页信息
		PageHelper.startPage(page, rows);
		
		//执行
		List<TbContent> list = contentMapper.selectByExample(example);
		
		//创建PageInfo对象,获取分页详细信息
		PageInfo<TbContent> pageInfo = new PageInfo<TbContent>(list);
		
		//创建分类包装类对象DatagridPageBean
		DatagridPageBean pageBean = new DatagridPageBean();
		//设置查询总记录数
		pageBean.setTotal(pageInfo.getTotal());
		//设置记录
		pageBean.setRows(list);
		
		return pageBean;
	}

	/**
	 * 需求:添加广告内容数据
	 * 参数:TbContent content
	 * 返回值:E3mallResult
	 */
	public E3mallResult saveContent(TbContent content) {
		// 不全时间属性
		Date date = new Date();
		content.setCreated(date);
		content.setUpdated(date);
		//保存
		contentMapper.insert(content);
		return E3mallResult.ok();
	}

}
