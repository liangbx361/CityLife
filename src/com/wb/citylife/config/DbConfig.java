package com.wb.citylife.config;

/**
 * 此类用于数据库配置
 * @author liangbx
 *
 */

public interface DbConfig {
	
	/**
	 * 数据库名称
	 */
	public static final String DB_NAME = "citylife.db";
	
	/**
	 * 数据库当前版本 （从1开始）
	 */
	public static final int DB_VERSION = 1;
	
	/**
	 * 数据库存放路径（可选如需要请修改FinalDb.create, 默认存储在 /data/data/应用包名/databases目录下)
	 */
	public static final String DB_DIRECTORY = "";
	
	/********************************************* 定义表名并注释表的用途  **************************************************/
	
	/**
	 * 用于存储搜索关键字的历史记录
	 */
	public static final String TN_SEARCH_KEYWORD_HISTORY = "search_keyword_history";
}
