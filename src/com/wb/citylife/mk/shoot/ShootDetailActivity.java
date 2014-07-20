package com.wb.citylife.mk.shoot;

import java.util.HashMap;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.CommentAdapter;
import com.wb.citylife.adapter.ImageAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.BaseBean;
import com.wb.citylife.bean.Collect;
import com.wb.citylife.bean.Comment;
import com.wb.citylife.bean.CommentList;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.bean.ShootDetail;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.dialog.ConfirmDialog;
import com.wb.citylife.mk.comment.CommentListActivity;
import com.wb.citylife.mk.common.CommDrawable;
import com.wb.citylife.mk.common.CommShare;
import com.wb.citylife.mk.news.NewsDetailActivity;
import com.wb.citylife.mk.old.OldInfoDetailActivity.CommentListener;
import com.wb.citylife.task.BaseRequest;
import com.wb.citylife.task.CollectRequest;
import com.wb.citylife.task.CommentListRequest;
import com.wb.citylife.task.CommentRequest;
import com.wb.citylife.task.ShootDetailRequest;
import com.wb.citylife.widget.ListViewForScrollView;

public class ShootDetailActivity extends BaseActivity implements OnClickListener,
	Listener<ShootDetail>, ErrorListener, ReloadListener, OnMenuItemClickListener {
		
	private TextView titleTv;
	private TextView imgNumTv;
	private ImageButton leftBtn;
	private ImageButton rightBtn;
	private ViewPager imgPager;
	private ImageAdapter mImgAdapter;
	
	private TextView usernameTv;
	private TextView timeTv;
	private TextView priceTv;
	private TextView commentTv;
	private TextView clickTv;
	private TextView detailTv;
	
	private String id;
		
	//随手拍详情
	private ShootDetailRequest mShootDetailRequest;
	private ShootDetail mShootDetail;
	
	//最新评论
	private ViewGroup commentListVg;
	private ListViewForScrollView commentLv;
	private CommentListRequest mCommentListRequest;
	private CommentList mCommentList;
	private CommentAdapter mCommentAdapter;
	private PageInfo commentPageInfo;
	
	//发表评论
	private EditText commentEt;
	private Button commentBtn;
	private CommentRequest mCommentRequest;
	private Comment mComment;
	
	//收藏
	private CollectRequest mCollectRequest;
	private Collect mCollect;
	private MenuItem mColletcMenuItem;
	
	//点赞
	private BaseRequest mBaseRequest;
	private MenuItem mFavourMenuItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shootdetail);
		
		getIntentData();
		initView();
		
		showLoading();
	}
	
	@Override
	public void getIntentData() {
		id = getIntent().getStringExtra(IntentExtraConfig.DETAIL_ID);
	}

	@Override
	public void initView() {		
		titleTv = (TextView) findViewById(R.id.title);
		
		imgNumTv = (TextView) findViewById(R.id.imgNum);
		leftBtn = (ImageButton) findViewById(R.id.leftBtn);
		leftBtn.setVisibility(View.GONE);
		leftBtn.setOnClickListener(this);
		rightBtn = (ImageButton) findViewById(R.id.rightBtn);
		rightBtn.setOnClickListener(this);
		imgPager = (ViewPager) findViewById(R.id.pager);
		
		usernameTv = (TextView) findViewById(R.id.username);
		timeTv = (TextView) findViewById(R.id.time);
		priceTv = (TextView) findViewById(R.id.price);
		commentTv = (TextView) findViewById(R.id.comment);
		clickTv = (TextView) findViewById(R.id.click);
		detailTv = (TextView) findViewById(R.id.detail);
		
		commentListVg = (ViewGroup) findViewById(R.id.comment_list_layout);
		commentLv = (ListViewForScrollView) findViewById(R.id.comment_list);
		commentEt = (EditText) findViewById(R.id.comment_et);
		commentBtn = (Button) findViewById(R.id.comment_btn);
		commentBtn.setOnClickListener(this);
		
		View bottomView = LayoutInflater.from(this).inflate(R.layout.bottom_click_layout, null);
		commentLv.addFooterView(bottomView);
		Button clickBtn = (Button)bottomView.findViewById(R.id.click);
		clickBtn.setOnClickListener(this);
		
		imgPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				setImgeNum(position);
				if(position == 0) {					
					leftBtn.setVisibility(View.GONE);
					rightBtn.setVisibility(View.VISIBLE);
				} else if(position == mShootDetail.imagesUrl.length-1) {
					leftBtn.setVisibility(View.VISIBLE);
					rightBtn.setVisibility(View.GONE);
				} else {
					leftBtn.setVisibility(View.VISIBLE);
					rightBtn.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				
			}
		});
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		mFavourMenuItem = setActionBarItem(menu, R.id.action_favour, R.string.action_favour, R.drawable.favour);
		//此处设置ActionBar的菜单按钮
		setOverflowMenu(R.menu.browse_content_menu, R.drawable.actionbar_overflow_icon, this);	
		
		requestShootDetail(Method.POST, NetInterface.METHOD_SHOOT_DETAIL, 
				getShootDetailRequestParams(), this, this);			
		
		commentPageInfo = new PageInfo(5, 1);
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, 
				getCommentListRequestParams(), new CommentListListener(), this);
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
		
		case R.id.action_favour:
			if(CityLifeApp.getInstance().checkLogin()) {
				if(mShootDetail.favourState == 0) {
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
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		
		switch(keyCode) {
		case KeyEvent.KEYCODE_BACK:
		    checkFinish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.share:
			String content = "我在城市生活看到一条随手拍信息：" + mShootDetail.title 
				+ ", " + mShootDetail.content 
				+ "\n详情内容请下载城市生活应用：" + NetConfig.APK_DOWNLOAD_URL;
			CommShare.share(this, content, false);
			break;
			
		case R.id.collect:
			if(CityLifeApp.getInstance().checkLogin()) {
				if(mShootDetail.collectState == 0) {
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
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch(v.getId()) {
		//点击提交评论
		case R.id.comment_btn:{
			if(!CityLifeApp.getInstance().checkLogin()) {
				ToastHelper.showToastInBottom(this, R.string.comment_login_toast);
				return;
			}
			
			String comment = commentEt.getText().toString();
			if(comment != null && !comment.equals("")) {
				if(comment.length() > 140) {
					ConfirmDialog dialog = new ConfirmDialog();
		    		dialog.getConfirmDialog(this, "提示", "抱歉，您的评论字数超过140字限制，请重新编辑,谢谢~").show();
		    	} else{
		    		requestComment(Method.POST, NetInterface.METHOD_COMMENT, getCommentRequestParams(comment), new CommentListener(), this);	
		    	}					
			} else {
				ToastHelper.showToastInBottom(this, R.string.comment_empty_toast);
			}	
		}break;		
				
		case R.id.click:{
			Intent intent = new Intent(this, CommentListActivity.class);
			intent.putExtra(IntentExtraConfig.COMMENT_ID, id);
			intent.putExtra(IntentExtraConfig.COMMENT_TYPE, ChannelType.CHANNEL_TYPE_SHOOT);
			startActivityForResult(intent, 0);
		}break;
		
		case R.id.leftBtn:{
			int currentItem = imgPager.getCurrentItem();
			currentItem--;
			if(currentItem >= 0) {
				imgPager.setCurrentItem(currentItem);
				setImgeNum(currentItem);
				if(currentItem == 0) {
					leftBtn.setVisibility(View.GONE);
				}
				rightBtn.setVisibility(View.VISIBLE);
			}
		}break;
			
		case R.id.rightBtn:{
			int currentItem = imgPager.getCurrentItem();
			currentItem++;
			if(currentItem < mShootDetail.imagesUrl.length) {
				imgPager.setCurrentItem(currentItem);
				setImgeNum(currentItem);
				if(currentItem >= mShootDetail.imagesUrl.length-1) {
					rightBtn.setVisibility(View.GONE);
				}
				leftBtn.setVisibility(View.VISIBLE);
			} 
		}break;
		}
	}
	
	private void setImgeNum(int position) {
		imgNumTv.setText((position + 1) + "/" + mShootDetail.imagesUrl.length);
	}

	@Override
	public void onReload() {
		requestShootDetail(Method.POST, NetInterface.METHOD_SHOOT_DETAIL, 
				getShootDetailRequestParams(), this, this);	
		
		commentPageInfo = new PageInfo(5, 1);
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, 
				getCommentListRequestParams(), new CommentListListener(), this);
		
		showLoading();
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getShootDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);		
		params.put("phoneId", CityLifeApp.getInstance().getPhoneId());		
		params.put("type", ChannelType.CHANNEL_TYPE_SHOOT+"");
		params.put(RespParams.USER_ID, CityLifeApp.getInstance().getUser().userId);
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
	private void requestShootDetail(int method, String methodUrl, Map<String, String> params,	 
			Listener<ShootDetail> listenre, ErrorListener errorListener) {			
		if(mShootDetailRequest != null) {
			mShootDetailRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mShootDetailRequest = new ShootDetailRequest(method, url, params, listenre, errorListener);
		startRequest(mShootDetailRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
		showLoadError(this);
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(ShootDetail response) {
		mShootDetail = response;
		setIndeterminateBarVisibility(false);
		if(response.respCode == RespCode.SUCCESS) {
			
			//判断是否已被用户删除掉
			if(TextUtils.isEmpty(response.title) && TextUtils.isEmpty(response.content)) {
				ToastHelper.showToastInBottom(this, "抱歉这条信息可能已经被用户删除了，你下拉更新一下列表吧~");
				finish();
				return;
			}
			
			if(response.imagesUrl == null || response.imagesUrl.length == 0) {
				response.imagesUrl = new String[1];
			}
			
			mShootDetail = response;			
			mImgAdapter = new ImageAdapter(this, mShootDetail.imagesUrl);
			imgPager.setAdapter(mImgAdapter);
			imgNumTv.setText("1/" + mShootDetail.imagesUrl.length);
			if(mShootDetail.imagesUrl.length == 1) {
				leftBtn.setVisibility(View.GONE);
				rightBtn.setVisibility(View.GONE);
			}
			
			titleTv.setText(mShootDetail.title);
			usernameTv.setText(mShootDetail.userName);
			timeTv.setText(mShootDetail.time);
			commentTv.setText("评论 " + mShootDetail.commentNum + "");
			clickTv.setText("点击 " + mShootDetail.clickNum + "");
			detailTv.setText(mShootDetail.content);
			showContent();
			
			if(mShootDetail.favourState == 0) {
				mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(ShootDetailActivity.this, R.drawable.favour, mShootDetail.favourNum));
			} else {
				mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(ShootDetailActivity.this, R.drawable.favoured, mShootDetail.favourNum));
			}		
			
			mColletcMenuItem = getOverflowMenuItem(1);
			if(mShootDetail.collectState == 0) {
				mColletcMenuItem.setIcon(R.drawable.un_collect_icon);
				mColletcMenuItem.setTitle(R.string.collect);
			} else {
				mColletcMenuItem.setIcon(R.drawable.collect_icon);
				mColletcMenuItem.setTitle(R.string.collect_cancle);
			}
		} else {
			showLoadError(this);
		}
	}
			
	/**
	 * 获取评论请求参数
	 * @return
	 */
	private Map<String, String> getCommentListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.PAGE_SIZE, commentPageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, commentPageInfo.pageNo+"");		
		params.put("id", id);
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
	private void requestCommentList(int method, String methodUrl, Map<String, String> params,	 
			Listener<CommentList> listenre, ErrorListener errorListener) {			
		if(mCommentListRequest != null) {
			mCommentListRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mCommentListRequest = new CommentListRequest(method, url, params, listenre, errorListener);
		startRequest(mCommentListRequest);		
	}
	
	/**
	 * 评论数据更新
	 * @author liangbx
	 *
	 */
	class CommentListListener implements Listener<CommentList> {

		@Override
		public void onResponse(CommentList commentList) {
			if(commentList.totalNum > 0) {
				commentListVg.setVisibility(View.VISIBLE);
				mCommentList = commentList;
				mCommentAdapter = new CommentAdapter(ShootDetailActivity.this, mCommentList);
				commentLv.setAdapter(mCommentAdapter);
			} else {
				commentListVg.setVisibility(View.GONE);
			}
		}		
	}
		
	/**
	 * 获取评论参数请求参数
	 * @return
	 */
	private Map<String, String> getCommentRequestParams(String comment) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().userId);
		params.put("id", id);
		params.put("comment", comment);
		params.put("type", ChannelType.CHANNEL_TYPE_SHOOT+"");
		return params;
	}
	
	/**
	 * 执行评论任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestComment(int method, String methodUrl, Map<String, String> params,	 
			Listener<Comment> listenre, ErrorListener errorListener) {			
		if(mCommentRequest != null) {
			mCommentRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mCommentRequest = new CommentRequest(method, url, params, listenre, errorListener);
		startRequest(mCommentRequest);		
	}
	
	/**
	 * 提交评论结果
	 * @author liangbx
	 *
	 */
	class CommentListener implements Listener<Comment> {

		@Override
		public void onResponse(Comment comment) {
			if(comment.respCode == RespCode.SUCCESS) {
				commentEt.setText("");
				ToastHelper.showToastInBottom(ShootDetailActivity.this, R.string.comment_success);
				
				//刷新评论列表
				commentPageInfo = new PageInfo(5, 1);
				requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
						new CommentListListener(), ShootDetailActivity.this);
			} else {
				ToastHelper.showToastInBottom(ShootDetailActivity.this, R.string.comment_fail);
			}
		}
		
	}	
	
	/**
	 * 获取收藏请求参数
	 * @return
	 */
	private Map<String, String> getCollectRequestParams(int option) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("option", option+"");			
		params.put("userId", CityLifeApp.getInstance().getUser().getUserId());
		params.put("id", id);
		params.put("type", ChannelType.CHANNEL_TYPE_SHOOT+"");
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
				if(mShootDetail.collectState == 0) {
					mShootDetail.collectState = 1;
					mColletcMenuItem.setIcon(R.drawable.collect_icon);
					mColletcMenuItem.setTitle(R.string.collect_cancle);
					ToastHelper.showToastInBottom(ShootDetailActivity.this, R.string.collect_success);
				} else {
					mShootDetail.collectState = 0;
					mColletcMenuItem.setIcon(R.drawable.un_collect_icon);
					mColletcMenuItem.setTitle(R.string.collect);
					ToastHelper.showToastInBottom(ShootDetailActivity.this, R.string.colletc_cancle_success);
				}
			} else {
				ToastHelper.showToastInBottom(ShootDetailActivity.this, collect.respMsg);
			}
		}
		
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getFavourRequestParams(int option) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().userId);
		params.put("id", id);
		params.put("type", ChannelType.CHANNEL_TYPE_SHOOT+"");
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
				if(mShootDetail.favourState == 0) {
					mShootDetail.favourState = 1;
					mShootDetail.favourNum++;
					mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(ShootDetailActivity.this, R.drawable.favoured, mShootDetail.favourNum));
					ToastHelper.showToastInBottom(ShootDetailActivity.this, R.string.favour_toast);
				} else {
					mShootDetail.favourState = 0;
					mShootDetail.favourNum--;
					mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(ShootDetailActivity.this, R.drawable.favour, mShootDetail.favourNum));
					ToastHelper.showToastInBottom(ShootDetailActivity.this, R.string.favour_cancle_toast);
				}
				
			} else {
				ToastHelper.showToastInBottom(ShootDetailActivity.this, baseBean.respMsg);
			}
		}		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == ResultCode.REFRESH_COMMENT_LIST) {
			//刷新评论列表
			commentPageInfo = new PageInfo(5, 1);
			requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
					new CommentListListener(), ShootDetailActivity.this);
		}
	}
	
	private void checkFinish() {
		String comment = commentEt.getText().toString();
		if(!TextUtils.isEmpty(comment)) {
	    	ConfirmDialog dialog = new ConfirmDialog();	    	
	    	dialog.getDialog(this, "提示", "您还有未发表的评论，确认要退出吗？", new DialogInterface.OnClickListener(){

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
