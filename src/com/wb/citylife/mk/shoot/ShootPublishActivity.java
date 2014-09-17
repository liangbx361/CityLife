package com.wb.citylife.mk.shoot;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.media.BitmapHelper;
import com.common.media.CarameHelper;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.common.widget.hzlib.HorizontalAdapterView;
import com.common.widget.hzlib.HorizontalAdapterView.OnItemClickListener;
import com.common.widget.hzlib.HorizontalListView;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.PhotoAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.BaseBean;
import com.wb.citylife.bean.Publish;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.ImageConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.dialog.AddPhotoDialog;
import com.wb.citylife.dialog.ConfirmDialog;
import com.wb.citylife.mk.old.PublishOldInfoActivity;
import com.wb.citylife.parser.BaseParser;
import com.wb.citylife.task.PublishRequest;

public class ShootPublishActivity extends BaseActivity implements OnItemClickListener,
	OnClickListener, Listener<Publish>, ErrorListener{
	
	public int maxNum = 6;
	public int itemWidth = 96;
	private int state = 0; // 0:新增  1：替换
	private int selPos;
	
	private HorizontalListView listView;
	private PhotoAdapter photoAdapter;
	private List<File> fileList = new ArrayList<File>();
	private List<SoftReference<Bitmap>> photoList = new ArrayList<SoftReference<Bitmap>>();
	private int currentFileIndex;
	
	private File photoFile;
	private Uri photoUri;
	
	private AddPhotoDialog optDialog;
	
	private EditText titleEt;
	private EditText contentEt;
	private Button submitBtn;
	
	//发布二手信息
	private PublishRequest mPublishOldInfoRequest;
	private Publish mPublishOldInfo;
		
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
		photoAdapter = new PhotoAdapter(this, fileList, photoList);
		listView.setAdapter(photoAdapter);		
		listView.setOnItemClickListener(this);
		
		titleEt = (EditText) findViewById(R.id.title);
		contentEt = (EditText) findViewById(R.id.content);
		submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
					
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
				
		switch(item.getItemId()) {
		
		case android.R.id.home:
			checkFinish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		
		switch(keyCode) {
		case KeyEvent.KEYCODE_BACK:
		    checkFinish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
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
			
		case R.id.submit:
			submit();
			break;
		}
	}
	
	private void submit() {
		if(!CityLifeApp.getInstance().checkLogin()) {
			ToastHelper.showToastInBottom(this, R.string.login_toast);
			return;
		}
		
		String title = titleEt.getText().toString();		
		if(TextUtils.isEmpty(title)) {
			ToastHelper.showToastInBottom(this, R.string.title_empty_toast);
			return;
		}
						
		String desc = contentEt.getText().toString();
		if(TextUtils.isEmpty(desc)) {
			ToastHelper.showToastInBottom(this, R.string.desc_empty_toast);
			return;
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().userId);
		params.put("title", title);
		params.put("desc", desc);
		requestPublish(Method.POST, NetInterface.METHOD_PUBLISH_SHOOT, params, this, this);		
		
		showDialog("正在发布...");
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
					File file = BitmapHelper.getScaleBitmapFile(this, photoFile.getAbsolutePath(), ImageConfig.MAX_WIDTH);
					if(file != null) {
						fileList.add(file);
						if(fileList.size() > maxNum) {
							fileList.remove(0);
							photoList.remove(0);
						}
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
			
			File file = BitmapHelper.getScaleBitmapFile(this, picturePath, ImageConfig.MAX_WIDTH);
			if(file!= null && file.exists()) {
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
	
	/**
	 * 执行任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestPublish(int method, String methodUrl, Map<String, String> params,	 
			Listener<Publish> listenre, ErrorListener errorListener) {			
		if(mPublishOldInfoRequest != null) {
			mPublishOldInfoRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mPublishOldInfoRequest = new PublishRequest(method, url, params, listenre, errorListener);
		startRequest(mPublishOldInfoRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {	
		dismissDialog();
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(Publish response) {
		mPublishOldInfo = response;
		setIndeterminateBarVisibility(false);
		
		if(response.respCode == RespCode.SUCCESS) {
			if(fileList.size() > 1) {
				if(fileList.get(0) != null) {
					currentFileIndex = 0;
					upLoadPhoto(fileList.get(currentFileIndex), mPublishOldInfo.id);
				} else {
					currentFileIndex = 1;
					upLoadPhoto(fileList.get(currentFileIndex), mPublishOldInfo.id);
				}
			} else {
				dismissDialog();
				ToastHelper.showToastInBottom(ShootPublishActivity.this, R.string.publish_shoot_success);
				setResult(ResultCode.REFRESH_MY_OLD_LIST);
				finish();
			}
		} else {
			dismissDialog();
			ToastHelper.showToastInBottom(this, R.string.publish_fail);
		}			
	}
	
	/**
	 * 上传照片
	 * @param file
	 */
	private void upLoadPhoto(File file, String id) {		
		String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
		String PREFIX = "--" , LINE_END = "\r\n"; 
		String CONTENT_TYPE = "multipart/form-data";   //内容类型
		String suffixName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")+1);		

		try {
			AjaxParams params = new AjaxParams();
			params.put("userId", CityLifeApp.getInstance().getUser().getUserId());
			params.put("id", id);
			params.put("type", ChannelType.CHANNEL_TYPE_SHOOT+"");
			params.put("photo", file);			
			params.put("suffixName", suffixName);
			FinalHttp fh = new FinalHttp(); 
			fh.configTimeout(NetConfig.UPLOAD_IMG_TIMEOUT);
			fh.addHeader("accessToken", "A0BAA87FCF5D187EC9582866B9AE1A3B");;
			fh.addHeader("connection", "keep-alive");
			fh.addHeader("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + NetInterface.METHOD_PHOTO_UPLOAD;
			fh.post(url, params, new AjaxCallBack<String>(){
				
						@Override
						public void onSuccess(String result) {
							BaseParser baseParser = new BaseParser();
							BaseBean baseBean = baseParser.parse(result);
							currentFileIndex++;
							if(currentFileIndex < fileList.size()) {
								upLoadPhoto(fileList.get(currentFileIndex), mPublishOldInfo.id);
							} else {
								dismissDialog();
								ToastHelper.showToastInBottom(ShootPublishActivity.this, R.string.publish_shoot_success);
								setResult(ResultCode.REFRESH_MY_SHOOT_LIST);
								finish();
							}
						}
						
						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							dismissDialog();
							
							ConfirmDialog dialog = new ConfirmDialog();
							dialog.getDialog(ShootPublishActivity.this, "提示", "照片上传失败，是否重试?", 
									new DialogInterface.OnClickListener(){

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {											
											upLoadPhoto(fileList.get(currentFileIndex), mPublishOldInfo.id);
											showDialog("照片上传中...");
											arg0.dismiss();
										}
								
							}).show();
						}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private void checkFinish() {
		String title = titleEt.getText().toString();
		String content = contentEt.getText().toString();
		if(fileList.size() > 1 || !TextUtils.isEmpty(title) || !TextUtils.isEmpty(content)) {
			ConfirmDialog dialog = new ConfirmDialog();	    	
	    	dialog.getDialog(this, "提示", "您正在编辑随手拍信息，确认要退出吗？", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					finish();
				}
    			
    		}).show();
		} else {
			finish();
		}
	}
}
