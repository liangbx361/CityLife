package com.wb.citylife.mk.main;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;

import com.common.widget.ToastHelper;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.wb.citylife.R;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.util.share.ShareHelper;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener,
	UmengUpdateListener{
	
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
            RequestType.SOCIAL);
	
	private Activity mActivity;
	private Preference feedbackPreference;
	private Preference sharePreference;
	private Preference updatePreference;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		feedbackPreference = (Preference) findPreference(getResources().getString(R.string.pf_feedbak));
		feedbackPreference.setOnPreferenceClickListener(this);
		
		sharePreference = (Preference) findPreference(getResources().getString(R.string.pf_apk_share));
		sharePreference.setOnPreferenceClickListener(this);
		
		updatePreference = (Preference) findPreference(getResources().getString(R.string.pf_update));
		updatePreference.setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		
		if(preference.getKey().equals(getResources().getString(R.string.pf_feedbak))) {
			FeedbackAgent agent = new FeedbackAgent(getActivity());
		    agent.startFeedbackActivity();
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_apk_share))) {			
			share();
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_update))) {
			UmengUpdateAgent.setUpdateListener(this);
			UmengUpdateAgent.update(mActivity);		
			ToastHelper.showToastInBottom(mActivity, "版本检测中");
		}
		
		return false;
	}	
	
	/**
	 * 分享应用下载地址
	 */
	private void share() {
		ShareHelper shareHelper = new ShareHelper();
		shareHelper.addWXPlatform(mActivity, mController);
		// 设置分享内容
		mController.setShareContent("应用下载" + NetConfig.APK_DOWNLOAD_URL);
		// 设置分享图片, 参数2为图片的url地址
		mController.setShareMedia(new UMImage(getActivity(), R.drawable.ic_launcher));

		mController.openShare(mActivity, false);
	}

	@Override
	public void onUpdateReturned(int updateStatus, UpdateResponse response) {
		if(updateStatus == UpdateStatus.No) {
			ToastHelper.showToastInBottom(mActivity, "已是最新版本");
		}
	}
}
