package com.wb.citylife.util.share;

import android.app.Activity;

import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMWXHandler;
import com.wb.citylife.config.NetConfig;

public class ShareHelper {
	
	/**
	 * 需要配置成我注册的APPID
	 */
	public static final String WX_APP_ID = "wx98ff40c9d02c04af";
	
	public static final String QQ_APP_ID = "1101770933";	
	
	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	public void addWXPlatform(Activity activity, UMSocialService controller) {
		
		// 微信 提供下载地址
		String contentUrl = NetConfig.APK_DOWNLOAD_URL;
		
		// 添加微信平台
		UMWXHandler wxHandler = controller.getConfig().supportWXPlatform(
				activity, WX_APP_ID, contentUrl);
		
		wxHandler.setWXTitle("永安城市生活");
		
		// 支持微信朋友圈
		UMWXHandler circleHandler = controller.getConfig()
				.supportWXCirclePlatform(activity, WX_APP_ID, contentUrl);
		
		circleHandler.setCircleTitle("永安城市生活");

	}
	
	/**
	 * @功能描述：设置QQ平台分享
	 * @param activity
	 * @param controller
	 */
	public void setQQPlatform(Activity activity, UMSocialService controller) {
		controller.getConfig().supportQQPlatform(activity, QQ_APP_ID, NetConfig.APK_DOWNLOAD_URL);
		controller.getConfig().setSsoHandler(new QZoneSsoHandler(activity, QQ_APP_ID));
		controller.getConfig().setSsoHandler(new TencentWBSsoHandler());			
	}
}
