package com.wb.citylife.mk.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Debug;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.NetworkImageView.NetworkImageListener;
import com.common.media.BitmapHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.User;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.mk.mycenter.AccountManagerActivity;
import com.wb.citylife.mk.mycenter.CollectActivity;
import com.wb.citylife.mk.mycenter.LoginActivity;
import com.wb.citylife.mk.mycenter.MsgHistroyActivity;

public class MyCenterFragment extends PreferenceFragment implements OnPreferenceClickListener,
	MyCenterListener, OnClickListener, NetworkImageListener{
	
	private Activity mActivity;
	private MainListener mainListener;
	private View accountView;
	
	private Preference myCollectPf;
	private Preference myMsgPf;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		mainListener = (MainListener) activity;
		mainListener.setMyCenter(this);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountView = (View) LayoutInflater.from(mActivity).inflate(R.layout.account_layout, null);
		AccountHolder holder = new AccountHolder();
		holder.avatarIv = (NetworkImageView) accountView.findViewById(R.id.avatar);
		holder.avatarIv.setDefaultImageResId(R.drawable.default_avatar);
		holder.avatarIv.setNetworkImageListener(this);
		holder.nameTv = (TextView) accountView.findViewById(R.id.title);
		accountView.setTag(holder);
		accountView.setOnClickListener(this);		
		
		addPreferencesFromResource(R.xml.mycenter);		
		initPf();		
	}
			
	@Override
	public void onResume() {
		super.onResume();
		initUser();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().addHeaderView(accountView);
	}
	
	private void initPf() {
		myCollectPf = (Preference) findPreference(getResources().getString(R.string.pf_collect));
		myCollectPf.setOnPreferenceClickListener(this);
		myMsgPf = (Preference) findPreference(getResources().getString(R.string.pf_msg));
		myMsgPf.setOnPreferenceClickListener(this);
	}
	
	private void initUser() {
		if(CityLifeApp.getInstance().checkLogin()) {
			onLogin();
		} else {
			AccountHolder holder = (AccountHolder) accountView.getTag();
			holder.avatarIv.setImageResource(R.drawable.default_avatar);
			holder.nameTv.setText(R.string.pf_login);
		}
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(getResources().getString(R.string.pf_collect))) {
			if(CityLifeApp.getInstance().checkLogin()) {
				Intent intent = new Intent(mActivity, CollectActivity.class);
				startActivity(intent);
			} else {
				ToastHelper.showToastInBottom(mActivity, R.string.please_login);
			}
		} else if(preference.getKey().equals(getResources().getString(R.string.pf_msg))) {
			if(CityLifeApp.getInstance().checkLogin()) {
				Intent intent = new Intent(mActivity, MsgHistroyActivity.class);
				startActivity(intent);
			} else {
				ToastHelper.showToastInBottom(mActivity, R.string.please_login);
			}
		}
		return false;
	}
	
	@Override
	public void onLogin() {
		User user = CityLifeApp.getInstance().getUser(); 
		AccountHolder holder = (AccountHolder) accountView.getTag();
		if(user.getNickname().equals("")) {
			holder.nameTv.setText(user.userphone);
		} else {
			holder.nameTv.setText(user.nickname);
		}		
		DebugConfig.showLog("avatarUrl", NetConfig.getPictureUrl(user.getAvatarUrl()));
		holder.avatarIv.setImageUrl(NetConfig.getPictureUrl(user.getAvatarUrl()), 
				CityLifeApp.getInstance().getImageLoader());		
	}

	@Override
	public void onClick(View v) {
		if(!CityLifeApp.getInstance().checkLogin()) {
			startActivityForResult(new Intent(mActivity, LoginActivity.class), 0);
		} else {
			startActivity(new Intent(mActivity, AccountManagerActivity.class));
		}
	}		
	
	class AccountHolder {
		public NetworkImageView avatarIv;
		public TextView nameTv;
	}
	
	@Override
	public void onGetBitmapListener(ImageView imageView, Bitmap bitmap) {
		if(bitmap != null) {
			imageView.setImageBitmap(BitmapHelper.toRoundCorner(bitmap, bitmap.getHeight()/2));
		}
	}  
}
