package com.wb.citylife.mk.shoot;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.common.media.CarameHelper;
import com.common.widget.ToastHelper;
import com.common.widget.hzlib.HorizontalAdapterView;
import com.common.widget.hzlib.HorizontalAdapterView.OnItemClickListener;
import com.common.widget.hzlib.HorizontalListView;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.PhotoAdapter;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.dialog.AddPhotoDialog;

public class ShootPublishActivity extends BaseActivity implements OnItemClickListener,
	OnClickListener{
	
	public int maxNum = 6;
	public int itemWidth = 96;
	private int state = 0; // 0:新增  1：替换
	private int selPos;
	
	private HorizontalListView listView;
	private PhotoAdapter photoAdapter;
	private List<File> fileList = new ArrayList<File>();
	private List<SoftReference<Bitmap>> photoList = new ArrayList<SoftReference<Bitmap>>();
	
	private File photoFile;
	private Uri photoUri;
	
	private AddPhotoDialog optDialog;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoot_publish_layout);
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		
	}

	@Override
	public void initView() {
		listView = (HorizontalListView) findViewById(android.R.id.list);
		fileList.add(null);
		photoList.add(new SoftReference<Bitmap>(null));
		photoAdapter = new PhotoAdapter(this, fileList);
		listView.setAdapter(photoAdapter);		
		listView.setOnItemClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
					
		return super.onCreateOptionsMenu(menu);
	}
	
	

	@Override
	public void onItemClick(HorizontalAdapterView<?> parent, View view,
			int position, long id) {
		if(optDialog == null) {
			optDialog = new AddPhotoDialog(this, R.style.popupStyle);
			optDialog.setListener(this);
		}
		
		selPos = position;
		File file = fileList.get(position);
		if(file == null) {
			optDialog.hidDel();
			if(position == 0) {
				state = 0;
			} else {
				state = 1;
			}
		} else {
			state = 1;
			optDialog.showDel();
		}
		
		optDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.take_picture:
			optDialog.dismiss();
			getImageFromCamera();
			break;

		case R.id.photo_album:
			optDialog.dismiss();
			getImageFromAlbum();
			break;
			
		case R.id.photo_del:
			optDialog.dismiss();
			fileList.remove(selPos);			
			photoList.get(selPos).get().recycle();
			photoList.get(selPos).clear();
			photoList.remove(selPos);
			if(fileList.get(0) != null) {
				fileList.add(0, null);
				photoList.add(0, new SoftReference<Bitmap>(null));
			}
			photoAdapter.notifyDataSetChanged();
			break;
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
	    } 
	}
	
	/**
	 * 从相册中获取图片
	 */
	private void getImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		startActivityForResult(intent, ResultCode.REQUEST_CODE_PICK_IMAGE);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		
		if (requestCode == ResultCode.REQUEST_CODE_CAPTURE_CAMEIA ) {		
			if(photoFile.exists()) {
				if(state == 0) {
					fileList.add(photoFile);
					if(fileList.size() > maxNum) {
						fileList.remove(0);
						photoList.remove(0);
					}
				} else {
					fileList.set(selPos, photoFile);
					photoList.get(selPos).get().recycle();
					photoList.get(selPos).clear();
				}
				photoAdapter.notifyDataSetChanged();
			} else {
				ToastHelper.showToastInBottom(this, R.string.phot_load_fail);
			}
		} else if(requestCode == ResultCode.REQUEST_CODE_PICK_IMAGE) {
			if(data == null) return;
			
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			
			File file = new File(picturePath);
			if(file.exists()) {
				if(state == 0) {
					fileList.add(file);
					if(fileList.size() > maxNum) {
						fileList.remove(0);
						photoList.remove(0);
					}
				} else {
					fileList.set(selPos, file);
					photoList.get(selPos).get().recycle();
					photoList.get(selPos).clear();
				}
				photoAdapter.notifyDataSetChanged();
			} else {
				ToastHelper.showToastInBottom(this, R.string.phot_load_fail);
			}
			
		} else if(requestCode == ResultCode.REQUEST_CODE_IMAGE_CROP) {
			
		}
	}
	
	@Override	
	public void onDestroy() {
		if(photoAdapter != null) {
			photoAdapter.recycleBmp();
		}
		super.onDestroy();
	}
}
