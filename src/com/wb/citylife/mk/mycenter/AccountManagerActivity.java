package com.wb.citylife.mk.mycenter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.common.media.CarameHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Avatar;
import com.wb.citylife.bean.db.User;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.dialog.ModifyAvatarDialog;
import com.wb.citylife.parser.AvatarParser;

public class AccountManagerActivity extends BaseActivity implements OnClickListener{
	
	private static final int CROP_WIDTH = 400;
	private static final int CROP_HEIGHT = 400;
	
	private File photoFile;
	private Uri photoUri;
	
	private LinearLayout modifyAvatarLayout;
	private LinearLayout modifyNicknameLayout;
	private LinearLayout modifyPasswordLayout;
	private Button logoutBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_manager);
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		
	}

	@Override
	public void initView() {
		modifyAvatarLayout = (LinearLayout) findViewById(R.id.modify_avatar);
		modifyNicknameLayout = (LinearLayout) findViewById(R.id.modify_nickname);
		modifyPasswordLayout = (LinearLayout) findViewById(R.id.modify_password);
		logoutBtn = (Button) findViewById(R.id.logout);
		
		modifyAvatarLayout.setOnClickListener(this);
		modifyNicknameLayout.setOnClickListener(this);
		modifyPasswordLayout.setOnClickListener(this);	
		logoutBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
				
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.take_picture:
			getImageFromCamera();
			break;
			
		case R.id.photo_album:
			getImageFromAlbum();
			break;
			
		case R.id.modify_avatar:
			ModifyAvatarDialog dialog = new ModifyAvatarDialog(this, R.style.popupStyle);
			dialog.setListener(this);
			dialog.show();			
			break;
			
		case R.id.modify_nickname:
			startActivity(new Intent(this, ModifyNickNameActivity.class));
			break;
			
		case R.id.modify_password:
			startActivity(new Intent(this, ModifyPasswordActivity.class));
			break;
			
		case R.id.logout:
			//登出处理
			User user = CityLifeApp.getInstance().getUser();
			user.isLogin = 0;
			FinalDb finalDb = CityLifeApp.getInstance().getDb();
			finalDb.update(user, "userId='" + user.userId + "'");
			finish();
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == ResultCode.REQUEST_CODE_CAPTURE_CAMEIA ) {
			
			 Intent intent = new Intent("com.android.camera.action.CROP");
			   intent.setDataAndType(photoUri, "image/*");
			   intent.putExtra("crop", "true");//可裁剪
			   intent.putExtra("aspectX", 1);
			   intent.putExtra("aspectY", 1);
			   intent.putExtra("outputX", CROP_WIDTH);
			   intent.putExtra("outputY", CROP_HEIGHT);
			   intent.putExtra("scale", true);
			   intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			   intent.putExtra("return-data", false);//若为false则表示不返回数据
			   intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
			   intent.putExtra("noFaceDetection", true); 
			   startActivityForResult(intent, ResultCode.REQUEST_CODE_IMAGE_CROP);
			
		} else if(requestCode == ResultCode.REQUEST_CODE_PICK_IMAGE) {
			//上传头像
			upLoadAvatar(photoFile);
			
		} else if(requestCode == ResultCode.REQUEST_CODE_IMAGE_CROP) {
			//上传头像
			upLoadAvatar(photoFile);
		}
	}
	
	/**
	 * 从相机中获取图片
	 */
	private void getImageFromCamera() {  
		String state = Environment.getExternalStorageState();  
	    if (state.equals(Environment.MEDIA_MOUNTED)) {  
	    	CarameHelper helper = new CarameHelper(this);
	    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	photoFile = helper.getOutputMediaFile(CarameHelper.MEDIA_TYPE_IMAGE);   
	    	photoUri = Uri.fromFile(photoFile);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); 
			startActivityForResult(intent, ResultCode.REQUEST_CODE_CAPTURE_CAMEIA);
	    } else {
	    	ToastHelper.showToastInBottom(this, R.string.sdcard_error);
	    }
	}
	
	/**
	 * 从相册中获取图片
	 */
	private void getImageFromAlbum() {
		CarameHelper helper = new CarameHelper(this);
		photoFile = helper.getOutputMediaFile(CarameHelper.MEDIA_TYPE_IMAGE);   
    	photoUri = Uri.fromFile(photoFile);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");

		//裁剪框比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		//图片输出大小
		intent.putExtra("outputX", CROP_WIDTH);
		intent.putExtra("outputY", CROP_HEIGHT);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		startActivityForResult(intent, ResultCode.REQUEST_CODE_PICK_IMAGE);
	}
	
	/**
	 * 上传头像
	 * @param file
	 */
	private void upLoadAvatar(File file) {		
		String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
		String PREFIX = "--" , LINE_END = "\r\n"; 
		String CONTENT_TYPE = "multipart/form-data";   //内容类型

		try {
			AjaxParams params = new AjaxParams();
			params.put("userId", CityLifeApp.getInstance().getUser().getUserId());
			params.put("avatar", file);
			FinalHttp fh = new FinalHttp(); 
			fh.addHeader("connection", "keep-alive");
			fh.addHeader("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			fh.post(NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + NetInterface.METHOD_MODIFY_AVATAR, params,
					new AjaxCallBack<Avatar>(){
				
						@Override
						public void onSuccess(Avatar t) {							
							DebugConfig.showLog("loadFile", t.avatarUrl);
							AvatarParser parser = new AvatarParser();
							parser.parse(t.toString());	
						}				
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 处理在拍照时屏幕翻转的问题
	 */
	public void onConfigurationChanged(Configuration newConfig) {  

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {   
            Configuration o = newConfig;  
            o.orientation = Configuration.ORIENTATION_PORTRAIT;  
            newConfig.setTo(o);  
        }   
        super.onConfigurationChanged(newConfig);  
    }
}
