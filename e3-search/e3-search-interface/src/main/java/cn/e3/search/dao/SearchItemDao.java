package cn.e3.search.dao;

import org.apache.solr.client.solrj.SolrQuery;

import cn.e3.search.pojo.SolrPageBean;

public interface SearchItemDao {

	/**
	 * 需求:数据访问层Dao,查询索引库数据
	 * 参数:SolrQuery
	 * 返回值:分页包装类对象SolrPageBean
	 * 方法:findSolrIndexWithConditionPage
	 */
	public SolrPageBean findSolrIndexWithConditionPage(SolrQuery solrQuery);
}
