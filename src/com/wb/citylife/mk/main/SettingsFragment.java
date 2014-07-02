package com.wb.citylife.mk.main;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.common.file.DataCleanManager;
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
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.User;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.dialog.PushDialog;
import com.wb.citylife.dialog.ThemeDialog;
import com.wb.citylife.util.share.ShareHelper;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener,
	UmengUpdateListener, OnClickListener{
	
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
            RequestType.SOCIAL);
	
	private Activity mActivity;
	private Preference themePf;
	private Preference pushPf;
	private Preference feedbackPf;
	private Preference sharePf;
	private Preference updatePf;	
	private Preference cleanPf;
	private View exitView;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		exitView = (View) LayoutInflater.from(mActivity).inflate(R.layout.exit_layout, null);
		addPreferencesFromResource(R.xml.settings);
		initPf();
	}
		
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
//		getListView().addFooterView(exitView);		
//		
//		if(CityLifeApp.getInstance().checkLogin()) {
//			exitView.setVisibility(View.GONE);
//		} else {
//			exitView.setVisibility(View.VISIBLE);
//		}
//		exitView.setOnClickListener(this);
	}
	
	private void initPf() {		
		themePf = (Preference) findPreference(getResources().getString(R.string.pf_select_theme));
		themePf.setOnPreferenceClickListener(this);
		
		pushPf = (Preference) findPreference(getResources().getString(R.string.pf_push_settings));
		pushPf.setOnPreferenceClickListener(this);
		
		feedbackPf = (Preference) findPreference(getResources().getString(R.string.pf_feedbak));
		feedbackPf.setOnPreferenceClickListener(this);
		
		sharePf = (Preference) findPreference(getResources().getString(R.string.pf_apk_share));
		sharePf.setOnPreferenceClickListener(this);
		
		updatePf = (Preference) findPreference(getResources().getString(R.string.pf_update));
		updatePf.setOnPreferenceClickListener(this);	
		
		cleanPf = (Preference) findPreference(getResources().getString(R.string.pf_clean_cache));
		cleanPf.setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(getResources().getString(R.string.pf_select_theme))) {
			
			Dialog dialog = new ThemeDialog(getActivity(), R.style.popupStyle);
			dialog.show();
			
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_push_settings))) {
			
			Dialog dialog = new PushDialog(getActivity(), R.style.popupStyle);
			dialog.show();
			
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_feedbak))) {
			
			FeedbackAgent agent = new FeedbackAgent(getActivity());
		    agent.startFeedbackActivity();
		    
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_apk_share))) {	
			
			share();
			
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_update))) {
			
			UmengUpdateAgent.setUpdateListener(this);
			UmengUpdateAgent.update(mActivity);		
			ToastHelper.showToastInBottom(mActivity, "版本检测中");
			
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_clean_cache))) {
			
			DataCleanManager.cleanExternalAllCache(getActivity());
			ToastHelper.showToastInBottom(getActivity(), R.string.clean_success_toast);
		}
		
		return false;
	}	
	
	@Override
	public void onClick(View v) {
		//登出处理
		User user = CityLifeApp.getInstance().getUser();
		user.isLogin = 0;
		FinalDb finalDb = CityLifeApp.getInstance().getDb();
		finalDb.update(user, "userId='" + user.userId + "'");
		exitView.setVisibility(View.GONE);
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
