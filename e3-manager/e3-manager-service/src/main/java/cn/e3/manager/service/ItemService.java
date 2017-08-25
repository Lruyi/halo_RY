package cn.e3.manager.service;

import cn.e3.pojo.TbItem;

public interface ItemService {

	/**
	 * 需求:根据id查询商品信息
	 * 参数:Long itemId
	 * 返回值:TbItem
	 * 方法:findItemByDI
	 */
	public TbItem findItemByDI(Long itemId);
}
