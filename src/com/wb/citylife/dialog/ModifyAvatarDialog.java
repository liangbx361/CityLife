package com.wb.citylife.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wb.citylife.R;

public class ModifyAvatarDialog extends Dialog implements OnClickListener {
	
	private Button takePictureBtn;
	private Button photoAlbum;
	private Context mContext;
	
	private android.view.View.OnClickListener listener;
	
	public ModifyAvatarDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mContext = context;
		
	}

	public ModifyAvatarDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public ModifyAvatarDialog(Context context) {
		super(context);
		mContext = context;
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_modify_avatar);
		
		getWindow().setGravity(Gravity.BOTTOM);
		setCancelable(true);
		
		initView();
	}	
	
	private void initView() {
		takePictureBtn = (Button) findViewById(R.id.take_picture);
		photoAlbum = (Button) findViewById(R.id.photo_album);
		
		takePictureBtn.setOnClickListener(this);
		photoAlbum.setOnClickListener(this);
	}
	
	public void setListener(android.view.View.OnClickListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		if (listener != null)
			listener.onClick(v);		
	}
}
