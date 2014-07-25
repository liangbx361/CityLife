package com.wb.citylife.util.share;

import android.app.Activity;

import com.common.widget.ToastHelper;
import com.umeng.socialize.bean.CustomPlatform;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.OnCustomPlatformClickListener;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
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
	public void addWXPlatform(final Activity activity, UMSocialService controller) {
		
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
		
//		controller.postShare(activity, SHARE_MEDIA.WEIXIN_CIRCLE, new SnsPostListener() {
//			
//			@Override
//			public void onStart() {
//				
//			}
//			
//			@Override
//			public void onComplete(SHARE_MEDIA platform,int eCode, SocializeEntity entity) {
//				ToastHelper.showToastInBottom(activity, eCode+"");
//				 if(eCode == StatusCode.ST_CODE_SUCCESSED){
//					 ToastHelper.showToastInBottom(activity, "分享成功");
//				 }
//			}
//		});
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
