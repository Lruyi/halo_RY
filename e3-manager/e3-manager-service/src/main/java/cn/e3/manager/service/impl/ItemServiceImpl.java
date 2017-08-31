package cn.e3.manager.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3.manager.service.ItemService;
import cn.e3.mapper.TbItemDescMapper;
import cn.e3.mapper.TbItemMapper;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbItemDesc;
import cn.e3.pojo.TbItemExample;
import cn.e3.utils.DatagridPageBean;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.IDUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
@Service
public class ItemServiceImpl implements ItemService {
	
	//注入item接口代理对象
	@Autowired
	private TbItemMapper tbItemMapper;
	
	//注入商品描述表Mapper接口代理对象
	@Autowired
	private TbItemDescMapper itemDescMapper;

	/**
	 * 需求:根据id查询商品信息
	 * 参数:Long itemId
	 * 返回值:TbItem
	 * 方法:findItemByDI
	 */
	public TbItem findItemByDI(Long itemId) {
		TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
		return tbItem;
	}

	/**
	 * 需求:分页查询商品类别
	 * 参数:Integer page , Integer rows;
	 * 返回值:DatagridPageBean
	 * 
	 * 业务:
	 * 	1. 分页查询商品列表
	 * 	2. 使用PageHelper进行分页查询
	 */
	public DatagridPageBean findItemList(Integer page, Integer rows) {
		//创建TbItemExample对象
		TbItemExample example = new TbItemExample();
		//在执行之前设置分页查询
		PageHelper.startPage(page, rows);
		//执行查询
		List<TbItem> list = tbItemMapper.selectByExample(example);
		//创建分页插件提供的PageInfo包装类对象,获取分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(list);
		//把分页数据封装到分页对象中
		//创建分页对象
		DatagridPageBean pageBean = new DatagridPageBean();
		//封装参数
		//设置总记录数
		pageBean.setTotal(pageInfo.getTotal());
		//设置分页列表数据
		pageBean.setRows(list);
		
		return pageBean;
	}

	/**
	 * 需求:保存商品表数据,商品描述表数据
	 * 参数:TbItem item,TbItemDesc itemDesc
	 * 返回值:E3mallResult
	 * 业务:
	 * 	商品id不能重复,必须保证商品id唯一性
	 * 模式:
	 * 	1. redis+1
	 * 	2. 数据库生成id
	 * 	3. 时间+随机数(√)---- 毫秒+2位随机数(99)---每一个毫秒有10000
	 */
	public E3mallResult saveItem(TbItem item, TbItemDesc itemDesc) {
		//保存商品表数据
		//生成商品id
		long itemId = IDUtils.genItemId();
		item.setId(itemId);
		//商品状态
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte)1);
		
		//商品添加时间
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		
		//保存商品
		tbItemMapper.insert(item);
		
		//保存商品描述
		//设置商品id
		itemDesc.setItemId(itemId);
		//设置时间
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		//保存
		itemDescMapper.insert(itemDesc);
		
		return E3mallResult.ok();
	}
	
	

}
