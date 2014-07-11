package com.wb.citylife.mk.comment;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.CommentAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Comment;
import com.wb.citylife.bean.CommentList;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.task.CommentListRequest;
import com.wb.citylife.task.CommentRequest;
import com.wb.citylife.widget.PullListViewHelper;

public class CommentListActivity extends BaseActivity implements Listener<CommentList>, ErrorListener,
	ReloadListener{
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;
	private int loadState;
	
	private ListView commentLv;
	private CommentAdapter mCommentAdapter;
	
	public String commentId;
	public int commentType;
	
	//评论数据
	private CommentListRequest mCommentListRequest;
	private CommentList mCommentList;
	private PageInfo commentPageInfo;
	
	//发表评论
	private EditText commentEt;
	private Button sendBtn;
	private CommentRequest mCommentRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commentlist);
		
		getIntentData();
		initView();			
		
		showLoading();
	}
			
	@Override
	public void getIntentData() {
		commentId = getIntent().getStringExtra(IntentExtraConfig.COMMENT_ID);
		commentType = getIntent().getIntExtra(IntentExtraConfig.COMMENT_TYPE, 0);
	}
	
	@Override
	public void initView() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);		
		mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理下拉刷新
				commentPageInfo.pageNo = 1;
				requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
						CommentListActivity.this, CommentListActivity.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理上拉加载
			}
		});
		
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				//滑动到底部的处理
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE && mCommentList.hasNextPage) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					commentPageInfo.pageNo++;
					requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
							CommentListActivity.this, CommentListActivity.this);
				}
			}
		});
				
		//设置允许下拉刷新
		mPullListView.setMode(Mode.PULL_FROM_START);
						
		commentLv = mPullListView.getRefreshableView();
		
		//底部添加正在加载视图
		pullHelper = new PullListViewHelper(this, commentLv);
		pullHelper.setBottomClick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					pullHelper.setBottomState(loadState, commentPageInfo.pageSize);	
					requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
							CommentListActivity.this, CommentListActivity.this);
				}
			}
		});
		
		commentEt = (EditText) findViewById(R.id.comment_et);
		sendBtn = (Button) findViewById(R.id.comment_btn);
		sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				 
				//点击提交评论
				if(!CityLifeApp.getInstance().checkLogin()) {
					ToastHelper.showToastInBottom(CommentListActivity.this, R.string.comment_login_toast);
					return;
				}
				
				String comment = commentEt.getText().toString();
				if(comment != null && !comment.equals("")) {
					requestComment(Method.POST, NetInterface.METHOD_COMMENT, getCommentRequestParams(comment), 
							new CommentListener(), CommentListActivity.this);						
				} else {
					ToastHelper.showToastInBottom(CommentListActivity.this, R.string.comment_empty_toast);
				}	
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		commentPageInfo = new PageInfo();
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), this, this);
		setIndeterminateBarVisibility(true);		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		
		switch(item.getItemId()) {
		case android.R.id.home:
			Intent resultIntent = new Intent();
		    setResult(ResultCode.REFRESH_COMMENT_LIST, resultIntent);
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		
		switch(keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent resultIntent = new Intent();
		    setResult(ResultCode.REFRESH_COMMENT_LIST, resultIntent);
		    finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getCommentListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.PAGE_SIZE, commentPageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, commentPageInfo.pageNo+"");		
		params.put(RespParams.ID, commentId);
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
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
		
		if(commentPageInfo.pageNo == 1) {
			showLoadError(this);
		} else {
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_FAIL;
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOAD_FAIL, commentPageInfo.pageSize);
		}
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(CommentList response) {		
		mPullListView.onRefreshComplete();
		setIndeterminateBarVisibility(false);		
		
		if(response.respCode == RespCode.SUCCESS) {
			if(response.totalNum == 0) {
				showEmpty();
				return;
			}
			
			showContent();
			if(commentPageInfo.pageNo == 1) {
				mCommentList = response;
				mCommentAdapter = new CommentAdapter(CommentListActivity.this, mCommentList);
				commentLv.setAdapter(mCommentAdapter);
			} else {
				mCommentList.hasNextPage = response.hasNextPage;
				mCommentList.datas.addAll(response.datas);
				mCommentAdapter.notifyDataSetChanged();
			}
			
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
			if(mCommentList.hasNextPage) {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOADING, commentPageInfo.pageSize);
			} else {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE, commentPageInfo.pageSize);
			}
		}
	}

	@Override
	public void onReload() {
		commentPageInfo.pageNo = 1;
		requestCommentList(Method.POST, NetInterface.METHOD_COMMENT_LIST, getCommentListRequestParams(), 
				CommentListActivity.this, CommentListActivity.this);
		showLoading();
	}
	
	/**
	 * 获取评论参数请求参数
	 * @return
	 */
	private Map<String, String> getCommentRequestParams(String comment) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().userId);
		params.put("id", commentId);
		params.put("comment", comment);
		params.put("type", commentType+"");
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
				onReload();
				ToastHelper.showToastInBottom(CommentListActivity.this, R.string.comment_success);				
			} else {
				ToastHelper.showToastInBottom(CommentListActivity.this, R.string.comment_fail);
			}
		}
		
	}	
}
