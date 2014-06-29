package com.wb.citylife.config;

/**
 * 此类用于定义URL地址
 * @author liangbx
 *
 */
public class NetConfig implements NetInterface {
			
	/*************************************测试环境配置************************************/
	//接口地址
	public static final String DEBUG_BASE_URL = "http://192.168.1.107:8081/";
	public static final String DEBUG_INTRANET_BASE_URL = "http://192.168.10.109:8081/"; 
		
	/*************************************正式环境配置************************************/
	//接口地址
	public static final String RELEASE_BASE_URL = "";
	public static final String RELEASE_INTRANET_BASE_URL = "";
	
	/*************************************项目扩展地址************************************/
	public static final String EXTEND_URL = "citylive/client/";
//	public static final String EXTEND_URL = "CityLife/";
	
	/*************************************应用下载地址************************************/
	public static final String APK_DOWNLOAD_URL = "";
	
	/**
	 * 获取服务器前缀地址+项目扩展地址
	 * @return
	 */
	public static String getServerBaseUrl() {
		if(DebugConfig.VERSION_CONFIG == 0) {
			if(DebugConfig.NET_CONFIG == 0) {
				return DEBUG_INTRANET_BASE_URL;
			} else {
				return DEBUG_BASE_URL;
			}
		} else {
			if(DebugConfig.NET_CONFIG == 0) {
				return RELEASE_INTRANET_BASE_URL;
			} else {
				return RELEASE_BASE_URL;
			}
		}
	}
	
	/**
	 * 获取图片的URL地址
	 * @param url
	 * @return
	 */
	public static String getPictureUrl(String url){
		if(url == null)
		{
			return "";
		}
		if(url.indexOf("http://")!=-1){
			return url;
		}else{
			return getServerBaseUrl() + EXTEND_URL + url;
		}
	}
}
