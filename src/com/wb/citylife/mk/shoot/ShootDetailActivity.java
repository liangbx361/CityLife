package com.wb.citylife.mk.shoot;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import com.wb.citylife.mk.comment.CommentListActivity;
import com.wb.citylife.task.CommentListRequest;
import com.wb.citylife.task.CommentRequest;
import com.wb.citylife.task.ShootDetailRequest;
import com.wb.citylife.widget.ListViewForScrollView;

public class ShootDetailActivity extends BaseActivity implements OnClickListener,
	Listener<ShootDetail>, ErrorListener, ReloadListener {
		
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
		return super.onOptionsItemSelected(item);
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
		showLoading();
		requestShootDetail(Method.POST, NetInterface.METHOD_SHOOT_DETAIL, 
				getShootDetailRequestParams(), this, this);	
		
		commentPageInfo = new PageInfo(5, 1);
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, 
				getCommentListRequestParams(), new CommentListListener(), this);
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
			mShootDetail = response;
			
			mImgAdapter = new ImageAdapter(this, mShootDetail.imagesUrl);
			imgPager.setAdapter(mImgAdapter);
			imgNumTv.setText("1/" + mShootDetail.imagesUrl.length);
			
			titleTv.setText(mShootDetail.title);
			usernameTv.setText(mShootDetail.userName);
			timeTv.setText(mShootDetail.time);
			commentTv.setText("评论 " + mShootDetail.commentNum + "");
			clickTv.setText("点击 " + mShootDetail.clickNum + "");
			detailTv.setText(mShootDetail.content);
			showContent();
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
			mCommentAdapter = new CommentAdapter(ShootDetailActivity.this, mCommentList);
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
			} else {
				ToastHelper.showToastInBottom(ShootDetailActivity.this, R.string.comment_fail);
			}
		}
		
	}	
}
