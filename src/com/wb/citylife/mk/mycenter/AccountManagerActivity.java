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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.common.media.CarameHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Avatar;
import com.wb.citylife.bean.db.User;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
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
	private LinearLayout modifyGenderLayout;
	private Button logoutBtn;
	
	private ModifyAvatarDialog dialog;
	
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
		modifyGenderLayout = (LinearLayout) findViewById(R.id.modify_gender);
		logoutBtn = (Button) findViewById(R.id.logout);
		
		modifyAvatarLayout.setOnClickListener(this);
		modifyNicknameLayout.setOnClickListener(this);
		modifyPasswordLayout.setOnClickListener(this);
		modifyGenderLayout.setOnClickListener(this);
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
			dialog = new ModifyAvatarDialog(this, R.style.popupStyle);
			dialog.setListener(this);
			dialog.show();			
			break;
			
		case R.id.modify_nickname:
			startActivity(new Intent(this, ModifyNickNameActivity.class));
			break;
			
		case R.id.modify_password:
			startActivity(new Intent(this, ModifyPasswordActivity.class));
			break;
			
		case R.id.modify_gender:
			startActivity(new Intent(this, ModifyGenderActivity.class));
			break;
			
		case R.id.logout:
			//登出处理
			User user = CityLifeApp.getInstance().getUser();
			user.isLogin = 0;
			FinalDb finalDb = CityLifeApp.getInstance().getDb();
			finalDb.update(user, "userId='" + user.userId + "'");
			user = new User();
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
			dialog.dismiss();
			
		} else if(requestCode == ResultCode.REQUEST_CODE_IMAGE_CROP) {
			//上传头像
			if(resultCode == 0 && data == null) {
				ToastHelper.showToastInBottom(this, "您取消了上传头像操作");
			} else {
				upLoadAvatar(photoFile);
			}
			dialog.dismiss();
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
	    	photoFile = helper.getOutputMediaFile(this, CarameHelper.MEDIA_TYPE_IMAGE);   
	    	if(photoFile != null) {
	    		photoUri = Uri.fromFile(photoFile);
	    		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); 
	    		startActivityForResult(intent, ResultCode.REQUEST_CODE_CAPTURE_CAMEIA);
	    	}
	    } else {
	    	ToastHelper.showToastInBottom(this, R.string.sdcard_error);
	    }
	}
	
	/**
	 * 从相册中获取图片
	 */
	private void getImageFromAlbum() {
		CarameHelper helper = new CarameHelper(this);
		photoFile = helper.getOutputMediaFile(this, CarameHelper.MEDIA_TYPE_IMAGE);   
		if(photoFile == null) return;
		
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
			fh.addHeader("accessToken", "A0BAA87FCF5D187EC9582866B9AE1A3B");;
			fh.addHeader("connection", "keep-alive");
			fh.addHeader("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + NetInterface.METHOD_MODIFY_AVATAR;
			fh.post(url, params, new AjaxCallBack<String>(){
				
						@Override
						public void onSuccess(String result) {							
							AvatarParser parser = new AvatarParser();
							Avatar avatar = parser.parse(result);	
							if(avatar.respCode == RespCode.SUCCESS) {
								CityLifeApp.getInstance().getUser().avatarUrl = avatar.avatarUrl;
								ToastHelper.showToastInBottom(AccountManagerActivity.this, "头像上传成功");
							} else {
								ToastHelper.showToastInBottom(AccountManagerActivity.this, "头像上传失败");
							}
						}

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							ToastHelper.showToastInBottom(AccountManagerActivity.this, "头像上传失败");
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
