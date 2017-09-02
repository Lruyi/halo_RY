package cn.e3.content.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cn.e3.content.jedis.JedisDao;
import cn.e3.content.service.ContentService;
import cn.e3.mapper.TbContentMapper;
import cn.e3.pojo.TbContent;
import cn.e3.pojo.TbContentExample;
import cn.e3.pojo.TbContentExample.Criteria;
import cn.e3.utils.AdItem;
import cn.e3.utils.DatagridPageBean;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
@Service
public class ContentServiceImpl implements ContentService {
	
	//注入内容表mapper接口代理对象
	@Autowired
	private TbContentMapper contentMapper;
	
	//注入广告图片的高
	@Value("${HEIGHT}")
	private Integer HEIGHT;
	
	@Value("${HEIGHTB}")
	private Integer HEIGHTB;
	
	//注入广告图片的宽
	@Value("${WIDTH}")
	private Integer WIDTH;
	
	@Value("${WIDTHB}")
	private Integer WIDTHB;
	
	//注入首页缓冲唯一标识
	@Value("${INDEX_CACHE}")
	private String INDEX_CACHE;
	
	//注入jedisDao
	@Autowired
	private JedisDao jedisDao;

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
	 * 同步缓冲:
	 * 	添加  修改   删除
	 * 	删除redis服务器缓冲,
	 * 	下次查询,重新从数据库查询数据
	 */
	public E3mallResult saveContent(TbContent content) {
		
		//删除缓冲
		jedisDao.hdel(INDEX_CACHE, content.getCategoryId()+"");
		
		// 不全时间属性
		Date date = new Date();
		content.setCreated(date);
		content.setUpdated(date);
		//保存
		contentMapper.insert(content);
		return E3mallResult.ok();
	}

	/**
	 * 需求:根据分类id查询分类内容
	 * 参数:Long categoryId
	 * 返回值:List<AdItem>
	 * 业务:加载大广告轮播图数据
	 * 缓存业务分析:
	 * 1,首页/食品/衣服/金融/... 加载大量数据,发送大量sql语句查询数据库
	 * 2,首页/食品/衣服/金融/... 并发压力非常大
	 * 以上情况:压力转移数据库,大量数据库读操作,数据库io面临非常大压力
	 * 为了减轻数据库压力:查询广告数据之前,先查询缓存,达到减轻数据库压力
	 * 流程:
	 * 1,查询广告信息的先查询redis缓存服务器
	 * 2,如果缓存中有值,直接返回即可,不再查询数据库
	 * 3,如果缓存中没有值,查询数据库,同时需求把查询数据放入缓存
	 * redis服务器作为缓存服务器:
	 * 数据结构:Hash
	 * key:INDEX_CACHE / FOOD_CACHE
	 * field:categoryId
	 * value:缓存数据
	 */
	public List<AdItem> findContentAdItemWithCategoryId(Long categoryId) {
		
		try {
			//先查询缓冲
			String adJson = jedisDao.hget(INDEX_CACHE, categoryId+"");
			//判断缓冲中是否有值
			if (StringUtils.isNoneBlank(adJson)) {
				//把json缓冲中广告数据直接返回
				List<AdItem> adList = JsonUtils.jsonToList(adJson, AdItem.class);
				return adList;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//创建一个广告集合对象,封装广告数据
		List<AdItem> adList = new ArrayList<AdItem>();
		
		//创建内容表example对象
		TbContentExample example = new TbContentExample();
		//创建example对象的criteria对象,设置参数
		Criteria createCriteria = example.createCriteria();
		//根据分类id查询分类内容
		createCriteria.andCategoryIdEqualTo(categoryId);
		//执行查询
		List<TbContent> list = contentMapper.selectByExample(example);
		//遍历广告内容信息,把广告内容信息封装到广告集合中
		for (TbContent tbContent : list) {
			//创建AdItem 对象
			AdItem ad = new AdItem();
			//封装广告对象数据
			//设置轮播图地址
			ad.setSrc(tbContent.getPic());
			ad.setSrcB(tbContent.getPic2());
			
			ad.setHref(tbContent.getUrl());
			ad.setAlt(tbContent.getSubTitle());
			
			//设置图片高,宽
			ad.setHeight(HEIGHT);
			ad.setHeightB(HEIGHTB);
			
			ad.setWidth(WIDTH);
			ad.setWidthB(WIDTHB);
			
			//添加到广告集合中
			adList.add(ad);
		}
		
		//把从数据库查询数据添加到缓存中
		jedisDao.hset(INDEX_CACHE, categoryId+"", JsonUtils.objectToJson(adList));
		
		return adList;
	}

}
