package com.wb.citylife.mk.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.viewpagerindicator.LinePageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.NewsAdapter;
import com.wb.citylife.adapter.ScrollNewsPagerAdapter;
import com.wb.citylife.bean.NewsList;
import com.wb.citylife.bean.NewsList.NewsItem;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.bean.ScrollNews;
import com.wb.citylife.bean.db.DbScrollNews;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.mk.main.HomeFragment;
import com.wb.citylife.mk.main.SearchActivity;
import com.wb.citylife.task.NewsListRequest;
import com.wb.citylife.task.ScrollNewsRequest;

public class NewsListActivity extends BaseActivity implements Listener<NewsList>, ErrorListener,
	OnItemClickListener, ReloadListener{
	
	//正在加载
	public static final int BOTTOM_STATE_LOADING = 0;
	//加载失败
	public static final int BOTTOM_STATE_LOAD_FAIL = 1;
	//无更多数据
	public static final int BOTTOM_STATE_NO_MORE_DATE = 2;	
	//加载空闲
	public static final int BOTTOM_STATE_LOAD_IDLE = 3;
	
	private PullToRefreshListView mPullListView;
	private ListView mNewsListView;	
	private NewsAdapter mNewsAdapter;
	private View bottomView;
	
	//新闻列表
	private NewsListRequest mNewsListRequest;	
	private NewsList mNewsList;
	private PageInfo newsPageInfo;
	private int loadState = BOTTOM_STATE_LOAD_IDLE;
	
	//滚动新闻
	private ViewPager mAdvViewPager;
	private ScrollNewsPagerAdapter mAdvAdapter;
	private LinePageIndicator mAdvIndicator;
	private AdvTimeCount advAdvTimeCount;
	private TextView advTitleTv;
	
	//滚动新闻
	private ScrollNewsRequest mScrollNewsRequest;
	private ScrollNews mScrollNews;
	private List<DbScrollNews> mScrollNewsList;
	private ScrollNewsListener scrollNewsListener = new ScrollNewsListener();
	
	private String channelId;
	private String channelName;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newslist);
		
		getIntentData();
		initView();		
		
		showLoading();
