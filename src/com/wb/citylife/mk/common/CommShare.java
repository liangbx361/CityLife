package com.wb.citylife.mk.common;

import android.app.Activity;

import com.tencent.connect.share.QzoneShare;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.ShareType;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.wb.citylife.R;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.util.share.ShareHelper;

public class CommShare {
			
	public static void share(Activity activity, String share, boolean disIcon) {
		UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
	            RequestType.SOCIAL);
		
		ShareHelper shareHelper = new ShareHelper();
		shareHelper.addWXPlatform(activity, mController);
		shareHelper.setQQPlatform(activity, mController);
		
		// 设置分享内容
		mController.setShareContent(share);
		mController.setShareType(ShareType.NORMAL);
		
		mController.getConfig().removePlatform( SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);
		
		if(disIcon) {
			// 设置分享图片, 参数2为图片的url地
			mController.setShareMedia(new UMImage(activity, R.drawable.ic_launcher));
		}

		mController.openShare(activity, false);
		
		mController.setAppWebSite(NetConfig.APK_DOWNLOAD_URL);		
		
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(share);
		qqShareContent.setTitle("城市生活");
		qqShareContent
		.setShareImage(new UMImage(activity, R.drawable.ic_launcher));
		qqShareContent.setTargetUrl(NetConfig.APK_DOWNLOAD_URL);
		mController.setShareMedia(qqShareContent);
		
		QZoneShareContent qzContent = new QZoneShareContent();
		qzContent.setAppWebSite(NetConfig.APK_DOWNLOAD_URL);
		qzContent.setShareContent(share);
		qzContent.setTitle("城市生活");
		qzContent.setShareMedia(new UMImage(activity, R.drawable.ic_launcher));
		qzContent.setTargetUrl(NetConfig.APK_DOWNLOAD_URL);
		mController.setShareMedia(qzContent);
		
	}
	
}
