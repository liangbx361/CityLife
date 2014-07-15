package com.wb.citylife.mk.main;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import com.wb.citylife.mk.about.AboutActivity;
import com.wb.citylife.mk.common.CommShare;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener,
	UmengUpdateListener, OnClickListener{
		
	private Activity mActivity;
	private Preference themePf;
	private Preference pushPf;
	private Preference feedbackPf;
	private Preference sharePf;
	private Preference updatePf;	
	private Preference cleanPf;
	private Preference aboutPf;
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
		
		aboutPf = (Preference) findPreference(getResources().getString(R.string.pf_about));
		aboutPf.setOnPreferenceClickListener(this);
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
			
			String share = "永安城市生活，专注于提供最好的本地服务，赶紧来试一试吧~\n应用下载地址:" + NetConfig.APK_DOWNLOAD_URL;
			CommShare.share(mActivity, share, false);
			
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_update))) {
			
			UmengUpdateAgent.setUpdateListener(this);
			UmengUpdateAgent.update(mActivity);	
			UmengUpdateAgent.forceUpdate(mActivity);
			UmengUpdateAgent.setUpdateAutoPopup(true);
			ToastHelper.showToastInBottom(mActivity, "版本检测中");
			
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_clean_cache))) {
			
			DataCleanManager.cleanExternalAllCache(getActivity());
			ToastHelper.showToastInBottom(getActivity(), R.string.clean_success_toast);
			
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_about))) {
			Intent intent = new Intent(mActivity, AboutActivity.class);
			startActivity(intent);
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
	
	@Override
	public void onUpdateReturned(int updateStatus, UpdateResponse response) {
		if(updateStatus == UpdateStatus.No) {
			ToastHelper.showToastInBottom(mActivity, "已是最新版本");
		}
	}
	
}
