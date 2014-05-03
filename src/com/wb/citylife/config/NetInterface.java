package com.wb.citylife.config;

/**
 * 此类用于定义网络接口名称
 * @author Administrator
 *
 */
public interface NetInterface {
	
	/***************************** 资讯  **********************************************/
	public static final String METHOD_NEWS_LIST = "newsList";
	public static final String METHOD_NEWS_DETAIL= "newsDetail";
	
	/***************************** 评论  **********************************************/
	public static final String METHOD_COMMENT_LIST = "commentList";
	public static final String METHOD_COMMENT = "comment";
}
