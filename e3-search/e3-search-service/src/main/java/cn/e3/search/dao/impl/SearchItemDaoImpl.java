package cn.e3.search.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3.search.dao.SearchItemDao;
import cn.e3.search.pojo.SearchItem;
import cn.e3.search.pojo.SolrPageBean;
@Repository
public class SearchItemDaoImpl implements SearchItemDao {
	
	//注入solr服务
	@Autowired
	private SolrServer solrServer;

	/**
	 * 需求:数据访问层Dao,查询索引库数据
	 * 参数:SolrQuery
	 * 返回值:分页包装类对象SolrPageBean
	 * 方法:findSolrIndexWithConditionPage
	 */
	public SolrPageBean findSolrIndexWithConditionPage(SolrQuery solrQuery) {
		
		//创建分页包装类对象,封装分页数据
		SolrPageBean pageBean = new SolrPageBean();
		
		//创建List<SearchItem>集合,封装搜索商品数据
		List<SearchItem> sList = new ArrayList<SearchItem>();
		
		try {
			//使用solr服务查询索引库
			QueryResponse response = solrServer.query(solrQuery);
			//从response中获取结果集
			SolrDocumentList results = response.getResults();
			//获取命中总记录数
			Long count = results.getNumFound();
			//封装到分页包装类对象中
			pageBean.setRecordCount(count.intValue());
			//循环文档集,获取每一个文档数据
			for (SolrDocument sdoc : results) {
				
				//创建SearchItem对象,封装从索引库中查询的索引数据
				SearchItem item = new SearchItem();
				//从文档对象中获取id
				String id = (String) sdoc.get("id");
				//设置到SearchItem对象中
				item.setId(Long.parseLong(id));
				
				//从文档对象中获取标题
				String item_title = (String) sdoc.get("item_title");
				
				//获取高亮
				Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
				Map<String, List<String>> map = highlighting.get(id);
				List<String> list = map.get("item_title");
				//判断是否存在高亮
				if (list!=null && list.size()>0) {
					item_title = list.get(0);
				}
				
				//把高亮字段设置商品回显对象
				item.setTitle(item_title);
				
				//从文档对象中获取item_sell_point
				String item_sell_point = (String) sdoc.get("item_sell_point");
				//设置到SearchItem对象中
				item.setSell_point(item_sell_point);
				
				//从文档对象中获取item_price
				Long item_price = (Long) sdoc.get("item_price");
				//设置到SearchItem对象中
				item.setPrice(item_price);
				
				//从文档对象中获取item_image
				String item_image = (String) sdoc.get("item_image");
				//设置到SearchItem对象中
				item.setImage(item_image);
				
				//从文档对象中获取item_category_name
				String item_category_name = (String) sdoc.get("item_category_name");
				//设置到SearchItem对象中
				item.setCategory_name(item_category_name);
				
				//把每次循环商品数据放入集合
				sList.add(item);
			}
			
			//把查询商品数据对象集合放到分页包装类对象中
			pageBean.setsList(sList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pageBean;
	}

}
