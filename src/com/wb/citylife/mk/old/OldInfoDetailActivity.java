package com.wb.citylife.mk.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.wb.citylife.bean.ImagesItem;
import com.wb.citylife.bean.OldInfoDetail;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.mk.comment.CommentListActivity;
import com.wb.citylife.mk.common.CommDrawable;
import com.wb.citylife.mk.news.NewsDetailActivity;
import com.wb.citylife.task.BaseRequest;
import com.wb.citylife.task.CollectRequest;
import com.wb.citylife.task.CommentListRequest;
import com.wb.citylife.task.CommentRequest;
import com.wb.citylife.task.OldInfoDetailRequest;
import com.wb.citylife.widget.ListViewForScrollView;

public class OldInfoDetailActivity extends BaseActivity implements OnClickListener,
	Listener<OldInfoDetail>, ErrorListener, ReloadListener, OnMenuItemClickListener {
		
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
	private TextView contactTv;
	
	private String id;
	
	//二手市场详情
	private OldInfoDetailRequest mOldInfoDetailRequest;
	private OldInfoDetail mOldInfoDetail;		
	
	//最新评论
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
		setContentView(R.layout.activity_oldinfodetail);
		
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
		contactTv = (TextView) findViewById(R.id.contactInfo);
		
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
				} else if(position == mOldInfoDetail.imagesUrl.length-1) {
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
		
		requestOldInfoDetail(Method.POST, NetInterface.METHOD_OLD_INFO_DETAIL, 
				getOldInfoDetailRequestParams(), this, this);			
		
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
		case R.id.action_favour:
			if(CityLifeApp.getInstance().checkLogin()) {
				if(mOldInfoDetail.favourState == 0) {
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
				if(mOldInfoDetail.collectState == 0) {
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
			startActivity(intent);
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
			if(currentItem < mOldInfoDetail.imagesUrl.length) {
				imgPager.setCurrentItem(currentItem);
				setImgeNum(currentItem);
				if(currentItem >= mOldInfoDetail.imagesUrl.length-1) {
					rightBtn.setVisibility(View.GONE);
				}
				leftBtn.setVisibility(View.VISIBLE);
			} 
		}break;
		}
	}
	
	private void setImgeNum(int position) {
		imgNumTv.setText((position + 1) + "/" + mOldInfoDetail.imagesUrl.length);
	}

	@Override
	public void onReload() {
		requestOldInfoDetail(Method.POST, NetInterface.METHOD_OLD_INFO_DETAIL, 
				getOldInfoDetailRequestParams(), this, this);	
		
		commentPageInfo = new PageInfo(5, 1);
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, 
				getCommentListRequestParams(), new CommentListListener(), this);
		showLoading();
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getOldInfoDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);		
		params.put("phoneId", CityLifeApp.getInstance().getPhoneId());
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
	private void requestOldInfoDetail(int method, String methodUrl, Map<String, String> params,	 
			Listener<OldInfoDetail> listenre, ErrorListener errorListener) {			
		if(mOldInfoDetailRequest != null) {
			mOldInfoDetailRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mOldInfoDetailRequest = new OldInfoDetailRequest(method, url, params, listenre, errorListener);
		startRequest(mOldInfoDetailRequest);		
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
	public void onResponse(OldInfoDetail response) {		
		setIndeterminateBarVisibility(false);
		if(response.respCode == RespCode.SUCCESS) {			
			mOldInfoDetail = response;
			mImgAdapter = new ImageAdapter(this, mOldInfoDetail.imagesUrl);
			imgPager.setAdapter(mImgAdapter);
			imgNumTv.setText("1/" + mOldInfoDetail.imagesUrl.length);
			
			titleTv.setText(mOldInfoDetail.title);
			usernameTv.setText(mOldInfoDetail.userName);
			timeTv.setText(mOldInfoDetail.time);
			priceTv.setText("￥" + mOldInfoDetail.price + "");
			commentTv.setText("评论 " + mOldInfoDetail.commentNum + "");
			clickTv.setText("点击 " + mOldInfoDetail.clickNum + "");
			detailTv.setText(mOldInfoDetail.content);
			contactTv.setText(mOldInfoDetail.contactInfo);
			
			if(mOldInfoDetail.imagesUrl.length <= 1) {
				leftBtn.setVisibility(View.GONE);
				rightBtn.setVisibility(View.GONE);
			}
			showContent();
			
			if(mOldInfoDetail.favourState == 0) {
				mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(OldInfoDetailActivity.this, R.drawable.favour, mOldInfoDetail.favourNum));
			} else {
				mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(OldInfoDetailActivity.this, R.drawable.favoured, mOldInfoDetail.favourNum));
			}		
			
			mColletcMenuItem = getOverflowMenuItem(1);
			if(mOldInfoDetail.collectState == 0) {
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
			mCommentList = commentList;
			mCommentAdapter = new CommentAdapter(OldInfoDetailActivity.this, mCommentList);
			commentLv.setAdapter(mCommentAdapter);
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
		params.put("type", ChannelType.CHANNEL_TYPE_OLD_MARKET+"");
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
				ToastHelper.showToastInBottom(OldInfoDetailActivity.this, R.string.comment_success);
			} else {
				ToastHelper.showToastInBottom(OldInfoDetailActivity.this, R.string.comment_fail);
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
				if(mOldInfoDetail.collectState == 0) {
					mOldInfoDetail.collectState = 1;
					mColletcMenuItem.setIcon(R.drawable.collect_icon);
					mColletcMenuItem.setTitle(R.string.collect_cancle);
					ToastHelper.showToastInBottom(OldInfoDetailActivity.this, R.string.collect_success);
				} else {
					mOldInfoDetail.collectState = 0;
					mColletcMenuItem.setIcon(R.drawable.un_collect_icon);
					mColletcMenuItem.setTitle(R.string.collect);
					ToastHelper.showToastInBottom(OldInfoDetailActivity.this, R.string.colletc_cancle_success);
				}
			} else {
				ToastHelper.showToastInBottom(OldInfoDetailActivity.this, collect.respMsg);
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
				if(mOldInfoDetail.favourState == 0) {
					mOldInfoDetail.favourState = 1;
					mOldInfoDetail.favourNum++;
					mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(OldInfoDetailActivity.this, R.drawable.favoured, mOldInfoDetail.favourNum));
					ToastHelper.showToastInBottom(OldInfoDetailActivity.this, R.string.favour_toast);
				} else {
					mOldInfoDetail.favourState = 0;
					mOldInfoDetail.favourNum--;
					mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(OldInfoDetailActivity.this, R.drawable.favour, mOldInfoDetail.favourNum));
					ToastHelper.showToastInBottom(OldInfoDetailActivity.this, R.string.favour_cancle_toast);
				}
				
			} else {
				ToastHelper.showToastInBottom(OldInfoDetailActivity.this, baseBean.respMsg);
			}
		}
		
	}
}
