package cn.e3.solr.cloud;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

public class MySolrCloud {

	/**
	 * 需求:测试solr集群
	 * 条件:solrJ连接远程solr服务器,测试查询,添加...
	 * @throws Exception 
	 */
	@Test
	public void testSolrCloud() throws Exception{
		//指定zookeeper配置中心服务器地址
		String zkHost ="192.168.80.131:2182,192.168.80.131:2183,192.168.80.131:2184";
		//创建solrJ提供的集群对象,连接远程集群服务
		CloudSolrServer cloudSolrServer = new CloudSolrServer(zkHost);
		//设置操作索引库
		cloudSolrServer.setDefaultCollection("item");
		//创建solrQuery封装查询参数对象
		SolrQuery solrQuery = new SolrQuery();
		//设置参数查询
		solrQuery.setQuery("*:*");
		//使用集群对象查询索引库
		QueryResponse response = cloudSolrServer.query(solrQuery);
		//获取文档结果集合对象
		SolrDocumentList results = response.getResults();
		for (SolrDocument solrDocument : results) {
			String item_title = (String) solrDocument.get("item_title");
			System.out.println(item_title);
		}
	}
}
