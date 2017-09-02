package cn.e3.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

import cn.e3.search.mapper.SearchItemMapper;
import cn.e3.search.pojo.SearchItem;
import cn.e3.search.service.SearchItemService;
import cn.e3.utils.E3mallResult;
@Service
public class SearchItemServiceImpl implements SearchItemService {
	
	//注入mapper接口代理对象
	@Autowired
	private SearchItemMapper searchItemMapper;
	
	//注入solr服务对象
	@Autowired
	private SolrServer solrServer;

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

}
