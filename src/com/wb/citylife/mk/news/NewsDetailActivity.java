package com.wb.citylife.mk.news;

import java.util.HashMap;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.common.net.HttpHelper;
import com.common.net.check.CheckNetwork;
import com.common.net.volley.VolleyErrorCode;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.CommentAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.BaseBean;
import com.wb.citylife.bean.Collect;
import com.wb.citylife.bean.Comment;
import com.wb.citylife.bean.CommentList;
import com.wb.citylife.bean.NewsDetail;
import com.wb.citylife.bean.PageInfo;
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
import com.wb.citylife.mk.video.VideoActivity;
import com.wb.citylife.task.BaseRequest;
import com.wb.citylife.task.CollectRequest;
import com.wb.citylife.task.CommentListRequest;
import com.wb.citylife.task.CommentRequest;
import com.wb.citylife.task.NewsDetailRequest;
import com.wb.citylife.widget.ListViewForScrollView;

public class NewsDetailActivity extends BaseActivity implements Listener<NewsDetail>, ErrorListener,
	OnClickListener, OnMenuItemClickListener, ReloadListener{
				
	private String id;
	private int type;
	
	//新闻详情
	private TextView titleTv;
	private TextView timeTv;
	private NetworkImageView imgIv;
	private ViewGroup imgVg;
	private WebView contentWv;
	private NewsDetailRequest mNewsDetailRequest;
	private NewsDetail mNewsDetail;
	
	//最新评论
	private ViewGroup commentListVg;
	private ListViewForScrollView commentLv;
	private CommentListRequest mCommentListRequest;
	private CommentList mCommentList;
	private CommentAdapter mCommentAdapter;
	private PageInfo commentPageInfo;		
	
	//发表评论
	private EditText commentEt;
	private Button sendBtn;
	private CommentRequest mCommentRequest;
	private Comment mComment;
	
	//收藏
	private CollectRequest mCollectRequest;
	private Collect mCollect;
	private MenuItem mColletcMenuItem;
	
	//点赞
	private BaseRequest mBaseRequest;
	private MenuItem mFavourMenuItem;
	
	private Handler mHandler = new Handler();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsdetail);
		
		getIntentData();
		initView();				
		initWebView();
		
		showLoading();
	}
			
	@Override
	public void getIntentData() {
		id = getIntent().getStringExtra(IntentExtraConfig.DETAIL_ID);
		type = getIntent().getIntExtra(IntentExtraConfig.DETAIL_TYPE, -1);
	}
	
	@Override
	public void initView() {
		titleTv = (TextView) findViewById(R.id.title);
		timeTv = (TextView) findViewById(R.id.time);
		imgIv = (NetworkImageView) findViewById(R.id.img);
		imgVg = (RelativeLayout) findViewById(R.id.img_layout);
		imgVg.setOnClickListener(this);
		contentWv = (WebView) findViewById(R.id.content);
		
		commentListVg = (ViewGroup) findViewById(R.id.comment_list_layout);
		commentLv = (ListViewForScrollView) findViewById(R.id.comment_list);
		commentEt = (EditText) findViewById(R.id.comment_et);
		sendBtn = (Button) findViewById(R.id.comment_btn);
		sendBtn.setOnClickListener(this);
		
		View bottomView = LayoutInflater.from(this).inflate(R.layout.bottom_click_layout, null);
		commentLv.addFooterView(bottomView);
		Button clickBtn = (Button)bottomView.findViewById(R.id.click);
		clickBtn.setOnClickListener(this);				
	}
	
	private void initWebView() {
		
		contentWv.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				
				if(newProgress == 100) {
					showContent();
				}
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
		
		//网络请求
		requestNewsDetail(Method.POST, NetInterface.METHOD_NEWS_DETAIL, getNewsDetailRequestParams(), this, this);
		commentPageInfo = new PageInfo(5, 1);
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), new CommentListListener(), this);
		
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
				if(mNewsDetail.favourState == 0) {
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
			String content = "我在城市生活看到一条新闻资讯：" + mNewsDetail.title 
					+ "\n详情内容请下载城市生活应用：" + NetConfig.APK_DOWNLOAD_URL;
			CommShare.share(this, content, false);
			break;
			
		case R.id.collect:
			if(CityLifeApp.getInstance().checkLogin()) {
				if(mNewsDetail.collectState == 0) {
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
				requestComment(Method.POST, NetInterface.METHOD_COMMENT, getCommentRequestParams(comment), new CommentListener(), this);						
			} else {
				ToastHelper.showToastInBottom(this, R.string.comment_empty_toast);
			}	
		}break;		
		
		case R.id.click:{
			Intent intent = new Intent(this, CommentListActivity.class);
			intent.putExtra(IntentExtraConfig.COMMENT_ID, id);
			intent.putExtra(IntentExtraConfig.COMMENT_TYPE, ChannelType.CHANNEL_TYPE_NEWS);
			startActivityForResult(intent, 0);
		}break;
		
		case R.id.img_layout:{
			if(!HttpHelper.netwokAvaiable(this)) {
				ToastHelper.showToastInBottom(this, VolleyErrorCode.NO_NETWORK_ERROR);
				return;
			}
			
			if(CheckNetwork.checkWifi(this)) {
				Intent intent = new Intent(this, VideoActivity.class);
				intent.putExtra(IntentExtraConfig.VIDEO_PATH, NetConfig.getPictureUrl(mNewsDetail.imagesUrl[1]));
				startActivity(intent);
			} else {
				//非Wifi状态下提示用户流量问题
				ConfirmDialog dialog = new ConfirmDialog();
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(NewsDetailActivity.this, VideoActivity.class);
						intent.putExtra(IntentExtraConfig.VIDEO_PATH, NetConfig.getPictureUrl(mNewsDetail.imagesUrl[1]));
						startActivity(intent);
					}
				};
				dialog.getDialog(this, R.string.toast, R.string.network_toast, listener).show();
			}
		}break;
		}				
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getNewsDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.ID, id);		
		params.put(RespParams.PHONE_ID, CityLifeApp.getInstance().getPhoneId());
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
	private void requestNewsDetail(int method, String methodUrl, Map<String, String> params,	 
			Listener<NewsDetail> listenre, ErrorListener errorListener) {			
		if(mNewsDetailRequest != null) {
			mNewsDetailRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mNewsDetailRequest = new NewsDetailRequest(method, url, params, listenre, errorListener);
		startRequest(mNewsDetailRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {	
		showLoadError(this);
		setIndeterminateBarVisibility(false);		
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 加载失败重，点击重新加载的处理
	 */
	@Override
	public void onReload() {
		requestNewsDetail(Method.POST, NetInterface.METHOD_NEWS_DETAIL, getNewsDetailRequestParams(), this, this);
		commentPageInfo = new PageInfo(5, 1);
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), new CommentListListener(), this);
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(NewsDetail response) {
		mNewsDetail = response;
		setIndeterminateBarVisibility(false);
		
		titleTv.setText(mNewsDetail.title);
		timeTv.setText(mNewsDetail.time);
		if(mNewsDetail.type == 0) {
			imgIv.setVisibility(View.GONE);
			imgVg.setVisibility(View.GONE);
		} else {
			imgIv.setVisibility(View.VISIBLE);			
			imgIv.setImageUrl(mNewsDetail.imagesUrl[0], CityLifeApp.getInstance().getImageLoader());
			imgVg.setVisibility(View.VISIBLE);
		}
		
		contentWv.loadDataWithBaseURL(NetConfig.getServerBaseUrl(), mNewsDetail.content, null, "utf-8", null);
		
		if(mNewsDetail.favourState == 0) {
			mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(NewsDetailActivity.this, R.drawable.favour, mNewsDetail.favourNum));
		} else {
			mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(NewsDetailActivity.this, R.drawable.favoured, mNewsDetail.favourNum));
		}		
		
		mColletcMenuItem = getOverflowMenuItem(1);
		if(mNewsDetail.collectState == 0) {
			mColletcMenuItem.setIcon(R.drawable.un_collect_icon);
			mColletcMenuItem.setTitle(R.string.collect);
		} else {
			mColletcMenuItem.setIcon(R.drawable.collect_icon);
			mColletcMenuItem.setTitle(R.string.collect_cancle);
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
				mCommentAdapter = new CommentAdapter(NewsDetailActivity.this, mCommentList);
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
		params.put("type", ChannelType.CHANNEL_TYPE_NEWS+"");
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
				ToastHelper.showToastInBottom(NewsDetailActivity.this, R.string.comment_success);
				
				//刷新评论列表
				commentPageInfo = new PageInfo(5, 1);
				requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
						new CommentListListener(), NewsDetailActivity.this);
			} else {
				ToastHelper.showToastInBottom(NewsDetailActivity.this, R.string.comment_fail);
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
		params.put("type", ChannelType.CHANNEL_TYPE_NEWS+"");
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
				if(mNewsDetail.collectState == 0) {
					mNewsDetail.collectState = 1;
					mColletcMenuItem.setIcon(R.drawable.collect_icon);
					mColletcMenuItem.setTitle(R.string.collect_cancle);
					ToastHelper.showToastInBottom(NewsDetailActivity.this, R.string.collect_success);
				} else {
					mNewsDetail.collectState = 0;
					mColletcMenuItem.setIcon(R.drawable.un_collect_icon);
					mColletcMenuItem.setTitle(R.string.collect);
					ToastHelper.showToastInBottom(NewsDetailActivity.this, R.string.colletc_cancle_success);
				}
			} else {
				ToastHelper.showToastInBottom(NewsDetailActivity.this, collect.respMsg);
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
		params.put("type", ChannelType.CHANNEL_TYPE_NEWS+"");
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
				if(mNewsDetail.favourState == 0) {
					mNewsDetail.favourState = 1;
					mNewsDetail.favourNum++;
					mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(NewsDetailActivity.this, R.drawable.favoured, mNewsDetail.favourNum));
					ToastHelper.showToastInBottom(NewsDetailActivity.this, R.string.favour_toast);
				} else {
					mNewsDetail.favourState = 0;
					mNewsDetail.favourNum--;
					mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(NewsDetailActivity.this, R.drawable.favour, mNewsDetail.favourNum));
					ToastHelper.showToastInBottom(NewsDetailActivity.this, R.string.favour_cancle_toast);
				}
				
			} else {
				ToastHelper.showToastInBottom(NewsDetailActivity.this, baseBean.respMsg);
			}
		}
		
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == ResultCode.REFRESH_COMMENT_LIST) {
			//刷新评论列表
			commentPageInfo = new PageInfo(5, 1);
			requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
					new CommentListListener(), NewsDetailActivity.this);
		}
	}
}
