package cn.e3.manager.service.impl;

import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import cn.e3.manager.jedis.JedisDao;
import cn.e3.manager.service.ItemService;
import cn.e3.mapper.TbItemDescMapper;
import cn.e3.mapper.TbItemMapper;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbItemDesc;
import cn.e3.pojo.TbItemExample;
import cn.e3.utils.DatagridPageBean;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.IDUtils;
import cn.e3.utils.JsonUtils;

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
	
	//注入spring提供的消息发送模板
	@Autowired
	private JmsTemplate jmsTemplate;
	
	//注入消息发送目的地
	@Autowired
	private ActiveMQTopic activeMQTopic;
	
	//注入jedisDao
	@Autowired
	private JedisDao jedisDao;
	
	//注入商品详情缓存唯一标识
	@Value("${ITEM_DETAIL}")
	private String ITEM_DETAIL;
	
	//注入商品详情页面缓存过期时间
	@Value("${ITEM_DETAIL_EXPIRE_TIEM}")
	private Integer ITEM_DETAIL_EXPIRE_TIEM;
	
	/**
	 * 需求:根据id查询商品信息
	 * 参数:Long itemId
	 * 返回值:TbItem
	 * 方法:findItemByDI
	 * 商品详情调用方法:
	 * 购物车调用方法:
	 * 因此此方法查询数据库,对数据库造成很大压力,因此必须添加缓存
	 * 缓存服务器redis:
	 * 数据结构选用:String(因为要给缓存设置过期时间,只有string能设置)
	 * KEY:ITEM_DETAIL:BASE:itemId
	 * VALUE:json(item)
	 * 缓存添加业务流程:
	 * 	1. 先查缓存
	 * 	2. 如果缓存中有数据,直接返回,不再查询数据库
	 * 	3. 如果缓存中没有数据,查询数据库,并把数据放入到缓存
	 * 	4. 缓存设置过期时间:12h. 12h后缓存消失,自动再次查询数据库
	 * 
	 */
	public TbItem findItemByDI(Long itemId) {
		
		try {
			//先查询缓存
			String itemJson = jedisDao.get(ITEM_DETAIL+":BASE:"+itemId);
			//判断商品缓存是否存在
			if (StringUtils.isNotBlank(itemJson)) {
				//说明缓存有值
				TbItem item = JsonUtils.jsonToPojo(itemJson, TbItem.class);
				//直接返回即可
				return item;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
		
		//添加缓存
		jedisDao.set(ITEM_DETAIL+":BASE:"+itemId, JsonUtils.objectToJson(item));
		//设置缓存过期时间
		jedisDao.expire(ITEM_DETAIL+":BASE:"+itemId, ITEM_DETAIL_EXPIRE_TIEM);
		
		return item;
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
	 * 添加商品:
	 * 	新添加商品与索引库商品数据不同步,添加商品时候发送消息,通知搜索服务,同步索引库数据
	 */
	public E3mallResult saveItem(TbItem item, TbItemDesc itemDesc) {
		//保存商品表数据
		//生成商品id
		final long itemId = IDUtils.genItemId();
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
		
		//发送消息
		jmsTemplate.send(activeMQTopic, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				//发送消息
				return session.createTextMessage(itemId+"");
			}
		});
		
		return E3mallResult.ok();
	}

	
	/**
	 * 需求:根据id查询商品描述数据
	 * 参数:Long itemId
	 * @param itemId
	 * @return TbItemDesc
	 * 商品详情调用方法:
	 * 购物车调用方法:
	 * 因此此方法查询数据库,对数据库造成很大压力,因此必须添加缓存
	 * 缓存服务器redis:
	 * 数据结构选用:String(因为要给缓存设置过期时间,只有string能设置)
	 * KEY:ITEM_DETAIL:DESC:itemId
	 * VALUE:json(item)
	 * 缓存添加业务流程:
	 * 	1. 先查缓存
	 * 	2. 如果缓存中有数据,直接返回,不再查询数据库
	 * 	3. 如果缓存中没有数据,查询数据库,并把数据放入到缓存
	 * 	4. 缓存设置过期时间:12h. 12h后缓存消失,自动再次查询数据库
	 */
	public TbItemDesc findItemDescByID(Long itemId) {
		
		try {
			//先查询缓存
			String itemDescJson = jedisDao.get(ITEM_DETAIL+":DESC:"+itemId);
			//判断商品缓存是否存在
			if (StringUtils.isNotBlank(itemDescJson)) {
				//说明缓存有值
				TbItemDesc itemDesc = JsonUtils.jsonToPojo(itemDescJson, TbItemDesc.class);
				//直接返回即可
				return itemDesc;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//查询商品描述
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		
		//添加缓存
		jedisDao.set(ITEM_DETAIL+":DESC:"+itemId, JsonUtils.objectToJson(itemDesc));
		//设置缓存过期时间
		jedisDao.expire(ITEM_DETAIL+":DESC:"+itemId, ITEM_DETAIL_EXPIRE_TIEM);
		
		return itemDesc;
	}
	

}
