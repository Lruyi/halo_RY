package cn.e3.manager.service;

import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbItemDesc;
import cn.e3.utils.DatagridPageBean;
import cn.e3.utils.E3mallResult;

public interface ItemService {

	/**
	 * 需求:根据id查询商品信息
	 * 参数:Long itemId
	 * 返回值:TbItem
	 * 方法:findItemByDI
	 */
	public TbItem findItemByDI(Long itemId);
	
	/**
	 * 需求:分页查询商品类别
	 * 参数:Integer page , Integer rows;
	 * 返回值:DatagridPageBean
	 */
	public DatagridPageBean findItemList(Integer page,Integer rows);
	
	/**
	 * 需求：存储商品表数据,商品描述表数据
	 * 参数:TbItem item,TbItemDesc itemDesc
	 * 返回值:E3mallResult
	 */
	public E3mallResult saveItem(TbItem item,TbItemDesc itemDesc);

	/**
	 * 需求:根据id查询商品描述数据
	 * 参数:Long itemId
	 * @param itemId
	 * @return TbItemDesc
	 */
	public TbItemDesc findItemDescByID(Long itemId);
}
