package com.wb.citylife.activity.base;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.BaseBean;
import com.wb.citylife.bean.Collect;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.task.BaseRequest;
import com.wb.citylife.task.CollectRequest;

public abstract class BaseDetailActivity extends BaseActivity implements ErrorListener, OnMenuItemClickListener{
	
	protected String detailId;
	protected String detailType;
	
	//收藏
	private CollectRequest mCollectRequest;
	private Collect mCollect;
	private MenuItem mColletcMenuItem;	
	protected int collectState = 0;
		
	//点赞
	private BaseRequest mBaseRequest;
	private MenuItem mFavourMenuItem;
	protected int favourState = 0;
	protected int favourNum = 0;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		mFavourMenuItem = setActionBarItem(menu, R.id.action_favour, R.string.action_favour, R.drawable.favour);
		setOverflowMenu(R.menu.browse_content_menu, R.drawable.actionbar_overflow_icon, this);	
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case R.id.action_favour:
			if(CityLifeApp.getInstance().checkLogin()) {
				if(favourState == 0) {
					requestFavour(Method.POST, NetInterface.METHOD_FAVOUR, getFavourRequestParams(1), new FavourListener(), this);
				} else {
					requestFavour(Method.POST, NetInterface.METHOD_FAVOUR, getFavourRequestParams(0), new FavourListener(), this);
				}
			} else {
				ToastHelper.showToastInBottom(this, R.string.favour_login_toast);
			}
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.share:
			break;
			
		case R.id.collect:
			if(CityLifeApp.getInstance().checkLogin()) {
				if(collectState == 0) {
					requestCollect(Method.POST, NetInterface.METHOD_COLLECT, getCollectRequestParams(0), new CollectListener(), this);
				} else {
					requestCollect(Method.POST, NetInterface.METHOD_COLLECT, getCollectRequestParams(1), new CollectListener(), this);
				}
			} else {
				ToastHelper.showToastInBottom(this, R.string.need_login_toast);
			}
			break;
		}
		return false;
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getFavourRequestParams(int option) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().userId);
		params.put("id", detailId);
		params.put("type", "1");
		params.put("option", option + "");
		return params;
	}
	
	/**
	 * 执行任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestFavour(int method, String methodUrl, Map<String, String> params,	 
			Listener<BaseBean> listenre, ErrorListener errorListener) {			
		if(mBaseRequest != null) {
			mBaseRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mBaseRequest = new BaseRequest(method, url, params, listenre, errorListener);
		startRequest(mBaseRequest);		
	}
	
	class FavourListener implements Listener<BaseBean> {

		@Override
		public void onResponse(BaseBean baseBean) {
			
			if(baseBean.respCode == RespCode.SUCCESS) {
				if(favourState == 0) {
					favourState = 1;
					favourNum++;
					mFavourMenuItem.setIcon(getFavDrawable(R.drawable.favoured));
					ToastHelper.showToastInBottom(BaseDetailActivity.this, "感谢您的赞赏");
				} else {
					favourState = 0;
					favourNum--;
					mFavourMenuItem.setIcon(getFavDrawable(R.drawable.favour));
					ToastHelper.showToastInBottom(BaseDetailActivity.this, "您取消了赞赏");
				}
				
			} else {
				ToastHelper.showToastInBottom(BaseDetailActivity.this, baseBean.respMsg);
			}
		}		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 设置点赞的图像
	 * @param resId
	 * @return
	 */
	private Drawable getFavDrawable(int resId) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
		Bitmap newBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(newBmp);
		Paint textPaint = new Paint();
		textPaint.setTypeface(Typeface.MONOSPACE);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(30);
		textPaint.setColor(0xff5c5c5c);
		canvas.drawText(favourNum+"", 70.0f, 32.0f, textPaint);
		Drawable drawable = new BitmapDrawable(getResources(), newBmp);
		
		return drawable;
	}
	
	/**
	 * 获取收藏请求参数
	 * @return
	 */
	private Map<String, String> getCollectRequestParams(int option) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("option", option+"");			
		params.put("userId", CityLifeApp.getInstance().getUser().getUserId());
		params.put("id", detailId);
		params.put("type", detailType+"");
		return params;
	}
	
	/**
	 * 执行收藏任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestCollect(int method, String methodUrl, Map<String, String> params,	 
			Listener<Collect> listenre, ErrorListener errorListener) {			
		if(mCollectRequest != null) {
			mCollectRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mCollectRequest = new CollectRequest(method, url, params, listenre, errorListener);
		startRequest(mCollectRequest);		
	}
	
	/**
	 * 收藏的请求处理
	 * @author liangbx
	 *
	 */
	class CollectListener implements Listener<Collect> {

		@Override
		public void onResponse(Collect collect) {
			if(collect.respCode == RespCode.SUCCESS) {
				if(collectState == 0) {
					collectState = 1;
					mColletcMenuItem.setIcon(R.drawable.collect_icon);
					mColletcMenuItem.setTitle(R.string.collect_cancle);
					ToastHelper.showToastInBottom(BaseDetailActivity.this, R.string.collect_success);
				} else {
					collectState = 0;
					mColletcMenuItem.setIcon(R.drawable.un_collect_icon);
					mColletcMenuItem.setTitle(R.string.collect);
					ToastHelper.showToastInBottom(BaseDetailActivity.this, R.string.colletc_cancle_success);
				}
			} else {
				ToastHelper.showToastInBottom(BaseDetailActivity.this, collect.respMsg);
			}
		}
		
	}
}
