package cn.e3.manager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3.manager.service.ItemService;
import cn.e3.mapper.TbItemMapper;
import cn.e3.pojo.TbItem;
@Service
public class ItemServiceImpl implements ItemService {
	
	//注入item接口代理对象
	@Autowired
	private TbItemMapper tbItemMapper;

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

}
