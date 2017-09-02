package cn.e3.search.mapper;

import java.util.List;

import cn.e3.search.pojo.SearchItem;

public interface SearchItemMapper {

	/**
	 * 需求:查询数据库数据,把数据导入索引库
	 * 导入原则:
	 * 把数据库字段一一对应映射索引库域字段中.
	 * 查询数据库字段有多少,索引库域字段有多少.
	 */
	public List<SearchItem> importSolrIndexWithDatabase();
}
