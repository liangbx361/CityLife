package com.wb.citylife.mk.estate;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
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
import com.wb.citylife.adapter.CommentAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Comment;
import com.wb.citylife.bean.CommentList;
import com.wb.citylife.bean.EstateDetail;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.task.CommentListRequest;
import com.wb.citylife.task.CommentRequest;
import com.wb.citylife.task.EstateDetailRequest;
import com.wb.citylife.widget.ListViewForScrollView;

public class EstateDetailActivity extends BaseActivity implements Listener<EstateDetail>, ErrorListener,
	OnClickListener{
	
	private TextView titleTv;
	private TextView priceTv;
	private TextView phoneTv;
	private TextView addressTv;
	private TextView saleAddressTv;
	private TextView detailTv;
	private NetworkImageView imgIv;
	private ViewGroup imagesVp;
	
	private String estateId;
	
	//详情
	private EstateDetailRequest mEstateDetailRequest;
	private EstateDetail mEstateDetail;	
	
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
		setContentView(R.layout.activity_estatedetail);
		
		getIntentData();
		initView();				
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
		imagesVp = (ViewGroup) findViewById(R.id.images);		
		imagesVp.setOnClickListener(this);
		
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
		
		requestEstateDetail(Method.GET, NetInterface.METHOD_ESTATE_DETAIL, 
				getEstateDetailRequestParams(), this, this);
		commentPageInfo = new PageInfo();
		requestCommentList(Method.GET, NetInterface.METHOD_COMMENT_LIST, 
				getCommentListRequestParams(), new CommentListListener(), this);
		setIndeterminateBarVisibility(true);		
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
		switch(v.getId()) {
		case R.id.images:{
			Intent intent = new Intent(this, ImageBrowseActivity.class);
			intent.putParcelableArrayListExtra(IntentExtraConfig.ESTATE_IMAGE_DATA, mEstateDetail.imagesUrl);
			startActivity(intent);
		}break;
		
		//点击提交评论
		case R.id.comment_btn:{
			String comment = commentEt.getText().toString();
			if(comment != null && !comment.equals("")) {
				requestComment(Method.GET, NetInterface.METHOD_COMMENT, getCommentRequestParams(comment), new CommentListener(), this);						
			} else {
				ToastHelper.showToastInBottom(this, R.string.comment_empty_toast);
			}	
		}break;		
		
		case R.id.click:
			
			break;
		}
	}
		
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getEstateDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
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
			
			imgIv.setDefaultImageResId(R.drawable.base_list_adv_default_icon);
			imgIv.setImageUrl(NetConfig.getPictureUrl(mEstateDetail.imagesUrl.get(0).images[0]), 
					CityLifeApp.getInstance().getImageLoader());
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
			mCommentAdapter = new CommentAdapter(EstateDetailActivity.this, mCommentList);
			commentLv.setAdapter(mCommentAdapter);
		}		
	}
		
	/**
	 * 获取评论参数请求参数
	 * @return
	 */
	private Map<String, String> getCommentRequestParams(String comment) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", "");
		params.put("id", estateId);
		params.put("comment", comment);
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
			} else {
				ToastHelper.showToastInBottom(EstateDetailActivity.this, R.string.comment_fail);
			}
		}
		
	}	
}