//		advTest();
	}
	
	@Override
	public void getIntentData() {
		channelId = getIntent().getStringExtra(IntentExtraConfig.CHANNEL_ID);
		channelName = getIntent().getStringExtra(IntentExtraConfig.CHANNEL_NAME);
	}
	
	@Override
	public void initView() {				
		mPullListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);		
		mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理下拉刷新
				newsPageInfo.pageNo = 1;
				requestNewsList(Method.POST, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), 
						NewsListActivity.this, NewsListActivity.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理上拉加载				
			}
		});
		
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if(loadState == BOTTOM_STATE_LOAD_IDLE && mNewsList.hasNextPage) {
					loadState = BOTTOM_STATE_LOADING;
					newsPageInfo.pageNo++;
					requestNewsList(Method.POST, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), 
							NewsListActivity.this, NewsListActivity.this);					
				}
			}
		});
		
		//设置请允许下拉刷新
		mPullListView.setMode(Mode.PULL_FROM_START);
		
		mNewsListView = mPullListView.getRefreshableView();
		mNewsListView.setOnItemClickListener(this);
		
		//广告视图添加到List头部
		View advView = LayoutInflater.from(this).inflate(R.layout.scroll_news_layout, null);
		mAdvViewPager = (ViewPager) advView.findViewById(R.id.adv_pager);
		mAdvIndicator = (LinePageIndicator) advView.findViewById(R.id.adv_indicator);
		advTitleTv = (TextView) advView.findViewById(R.id.title);
		mNewsListView.addHeaderView(advView, null, false);	
		advAdvTimeCount = new AdvTimeCount(HomeFragment.ADV_AUTO_MOVE_TIME, HomeFragment.ADV_AUTO_MOVE_TIME);
		mAdvIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				advTitleTv.setText(mScrollNewsList.get(position).title);
				advAdvTimeCount.cancel();
				advAdvTimeCount.start();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				
			}
		});
		
		//底部添加正在加载视图
		bottomView = LayoutInflater.from(this).inflate(R.layout.bottom_loading_layout, null);
		BottomHolder holder = new BottomHolder();
		holder.progressBar = (ProgressBar) bottomView.findViewById(R.id.loading_processbar);
		holder.stateTv = (TextView) bottomView.findViewById(R.id.state);
		bottomView.setTag(holder);
		mNewsListView.addFooterView(bottomView);
		
		bottomView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				if(loadState == BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = BOTTOM_STATE_LOADING;
					setBottomState(loadState);
					requestNewsList(Method.POST, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), 
							NewsListActivity.this, NewsListActivity.this);					
				}
			}
		});				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		if(!TextUtils.isEmpty(channelName)) {
			setTitle(channelName);
		}
		
		setActionBarItem(menu, R.id.action_search, R.string.action_search, 
				R.drawable.actionbar_search_icon);
		
		newsPageInfo = new PageInfo();
		requestNewsList(Method.POST, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), this, this);
		requestScrollNews(Method.POST, NetInterface.METHOD_SCROLL_NEWS, getScrollNewsRequestParams(), scrollNewsListener, scrollNewsListener);
	
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch(item.getItemId()) {
		case R.id.action_search:
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra(IntentExtraConfig.SEARCH_TYPE, ChannelType.CHANNEL_TYPE_NEWS);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
		
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getNewsListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		
		params.put("channelId", channelId);
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
		mPullListView.onRefreshComplete();
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
		
		if(newsPageInfo.pageNo == 1) {
			showLoadError(this);
		} else {
			loadState = BOTTOM_STATE_LOAD_FAIL;
			setBottomState(BOTTOM_STATE_LOAD_FAIL);
		}
		
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(NewsList response) {
		mPullListView.onRefreshComplete();			
		
		if(newsPageInfo.pageNo == 1) {
			mNewsList = response;
			mNewsAdapter = new NewsAdapter(NewsListActivity.this, mNewsList);
			mNewsListView.setAdapter(mNewsAdapter);
			showContent();
		} else {
			mNewsList.hasNextPage = response.hasNextPage;
			mNewsList.datas.addAll(response.datas);
			mNewsAdapter.notifyDataSetChanged(mNewsList);		
		}
		
		loadState = BOTTOM_STATE_LOAD_IDLE;
		if(mNewsList.hasNextPage) {			
			setBottomState(BOTTOM_STATE_LOADING);
		} else {
			setBottomState(BOTTOM_STATE_NO_MORE_DATE);
		}
	}
	
	/**
	 * 列表点击后的处理
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if(position <mNewsListView.getHeaderViewsCount()) {
			//点击头部
		} else if(position < mNewsListView.getHeaderViewsCount() + mNewsList.datas.size()) { 
			position -= mNewsListView.getHeaderViewsCount();
			NewsItem newsItem = mNewsList.datas.get(position);
			Intent intent = new Intent(this, NewsDetailActivity.class);
			intent.putExtra(IntentExtraConfig.DETAIL_ID, newsItem.id);
			startActivity(intent);
		} else {
			//底部点击
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
	
	public class BottomHolder {
		private ProgressBar progressBar;
		private TextView stateTv;
	}
	
	private void setBottomState(int state) {
		BottomHolder holder = (BottomHolder) bottomView.getTag();
		switch (state) {		
		case BOTTOM_STATE_LOADING:			
			holder.progressBar.setVisibility(View.VISIBLE);
			holder.stateTv.setText(newsPageInfo.pageSize + "条载入中...");
			break;
			
		case BOTTOM_STATE_LOAD_FAIL:
			holder.progressBar.setVisibility(View.GONE);
			holder.stateTv.setText("加载失败，点击重试");
			break;

		case BOTTOM_STATE_NO_MORE_DATE:
			mNewsListView.removeFooterView(bottomView);
			holder.progressBar.setVisibility(View.GONE);
			holder.stateTv.setText("无更多新闻");
			break;
		}
	}
	
	/**
	 * 获取滚动新闻请求参数
	 * @return
	 */
	private Map<String, String> getScrollNewsRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("channelId", channelId);
		params.put("type", "1");		
		return params;
	}
	
	/**
	 * 执行滚动新闻任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestScrollNews(int method, String methodUrl, Map<String, String> params,	 
			Listener<ScrollNews> listenre, ErrorListener errorListener) {			
		if(mScrollNewsRequest != null) {
			mScrollNewsRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mScrollNewsRequest = new ScrollNewsRequest(method, url, params, listenre, errorListener);
		startRequest(mScrollNewsRequest);		
	}
	
	/**
	 * 处理滚动新闻
	 * @author liangbx
	 *
	 */
	class ScrollNewsListener implements Listener<ScrollNews>, ErrorListener {

		@Override
		public void onResponse(ScrollNews scrollNews) {
			setIndeterminateBarVisibility(false);
			if(scrollNews.respCode == RespCode.SUCCESS) {
				mScrollNews = scrollNews;
				mScrollNewsList = new ArrayList<DbScrollNews>();
				for(int i=0; i<mScrollNews.datas.size(); i++) {
					ScrollNews.NewsItem newsItem = mScrollNews.datas.get(i);
					DbScrollNews dbScrollNews = new DbScrollNews();
					dbScrollNews.newsId = newsItem.id;
					dbScrollNews.imageUrl = newsItem.imageUrl;
					dbScrollNews.title = newsItem.title;
					dbScrollNews.type = newsItem.type;
					dbScrollNews.isVideo = newsItem.isVideo;
					mScrollNewsList.add(dbScrollNews);
				}
				mAdvAdapter = new ScrollNewsPagerAdapter(NewsListActivity.this, mScrollNewsList, ChannelType.CHANNEL_TYPE_NEWS);
				mAdvViewPager.setAdapter(mAdvAdapter);
				mAdvIndicator.setViewPager(mAdvViewPager);
				if(mScrollNewsList.size() > 0) {
					advTitleTv.setText(mScrollNewsList.get(0).title);
					advAdvTimeCount.cancel();
					advAdvTimeCount.start();
				}
			}
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			setIndeterminateBarVisibility(false);
			showLoadError(NewsListActivity.this);
			ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));			
		}
	}
	
	@Override
	public void onReload() {
		newsPageInfo.pageNo = 1;
		requestNewsList(Method.POST, NetInterface.METHOD_NEWS_LIST, getNewsListRequestParams(), this, this);		
		requestScrollNews(Method.POST, NetInterface.METHOD_SCROLL_NEWS, getScrollNewsRequestParams(), scrollNewsListener, scrollNewsListener);
		showLoading();
	}	
	
	/************************************************ 测试数据 **********************************************/
//	private void advTest() {
//		mAdv = new Advertisement();
//		mAdv.respCode = 0;
//		mAdv.respMsg = "ok";
//		mAdv.totalCount = 2;
//		mAdv.resources = new ArrayList<Advertisement.AdvItem>();
//		
//		Advertisement.AdvItem item  = mAdv.new AdvItem();
//		item.id = "1";
//		item.imageUrl = "http://img3.cache.netease.com/photo/0007/2014-04-29/9R0BQDPF1OQR0007.jpg";
//		item.title = "舞动青春";
//		item.linkUrl = "";
//		mAdv.resources.add(item);
//		
//		item  = mAdv.new AdvItem();
//		item.id = "2";
//		item.imageUrl = "http://pic16.nipic.com/20110910/4582261_110721084388_2.jpg";
//		item.title = "创意无限";
//		item.linkUrl = "";
//		mAdv.resources.add(item);
//		
//		mAdvAdapter = new AdvPagerAdapter(this, mAdv);
//		mAdvViewPager.setAdapter(mAdvAdapter);
//		mAdvIndicator.setViewPager(mAdvViewPager);
//	}
	
}
