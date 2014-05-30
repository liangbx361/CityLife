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
	
	/***************************** 登录注册  **********************************************/
	public static final String METHOD_LOGIN = "login";
	public static final String METHOD_REGISTER = "register";
	public static final String METHOD_MODIFY_AVATAR = "modifyAvatar";
	public static final String METHOD_MODIFY_NICKNAME = "modifyNickname";	
	public static final String METHOD_MODIFY_PASSWORD = "modifyPwd";
	
	/***************************** 栏目  **********************************************/
	public static final String METHOD_CHANNEL = "channel";
	
	/***************************** 广告  **********************************************/
	public static final String METHOD_SCROLL_NEWS = "scrollNews";
	
	/***************************** 收藏  **********************************************/
	public static final String METHOD_COLLECT = "collect";
	
	/***************************** 投票  **********************************************/
	public static final String METHOD_VOTE_LIST = "voteList";
	public static final String METHOD_VOTE_DETAIL = "voteDetail";
	public static final String METHOD_VOTE_SATISTICS = "voteSatistics";
	
	/***************************** 二手市场  **********************************************/
	public static final String METHOD_OLD_INFO_LIST = "oldInfoList";
	public static final String METHOD_OLD_INFO_DETAIL = "oldInfoDetail";
	
	/***************************** 随手拍  **********************************************/
	public static final String METHOD_SHOOT_LIST = "shootList";
	
	/***************************** 房地产  **********************************************/
	public static final String METHOD_ESTATE_LIST = "estateList";
}
