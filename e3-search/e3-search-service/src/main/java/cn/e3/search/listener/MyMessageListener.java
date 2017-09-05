package cn.e3.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3.search.mapper.SearchItemMapper;
import cn.e3.search.pojo.SearchItem;
/**
 * 同步索引库监听器 
 * @author lry
 * 流程:
 * 	1. 接收消息(商品id)
 * 	2. 根据商品id查询数据库新添加的,修改的商品数据
 * 	3. 把新的商品数据写入索引库即可,实现索引库数据同步
 */
public class MyMessageListener implements MessageListener{
	
	//注入mapper接口代理对象
	@Autowired
	private SearchItemMapper searchItemMapper;
	
	//注入solr服务对象
	@Autowired
	private SolrServer solrServer;

	@Override
	public void onMessage(Message message) {
		
		try {
			//定义商品id
			Long itemId = null;
			// 接收消息
			if (message instanceof TextMessage) {
				TextMessage m = (TextMessage) message;
				//获取消息商品id
				itemId = Long.parseLong(m.getText());
				//根据商品id查询出数据库的新的数据,这些数据必须和索引库字段一一对应
				SearchItem searchItem = searchItemMapper.selectSolrIndexWithDatabase(itemId);
				
				//创建文档对象,封装查询的商品数据
				SolrInputDocument doc = new SolrInputDocument();
				//封装索引域字段数据
				//封装id
				doc.addField("id", searchItem.getId());				
				doc.addField("item_title", searchItem.getTitle());
				doc.addField("item_sell_point", searchItem.getSell_point());
				doc.addField("item_price", searchItem.getPrice());
				doc.addField("item_image", searchItem.getImage());
				doc.addField("item_category_name", searchItem.getCategory_name());
				doc.addField("item_desc", searchItem.getItem_desc());
				
				//把数据写入索引库
				solrServer.add(doc);
				//提交
				solrServer.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
