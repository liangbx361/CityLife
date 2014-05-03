package com.wb.citylife.mk.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.viewpagerindicator.CirclePageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.AdvPagerAdapter;
import com.wb.citylife.adapter.NewsAdapter;
import com.wb.citylife.bean.Advertisement;
import com.wb.citylife.bean.NewsList;
import com.wb.citylife.bean.NewsList.NewsItem;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.task.NewsListRequest;
import com.wb.citylife.widget.ListViewForScrollView;
import com.wb.citylife.widget.ListViewForScrollView.OnLastItemVisibleListener;

public class CopyOfNewsListActivity extends BaseActivity implements Listener<NewsList>, ErrorListener, OnItemClickListener{
	
	private PullToRefreshScrollView mPullScrollView;
	private ListViewForScrollView mNewsListView;	
	private NewsAdapter mNewsAdapter;
	private View bottomView;
	
	private NewsListRequest mNewsListRequest;	
	private NewsList mNewsList;
	private PageInfo newsPageInfo;
	
	//广告
	private ViewPager mAdvViewPager;
	private AdvPagerAdapter mAdvAdapter;
	private CirclePageIndicator mAdvIndicator;
	private AdvTimeCount advAdvTimeCount;
	private Advertisement mAdv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newslist);
		
		getIntentData();
		initView();		
		advTest();
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		mAdvViewPager = (ViewPager) findViewById(R.id.adv_pager);
		mAdvIndicator = (CirclePageIndicator) findViewById(R.id.adv_indicator);
		
		mPullScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);		
		mPullScrollView.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				//处理下拉刷新
				newsPageInfo.pageNo = 1;
				requestNewsList(Method.GET, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), 
						CopyOfNewsListActivity.this, CopyOfNewsListActivity.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				//处理上拉加载
				if(mNewsList.hasNextPage) {
					newsPageInfo.pageNo++;
					requestNewsList(Method.GET, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), 
							CopyOfNewsListActivity.this, CopyOfNewsListActivity.this);
				}
			}
		});
		
//		mPullScrollView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
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
		
		mPullScrollView.setMode(Mode.PULL_FROM_START);
		
		mNewsListView = (ListViewForScrollView) findViewById(R.id.news_list);
		mNewsListView.setOnItemClickListener(this);	
		mNewsListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			
			@Override
			public void onLastItemVisible() {
				Log.d("scroll_bottom", "滑动到底部");
			}
		});
				
		bottomView = LayoutInflater.from(this).inflate(R.layout.bottom_loading_layout, null);
		mNewsListView.addFooterView(bottomView);
		
		mPullScrollView.setOnPullEventListener(new OnPullEventListener<ScrollView>() {

			@Override
			public void onPullEvent(PullToRefreshBase<ScrollView> refreshView,
					State state, Mode direction) {
				Log.d("scroll_bottom", "滑动到底部" + direction);
			}
		});
		
		ScrollView scrollView = mPullScrollView.getRefreshableView();
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
		mPullScrollView.onRefreshComplete();
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(NewsList response) {
		setIndeterminateBarVisibility(false);
		mPullScrollView.onRefreshComplete();					
		if(newsPageInfo.pageNo == 1) {
			mNewsList = response;
			mNewsAdapter = new NewsAdapter(CopyOfNewsListActivity.this, mNewsList);
			mNewsListView.setAdapter(mNewsAdapter);
		} else {
			mNewsList.datas.addAll(response.datas);
			mNewsAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 广告播放时间计时器
	 * @author liangbx
	 *
	 */
	class AdvTimeCount extends CountDownTimer {

		public AdvTimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			int currentItem = mAdvViewPager.getCurrentItem();
			if(currentItem < mAdvViewPager.getChildCount() - 1) {
				currentItem++;
			} else {
				currentItem = 0;
			}
			mAdvIndicator.setCurrentItem(currentItem);
			advAdvTimeCount.cancel();
			advAdvTimeCount.start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
		}		
	}
	
	/**
	 * 列表点击后的处理
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if(position < mNewsListView.getLastVisiblePosition()) {		
			NewsItem newsItem = mNewsList.datas.get(position);
			Intent intent = new Intent(this, NewsDetailActivity.class);
			intent.putExtra(IntentExtraConfig.ND_ID, newsItem.id);
			startActivity(intent);
		} 
	}
	
	/************************************************ 测试数据 **********************************************/
	private void advTest() {
		mAdv = new Advertisement();
		mAdv.respCode = 0;
		mAdv.respMsg = "ok";
		mAdv.totalCount = 2;
		mAdv.resources = new ArrayList<Advertisement.AdvItem>();
		
		Advertisement.AdvItem item  = mAdv.new AdvItem();
		item.id = "1";
		item.imageUrl = "http://img3.cache.netease.com/photo/0007/2014-04-29/9R0BQDPF1OQR0007.jpg";
		item.title = "舞动青春";
		item.linkUrl = "";
		mAdv.resources.add(item);
		
		item  = mAdv.new AdvItem();
		item.id = "1";
		item.imageUrl = "http://pic16.nipic.com/20110910/4582261_110721084388_2.jpg";
		item.title = "创意无限";
		item.linkUrl = "";
		mAdv.resources.add(item);
		
		mAdvAdapter = new AdvPagerAdapter(this, mAdv);
		mAdvViewPager.setAdapter(mAdvAdapter);
		mAdvIndicator.setViewPager(mAdvViewPager);
	}
	
}
