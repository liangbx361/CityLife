package com.wb.citylife.mk.vote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.viewpagerindicator.CirclePageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.AdvPagerAdapter;
import com.wb.citylife.adapter.VoteListAdapter;
import com.wb.citylife.bean.Advertisement;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.bean.ScrollNews;
import com.wb.citylife.bean.VoteList;
import com.wb.citylife.bean.VoteList.VoteItem;
import com.wb.citylife.bean.db.DbScrollNews;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.task.ScrollNewsRequest;
import com.wb.citylife.task.VoteListRequest;
import com.wb.citylife.widget.PullListViewHelper;

public class VoteListActivity extends BaseActivity implements Listener<VoteList>, ErrorListener,
	OnItemClickListener, ReloadListener{
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;
	
	//投票列表
	private ListView mVoteListView;
	private VoteListAdapter mVoteAdapter;	
	private VoteListRequest mVoteListRequest;
	private VoteList mVoteList;		
	private PageInfo votePageInfo;
	private int loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
	
	//广告
	private ViewPager mAdvViewPager;
	private AdvPagerAdapter mAdvAdapter;
	private CirclePageIndicator mAdvIndicator;
	private AdvTimeCount advAdvTimeCount;
	private Advertisement mAdv;
		
	//滚动新闻
	private ScrollNewsRequest mScrollNewsRequest;
	private ScrollNews mScrollNews;
	private List<DbScrollNews> mScrollNewsList;
	
	private ScrollNewsListener listener = new ScrollNewsListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_votelist);
		
		getIntentData();
		initView();	
		
		showLoading();
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
				votePageInfo.pageNo = 1;
				requestVoteList(Method.POST, NetInterface.METHOD_VOTE_LIST, getVoteListRequestParams(), 
						VoteListActivity.this, VoteListActivity.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理上拉加载				
			}
		});
		
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE && mVoteList.hasNextPage) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					votePageInfo.pageNo++;
					requestVoteList(Method.POST, NetInterface.METHOD_VOTE_LIST, getVoteListRequestParams(), 
							VoteListActivity.this, VoteListActivity.this);					
				}
			}
		});
		
		//设置请允许下拉刷新
		mPullListView.setMode(Mode.PULL_FROM_START);
		
		mVoteListView = mPullListView.getRefreshableView();
		mVoteListView.setOnItemClickListener(this);
		
		//广告视图添加到List头部
		View advView = LayoutInflater.from(this).inflate(R.layout.adv_layout, null);
		mAdvViewPager = (ViewPager) advView.findViewById(R.id.adv_pager);
		mAdvIndicator = (CirclePageIndicator) advView.findViewById(R.id.adv_indicator);
		mVoteListView.addHeaderView(advView, null, false);		
		
		//底部添加正在加载视图
		pullHelper = new PullListViewHelper(this, mVoteListView);
		
		pullHelper.setBottomClick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					pullHelper.setBottomState(loadState, votePageInfo.pageSize);		
					requestVoteList(Method.POST, NetInterface.METHOD_VOTE_LIST, getVoteListRequestParams(), 
							VoteListActivity.this, VoteListActivity.this);
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		setIndeterminateBarVisibility(true);				
		votePageInfo = new PageInfo();
		requestVoteList(Method.POST, NetInterface.METHOD_VOTE_LIST, getVoteListRequestParams(), 
				VoteListActivity.this, VoteListActivity.this);
		requestScrollNews(Method.POST, NetInterface.METHOD_SCROLL_NEWS, 
				getScrollNewsRequestParams(), listener, listener);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getVoteListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		
		params.put(RespParams.PAGE_SIZE, votePageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, votePageInfo.pageNo+"");
		
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
	private void requestVoteList(int method, String methodUrl, Map<String, String> params,	 
			Listener<VoteList> listenre, ErrorListener errorListener) {			
		if(mVoteListRequest != null) {
			mVoteListRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mVoteListRequest = new VoteListRequest(method, url, params, listenre, errorListener);
		startRequest(mVoteListRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {				
		mPullListView.onRefreshComplete();	
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
		
		if(votePageInfo.pageNo == 1) {
			showLoadError(this);
		} else {
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_FAIL;
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOAD_FAIL, votePageInfo.pageSize);
		}
	}
	
	@Override
	public void onReload() {
		votePageInfo.pageNo = 1;
		requestVoteList(Method.POST, NetInterface.METHOD_VOTE_LIST, getVoteListRequestParams(), 
				VoteListActivity.this, VoteListActivity.this);
		requestScrollNews(Method.POST, NetInterface.METHOD_SCROLL_NEWS, 
				getScrollNewsRequestParams(), listener, listener);
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(VoteList response) {
		mPullListView.onRefreshComplete();
		setIndeterminateBarVisibility(false);
		if(votePageInfo.pageNo == 1) {
			mVoteList = response;
			mVoteAdapter = new VoteListAdapter(VoteListActivity.this, mVoteList);
			mVoteListView.setAdapter(mVoteAdapter);
			showContent();
		} else {
			mVoteList.hasNextPage = response.hasNextPage;
			mVoteList.datas.addAll(response.datas);
			mVoteAdapter.notifyDataSetChanged();
		}
		
		loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
		if(mVoteList.hasNextPage) {
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOADING, votePageInfo.pageSize);
		} else {
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE, votePageInfo.pageSize);
		}
	}
	
	/**
	 * 投票列表的点击处理
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position < mVoteListView.getHeaderViewsCount()) {
			//点击头部
		} else if(position < mVoteListView.getHeaderViewsCount() + mVoteList.datas.size()) {
			VoteItem voteItem = mVoteList.datas.get(position - mVoteListView.getHeaderViewsCount());
			Intent intent = new Intent(this, VoteDetailActivity.class);
			intent.putExtra(IntentExtraConfig.DETAIL_ID, voteItem.id);
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
	
	/**
	 * 获取滚动新闻请求参数
	 * @return
	 */
	private Map<String, String> getScrollNewsRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "2");		
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
			mScrollNews = scrollNews;
			List<DbScrollNews> scrollNewsList = new ArrayList<DbScrollNews>();
			for(int i=0; i<mScrollNews.datas.size(); i++) {
				ScrollNews.NewsItem newsItem = mScrollNews.datas.get(i);
				DbScrollNews dbScrollNews = new DbScrollNews();
				dbScrollNews.newsId = newsItem.id;
				dbScrollNews.imageUrl = newsItem.imageUrl;
				dbScrollNews.title = newsItem.title;
				dbScrollNews.type = newsItem.type;
				scrollNewsList.add(dbScrollNews);
			}
			mAdvAdapter = new AdvPagerAdapter(VoteListActivity.this, scrollNewsList);
			mAdvViewPager.setAdapter(mAdvAdapter);
			mAdvIndicator.setViewPager(mAdvViewPager);
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			setIndeterminateBarVisibility(false);
			ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
		}
	}	
}
