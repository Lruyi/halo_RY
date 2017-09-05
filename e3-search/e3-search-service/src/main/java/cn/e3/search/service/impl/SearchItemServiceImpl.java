package cn.e3.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3.search.dao.SearchItemDao;
import cn.e3.search.mapper.SearchItemMapper;
import cn.e3.search.pojo.SearchItem;
import cn.e3.search.pojo.SolrPageBean;
import cn.e3.search.service.SearchItemService;
import cn.e3.utils.E3mallResult;

import com.alibaba.dubbo.config.annotation.Service;
@Service
public class SearchItemServiceImpl implements SearchItemService {
	
	//注入mapper接口代理对象
	@Autowired
	private SearchItemMapper searchItemMapper;
	
	//注入solr服务对象
	@Autowired
	private SolrServer solrServer;
	
	//输入SearchItemDao
	@Autowired
	private SearchItemDao searchItemDao;

	/**
	 * 需求:查询数据库,数据库导入索引库
	 * 思考:服务发布?
	 */
	public E3mallResult importSolrIndexWithDatabase() {
		
		try {
			//查询索引数据
			List<SearchItem> list = searchItemMapper.importSolrIndexWithDatabase();
			//循环商品数据集合
			for (SearchItem searchItem : list) {
				
				//创建document对象,封装商品索引数据
				SolrInputDocument doc = new SolrInputDocument();
				//封装索引域字段
				//封装id
				doc.addField("id", searchItem.getId());
				//封装item_title
				doc.addField("item_title", searchItem.getTitle());
				//封装item_sell_point
				doc.addField("item_sell_point", searchItem.getSell_point());
				//封装item_price
				doc.addField("item_price", searchItem.getPrice());
				//封装item_image
				doc.addField("item_image", searchItem.getImage());
				//封装item_category_name
				doc.addField("item_category_name", searchItem.getCategory_name());
				//封装item_desc
				doc.addField("item_desc", searchItem.getItem_desc());
				
				//把数据设置到索引库中(这是添加数据到索引库)
				solrServer.add(doc);
			}
			
			//提交
			solrServer.commit();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return E3mallResult.ok();
	}

	/**
	 * 需求:业务处理层,查询索引库数据
	 * 参数:String qName,Integer page,Integer rows
	 * 返回值:分页包装类对象SolrPageBean
	 * 方法:findSolrIndexWithConditionPage
	 */
	public SolrPageBean findSolrIndexWithConditionPage(String qName,
			Integer page, Integer rows) {
		
		// 创建solrj提供封装参数对象SolrQuery
		SolrQuery solrQuery = new SolrQuery();
		//封装查询参数
		if (qName!=null && !"".equals(qName)) {
			solrQuery.setQuery(qName);
		}else{
			solrQuery.setQuery("*:*");
		}
		
		//设置分页
		int startNo = (page-1)*rows;
		solrQuery.setStart(startNo);
		solrQuery.setRows(rows);
		
		//高亮展示
		//开启高亮
		solrQuery.setHighlight(true);
		//指定高亮字段
		solrQuery.addHighlightField("item_title");
		//设置高亮前缀
		solrQuery.setHighlightSimplePre("<font color='red'>");
		//设置高亮后缀
		solrQuery.setHighlightSimplePost("</font>");
		
		//设置默认查询字段
		solrQuery.set("df", "item_keywords");
		
		//查询dao
		SolrPageBean pageBean = searchItemDao.findSolrIndexWithConditionPage(solrQuery);
		//设置当前页
		pageBean.setCurPage(page);
		//设置总页码
		//计算总页码
		Integer recordCount = pageBean.getRecordCount();
		int pages = (int) Math.ceil(1.0*recordCount/rows);
		
		pageBean.setTotalPages(pages);
		
		return pageBean;
	}

}
