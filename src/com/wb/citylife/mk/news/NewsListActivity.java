package com.wb.citylife.mk.news;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.NewsAdapter;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespParams;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.wb.citylife.bean.NewsList;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.task.NewsListRequest;

public class NewsListActivity extends BaseActivity implements Listener<NewsList>, ErrorListener{
	
	private PullToRefreshListView mPullListView;
	private ListView mNewsListView;	
	private NewsAdapter mNewsAdapter;
	
	private NewsListRequest mNewsListRequest;	
	private NewsList mNewsList;
	private PageInfo newsPageInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newslist);
		
		getIntentData();
		initView();				
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);		
		mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理下拉刷新
				newsPageInfo.pageNo = 1;
				requestNewsList(Method.GET, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), 
						NewsListActivity.this, NewsListActivity.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理上拉加载
				if(mNewsList.hasNextPage) {
					newsPageInfo.pageNo++;
					requestNewsList(Method.GET, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), 
							NewsListActivity.this, NewsListActivity.this);
				}
			}
		});
		
//		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
//
//			@Override
//			public void onLastItemVisible() {
//				//滑动到底部的处理
//			}
//		});
//		
//		//设置刷新时的滑动�?��		
//		mPullListView.setScrollingWhileRefreshingEnabled(true);
//		
//		//设置自动刷新
//		mPullListView.setRefreshing(false);
		
		mPullListView.setMode(Mode.BOTH);
		
		mNewsListView = mPullListView.getRefreshableView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		newsPageInfo = new PageInfo();
		requestNewsList(Method.GET, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), this, this);
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
		
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getNewsListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		
		params.put(RespParams.PAGE_SIZE, newsPageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, newsPageInfo.pageNo+"");
		
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
	private void requestNewsList(int method, String methodUrl, Map<String, String> params,	 
			Listener<NewsList> listenre, ErrorListener errorListener) {			
		if(mNewsListRequest != null) {
			mNewsListRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mNewsListRequest = new NewsListRequest(method, url, params, listenre, errorListener);
		startRequest(mNewsListRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		mPullListView.onRefreshComplete();
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(NewsList response) {
		setIndeterminateBarVisibility(false);
		mPullListView.onRefreshComplete();					
		if(newsPageInfo.pageNo == 1) {
			mNewsList = response;
			mNewsAdapter = new NewsAdapter(NewsListActivity.this, mNewsList);
			mNewsListView.setAdapter(mNewsAdapter);
		} else {
			mNewsList.datas.addAll(response.datas);
			mNewsAdapter.notifyDataSetChanged();
		}
	}
}
