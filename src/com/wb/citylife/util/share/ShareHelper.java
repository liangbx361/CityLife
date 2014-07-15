package com.wb.citylife.util.share;

import android.app.Activity;

import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.UMWXHandler;
import com.wb.citylife.config.NetConfig;

public class ShareHelper {
	
	/**
	 * 需要配置成我注册的APPID
	 */
	public static final String WX_APP_ID = "wx98ff40c9d02c04af";
	
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
}
