package com.wb.citylife.dialog;

import com.wb.citylife.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;

public class ThemeDialog extends Dialog {
	
	private Context mContext;
	
	public ThemeDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mContext = context;
	}

	public ThemeDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public ThemeDialog(Context context) {
		super(context);
		mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sel_theme);
		
		getWindow().setGravity(Gravity.CENTER);
		setCancelable(true);
		
		initView();
	}	
	
	private void initView() {
		
	}
}
