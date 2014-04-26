package com.wb.citylife.util.share;

import android.app.Activity;

import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.sso.UMWXHandler;

public class ShareHelper {
	
	/**
	 * 需要配置成我注册的APPID
	 */
	public static final String WX_APP_ID = "wx967daebe835fbeac";
	
	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	public void addWXPlatform(Activity activity, UMSocialService controller) {
		// 微信图文分享,音乐必须设置一个url
		String contentUrl = "http://m.babytree.com/app/#area-1";
		// 添加微信平台
		UMWXHandler wxHandler = controller.getConfig().supportWXPlatform(
				activity, WX_APP_ID, contentUrl);
		wxHandler.setWXTitle("友盟社会化组件还不错-WXHandler...");

		UMImage mUMImgBitmap = new UMImage(activity,
				"http://www.umeng.com/images/pic/banner_module_social.png");

		UMusic uMusic = new UMusic("http://sns.whalecloud.com/test_music.mp3");
		uMusic.setAuthor("zhangliyong");
		uMusic.setTitle("天籁之音");
		// uMusic.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
		// 非url类型的缩略图需要传递一个UMImage的对象
		uMusic.setThumb(mUMImgBitmap);
		//
		// 视频分享
		UMVideo umVedio = new UMVideo(
				"http://v.youku.com/v_show/id_XNTc0ODM4OTM2.html");
		umVedio.setTitle("友盟社会化组件视频");
		// umVedio.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
		umVedio.setThumb(mUMImgBitmap);
		// 设置分享文字内容
		controller
				.setShareContent("友盟社会化组件还不错，让移动应用快速整合社交分享功能。www.umeng.com/social");
		// mController.setShareContent(null);
		// 设置分享图片
		// mController.setShareMedia(mUMImgBitmap);
		// 支持微信朋友圈
		UMWXHandler circleHandler = controller.getConfig()
				.supportWXCirclePlatform(activity, WX_APP_ID, contentUrl);
		circleHandler.setCircleTitle("友盟社会化组件还不错-CircleHandler...");

	}
}
