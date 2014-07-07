package com.wb.citylife.mk.common;

import android.app.Activity;

import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.wb.citylife.R;
import com.wb.citylife.util.share.ShareHelper;

public class CommShare {
			
	public static void share(Activity activity, String share, boolean disIcon) {
		UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
	            RequestType.SOCIAL);
		
		ShareHelper shareHelper = new ShareHelper();
		shareHelper.addWXPlatform(activity, mController);
		// 设置分享内容
		mController.setShareContent(share);
		
		if(disIcon) {
			// 设置分享图片, 参数2为图片的url地
			mController.setShareMedia(new UMImage(activity, R.drawable.ic_launcher));
		}

		mController.openShare(activity, false);
	}
	
}
