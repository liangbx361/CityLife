package com.wb.citylife.dialog;

import com.wb.citylife.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class ChannelDialog extends Dialog implements OnClickListener {

	private ViewGroup orderV;
	private ViewGroup setLauncherV;
	private ViewGroup delV;
	private Context mContext;

	private android.view.View.OnClickListener listener;

	public ChannelDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mContext = context;
	}

	public ChannelDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public ChannelDialog(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_channel_layout);

		getWindow().setGravity(Gravity.BOTTOM);
		setCancelable(true);

		initView();
	}

	private void initView() {
		orderV = (ViewGroup) findViewById(R.id.box_option_batch_manager);
		setLauncherV = (ViewGroup) findViewById(R.id.box_option_setlauncher);
		delV = (ViewGroup) findViewById(R.id.box_option_delete_item);

		orderV.setOnClickListener(this);
		setLauncherV.setOnClickListener(this);
		delV.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (listener != null)
			listener.onClick(v);
	}

	public void setListener(android.view.View.OnClickListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void show() {
		super.show();
		
		WindowManager windowManager = ((Activity)mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = (int)(display.getWidth()); //设置宽度
		getWindow().setAttributes(lp);
	}	
}
