package com.wb.citylife.mk.estate;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
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
import com.wb.citylife.bean.EstateDetail;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.mk.comment.CommentListActivity;
import com.wb.citylife.mk.common.CommDrawable;
import com.wb.citylife.mk.img.ImageBrowseActivity;
import com.wb.citylife.mk.old.OldInfoDetailActivity;
import com.wb.citylife.task.BaseRequest;
import com.wb.citylife.task.CollectRequest;
import com.wb.citylife.task.CommentListRequest;
import com.wb.citylife.task.CommentRequest;
import com.wb.citylife.task.EstateDetailRequest;
import com.wb.citylife.widget.ListViewForScrollView;

public class EstateDetailActivity extends BaseActivity implements Listener<EstateDetail>, ErrorListener,
	OnClickListener, ReloadListener, OnMenuItemClickListener{
	
	private TextView titleTv;
	private TextView priceTv;
	private TextView phoneTv;
	private TextView addressTv;
	private TextView saleAddressTv;
	private TextView detailTv;
	private NetworkImageView imgIv;
	private ViewGroup imagesVg;
	private ViewGroup videoVg;
	
	private String estateId;
	
	//详情
	private EstateDetailRequest mEstateDetailRequest;
	private EstateDetail mEstateDetail;	
	
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
		setContentView(R.layout.activity_estatedetail);
		
		getIntentData();
		initView();		
		
		showLoading();
	}
	
	@Override
	public void getIntentData() {
		estateId = getIntent().getStringExtra(IntentExtraConfig.DETAIL_ID);
	}
	
	@Override
	public void initView() {
		titleTv = (TextView) findViewById(R.id.title);
		priceTv = (TextView) findViewById(R.id.price);
		phoneTv = (TextView) findViewById(R.id.phone);
		addressTv = (TextView) findViewById(R.id.address);
		saleAddressTv = (TextView) findViewById(R.id.saleAddress);
		detailTv = (TextView) findViewById(R.id.detail);
		imgIv = (NetworkImageView) findViewById(R.id.img);
		imgIv.setOnClickListener(this);
		imagesVg = (ViewGroup) findViewById(R.id.images);		
		imagesVg.setOnClickListener(this);
		videoVg = (ViewGroup) findViewById(R.id.video);
		videoVg.setOnClickListener(this);
		videoVg.setVisibility(View.GONE);
		
		commentListVg = (ViewGroup) findViewById(R.id.comment_list_layout);
		commentLv = (ListViewForScrollView) findViewById(R.id.comment_list);
		commentEt = (EditText) findViewById(R.id.comment_et);
		commentBtn = (Button) findViewById(R.id.comment_btn);
		commentBtn.setOnClickListener(this);
		
		View bottomView = LayoutInflater.from(this).inflate(R.layout.bottom_click_layout, null);
		commentLv.addFooterView(bottomView);
		Button clickBtn = (Button)bottomView.findViewById(R.id.click);
		clickBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		mFavourMenuItem = setActionBarItem(menu, R.id.action_favour, R.string.action_favour, R.drawable.favour);
		//此处设置ActionBar的菜单按钮
		setOverflowMenu(R.menu.browse_content_menu, R.drawable.actionbar_overflow_icon, this);	
		
		requestEstateDetail(Method.POST, NetInterface.METHOD_ESTATE_DETAIL, 
				getEstateDetailRequestParams(), this, this);
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
				if(mEstateDetail.favourState == 0) {
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
				if(mEstateDetail.collectState == 0) {
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
		case R.id.images:{
			Intent intent = new Intent(this, ImageBrowseActivity.class);
			intent.putParcelableArrayListExtra(IntentExtraConfig.ESTATE_IMAGE_DATA, mEstateDetail.imagesUrl);
			startActivity(intent);
		}break;
		
		case R.id.image:{
			Intent intent = new Intent(this, ImageBrowseActivity.class);
			intent.putParcelableArrayListExtra(IntentExtraConfig.ESTATE_IMAGE_DATA, mEstateDetail.imagesUrl);
			startActivity(intent);
		}break;
		
		case R.id.video:{
			
		}
		
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
			intent.putExtra(IntentExtraConfig.COMMENT_ID, estateId);
			intent.putExtra(IntentExtraConfig.COMMENT_TYPE, ChannelType.CHANNEL_TYPE_ESTATE);
			startActivityForResult(intent, 0);
		}break;
		}				
	}
		
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getEstateDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", estateId);		
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
	private void requestEstateDetail(int method, String methodUrl, Map<String, String> params,	 
			Listener<EstateDetail> listenre, ErrorListener errorListener) {			
		if(mEstateDetailRequest != null) {
			mEstateDetailRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mEstateDetailRequest = new EstateDetailRequest(method, url, params, listenre, errorListener);
		startRequest(mEstateDetailRequest);		
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
	
	@Override
	public void onReload() {
		requestEstateDetail(Method.POST, NetInterface.METHOD_ESTATE_DETAIL, 
				getEstateDetailRequestParams(), this, this);
		commentPageInfo.pageNo = 1;
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, 
				getCommentListRequestParams(), new CommentListListener(), this);
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(EstateDetail response) {		
		setIndeterminateBarVisibility(false);
		if(response.respCode == RespCode.SUCCESS) {
			mEstateDetail = response;
			setText(titleTv, mEstateDetail.title);
			setText(priceTv, mEstateDetail.averagePrice);
			setText(phoneTv, mEstateDetail.phone);
			setText(addressTv, mEstateDetail.address);
			setText(saleAddressTv, mEstateDetail.saleAddress);
			setText(detailTv, mEstateDetail.detail);
			
			if(response.imagesUrl == null || response.imagesUrl.size() == 0) {
				imagesVg.setVisibility(View.GONE);
			}
			
			imgIv.setDefaultImageResId(R.drawable.estate_default_img);
			if(mEstateDetail.imagesUrl.size() > 0) {
				imgIv.setImageUrl(NetConfig.getPictureUrl(mEstateDetail.imagesUrl.get(0).images[0]), 
						CityLifeApp.getInstance().getImageLoader());
			}
			showContent();
			
			//设置当前的点赞状态
			if(mEstateDetail.favourState == 0) {
				mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(EstateDetailActivity.this, R.drawable.favour, mEstateDetail.favourNum));
			} else {
				mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(EstateDetailActivity.this, R.drawable.favoured, mEstateDetail.favourNum));
			}
			
			//设置当前的收藏状态
			mColletcMenuItem = getOverflowMenuItem(1);
			if(mEstateDetail.collectState == 0) {
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
	
	private void setText(TextView tv, String str) {
		if(!TextUtils.isEmpty(str)) {
			tv.setText(str);
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
		params.put("id", estateId);
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
			if(commentList.respCode == RespCode.SUCCESS) {
				if(commentList.totalNum > 0) {
					commentListVg.setVisibility(View.VISIBLE);
					mCommentList = commentList;
					mCommentAdapter = new CommentAdapter(EstateDetailActivity.this, mCommentList);
					commentLv.setAdapter(mCommentAdapter);
				} else {
					commentListVg.setVisibility(View.GONE);
				}
			} else {
				ToastHelper.showToastInBottom(EstateDetailActivity.this, commentList.respMsg);
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
		params.put("id", estateId);
		params.put("comment", comment);
		params.put("type", ChannelType.CHANNEL_TYPE_ESTATE+"");
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
				ToastHelper.showToastInBottom(EstateDetailActivity.this, R.string.comment_success);
				
				//最新评论
				commentPageInfo.pageNo = 1;
				requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, 
						getCommentListRequestParams(), new CommentListListener(), EstateDetailActivity.this);
			} else {
				ToastHelper.showToastInBottom(EstateDetailActivity.this, R.string.comment_fail);
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
		params.put("id", estateId);
		params.put("type", ChannelType.CHANNEL_TYPE_ESTATE+"");
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
				if(mEstateDetail.collectState == 0) {
					mEstateDetail.collectState = 1;
					mColletcMenuItem.setIcon(R.drawable.collect_icon);
					mColletcMenuItem.setTitle(R.string.collect_cancle);
					ToastHelper.showToastInBottom(EstateDetailActivity.this, R.string.collect_success);
				} else {
					mEstateDetail.collectState = 0;
					mColletcMenuItem.setIcon(R.drawable.un_collect_icon);
					mColletcMenuItem.setTitle(R.string.collect);
					ToastHelper.showToastInBottom(EstateDetailActivity.this, R.string.colletc_cancle_success);
				}
			} else {
				ToastHelper.showToastInBottom(EstateDetailActivity.this, collect.respMsg);
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
		params.put("id", estateId);
		params.put("type", ChannelType.CHANNEL_TYPE_ESTATE+"");
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
				if(mEstateDetail.favourState == 0) {
					mEstateDetail.favourState = 1;
					mEstateDetail.favourNum++;
					mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(EstateDetailActivity.this, R.drawable.favoured, mEstateDetail.favourNum));
					ToastHelper.showToastInBottom(EstateDetailActivity.this, R.string.favour_toast);
				} else {
					mEstateDetail.favourState = 0;
					mEstateDetail.favourNum--;
					mFavourMenuItem.setIcon(CommDrawable.getFavDrawable(EstateDetailActivity.this, R.drawable.favour, mEstateDetail.favourNum));
					ToastHelper.showToastInBottom(EstateDetailActivity.this, R.string.favour_cancle_toast);
				}
				
			} else {
				ToastHelper.showToastInBottom(EstateDetailActivity.this, baseBean.respMsg);
			}
		}
		
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == ResultCode.REFRESH_COMMENT_LIST) {
			//刷新评论列表
			commentPageInfo = new PageInfo(5, 1);
			requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
					new CommentListListener(), EstateDetailActivity.this);
		}
	}
}
