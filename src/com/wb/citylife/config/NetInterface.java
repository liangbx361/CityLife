package com.wb.citylife.config;

/**
 * 此类用于定义网络接口名称
 * @author Administrator
 *
 */
public interface NetInterface {
	
	/***************************** 公众账号 **********************************************/
	public static final String METHOD_BIND = "bind/bind";	 
	public static final String METHOD_UNBIND = "bind/unBind";	
	public static final String METHOD_QUERY_IS_BIND = "query/isBinded";
	public static final String METHOD_QUERY_BIND_ACCOUNT = "query/bindedAccountList";	
	public static final String METHOD_QUERY_ACCOUNT = "query/accountList";
	
	public static final String METHOD_RELATION_FRIEND = "relation/friend";
	public static final String METHOD_ACCOUNT = "account";
	
	/***************************** 钱包相关 **********************************************/
	public static final String METHOD_WALLET_COUNT = "wallet/count";
	public static final String METHOD_WALLET_CONSUMERCARD = "wallet/card";
	public static final String METHOD_WALLET_COUPON = "wallet/coupon";
	public static final String METHOD_WALLET_ELECTICKET = "wallet/ticket";
	public static final String METHOD_WALLET_GROUPON = "wallet/groupon";
	public static final String METHOD_WALLET_PASSWORD = "wallet/password";
	
	/***************************** 收藏  **********************************************/
	public static final String METHOD_RELATION_FAVORITE = "relation/favorite";
	
	/***************************** 商品  **********************************************/
	public static final String METHOD_PRODUCT = "product";
	
	/***************************** 搜索  **********************************************/
	public static final String METHOD_SEARCH_PRODUCT = "search/product";
	public static final String METHOD_SEARCH_ACCOUNT = "search/account";
	
	/***************************** 账号相关 **********************************************/
	public static final String METHOD_ACCOUNT_CAPTHCHA = "checkcode";
	public static final String METHOD_ACCOUNT_INFO = "account";
	public static final String METHOD_ACCOUNT_REGISTER = "account/register";
	public static final String METHOD_ACCOUNT_LOGIN = "account/login";
	public static final String METHOD_ACCOUNT_SETPWD = "account/password";
	public static final String METHOD_ACCOUNT_PROXY = "account/proxy";
	
	public static final String METHOD_ACCOUNT_ACATAR = "account/avatar";
	public static final String METHOD_ACCOUT_PROFILE = "account/profile";
	
	/***************************** 广告相关 **********************************************/
	public static final String METHOD_ADVERTISEMENT = "support/advertisement";

	/***************************** 栏目相关 **********************************************/
	public static final String METHOD_CHANNEL = "support/channel";

	/***************************** 订单 **********************************************/
	public static final String METHOD_ORDER = "order";
	public static final String METHOD_ORDER_COUNT = "order/count";
	public static final String METHOD_ORDER_STATUS = "order/status";
	
	/***************************** 流量套餐查询接口 **********************************************/
	public static final String ACCOUNT_PROXY = "account/proxy";
	
	/***************************** 翼支付相�?**********************************************/
	public static final String METHOD_OPEN_BESTPAY = "account/open_bestpay";
	
	public static final String PRODUCT_TRAFFIC = "/product/traffic";
	public static final String ORDER = "/order";
	public static final String ORDER_BESTPAY = "/order/bestpay";
}
