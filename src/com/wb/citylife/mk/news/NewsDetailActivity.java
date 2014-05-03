package com.wb.citylife.mk.news;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.tencent.mm.sdk.openapi.GetMessageFromWX.Resp;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.CommentAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Comment;
import com.wb.citylife.bean.CommentList;
import com.wb.citylife.bean.NewsDetail;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.task.CommentListRequest;
import com.wb.citylife.task.CommentRequest;
import com.wb.citylife.task.NewsDetailRequest;
import com.wb.citylife.widget.ListViewForScrollView;

public class NewsDetailActivity extends BaseActivity implements Listener<NewsDetail>, ErrorListener,
	OnClickListener{
				
	private String id;
	
	//新闻详情
	private TextView titleTv;
	private TextView timeTv;
	private NetworkImageView imgIv;
	private TextView contentTv;
	private NewsDetailRequest mNewsDetailRequest;
	private NewsDetail mNewsDetail;
	
	//最新评论
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsdetail);
		
		getIntentData();
		initView();				
	}
			
	@Override
	public void getIntentData() {
		id = getIntent().getStringExtra("id");
	}
	
	@Override
	public void initView() {
		titleTv = (TextView) findViewById(R.id.title);
		timeTv = (TextView) findViewById(R.id.time);
		imgIv = (NetworkImageView) findViewById(R.id.img);
		contentTv = (TextView) findViewById(R.id.content);
		commentLv = (ListViewForScrollView) findViewById(R.id.comment_list);
		commentEt = (EditText) findViewById(R.id.comment);
		sendBtn = (Button) findViewById(R.id.send);
		sendBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		setIndeterminateBarVisibility(true);
		
		requestNewsDetail(Method.GET, NetInterface.METHOD_NEWS_DETAIL, getNewsDetailRequestParams(), this, this);
		commentPageInfo = new PageInfo();
		requestCommentList(Method.GET, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), new CommentListListener(), this);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {			
		return super.onOptionsItemSelected(item);
	}
		
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getNewsDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(IntentExtraConfig.ND_ID, id);		
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
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
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
		imgIv.setImageUrl(mNewsDetail.imagesUrl[0], CityLifeApp.getInstance().getImageLoader());
		contentTv.setText(mNewsDetail.content);
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
			mCommentAdapter = new CommentAdapter(NewsDetailActivity.this, mCommentList);
			commentLv.setAdapter(mCommentAdapter);
		}		
	}
	
	/**
	 * 点击提交评论
	 */
	@Override
	public void onClick(View v) {
		String comment = commentEt.getText().toString();
		if(comment != null && !comment.equals("")) {
			requestComment(Method.GET, NetInterface.METHOD_COMMENT, getCommentRequestParams(comment), new CommentListener(), this);						
		} else {
			ToastHelper.showToastInBottom(this, R.string.comment_empty_toast);
		}		
	}
	
	/**
	 * 获取评论参数请求参数
	 * @return
	 */
	private Map<String, String> getCommentRequestParams(String comment) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", "");
		params.put("id", id);
		params.put("comment", comment);
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
			} else {
				ToastHelper.showToastInBottom(NewsDetailActivity.this, R.string.comment_fail);
			}
		}
		
	}	
}
