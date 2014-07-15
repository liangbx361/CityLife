package com.wb.citylife.mk.main;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.BaseExtraLayoutFragment;
import com.wb.citylife.activity.base.IBaseNetActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.SearchListAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.bean.Search;
import com.wb.citylife.bean.Search.SearchItem;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.mk.common.CommIntent;
import com.wb.citylife.task.SearchRequest;
import com.wb.citylife.widget.PullListViewHelper;

public class SearchFragment extends BaseExtraLayoutFragment implements Listener<Search>, ErrorListener,
	OnItemClickListener, ReloadListener, OnClickListener{
	
	private BaseActivity mActivity;
	
	private EditText searchEt;
	private Button searchBtn;
	private int searchType = 0;
	private String keyword;
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;
	private int loadState;
	
	private ListView mSearchResultLv;
	private SearchListAdapter mSearchAdapter;
	
	private SearchRequest mSearchRequest;
	private Search mSearch;
	private PageInfo searchPageInfo;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity =  (BaseActivity) activity;
		if(getArguments() != null) {
			searchType = getArguments().getInt(IntentExtraConfig.SEARCH_TYPE, 0);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return setConViewInLayout(inflater, R.layout.fg_search);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);		
		initView(view);
		
		searchPageInfo = new PageInfo();
	}
	
	private void initView(View view) {
		searchEt = (EditText) view.findViewById(R.id.search_et);
		searchBtn = (Button) view.findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				search();				
			}
		});
		
		mPullListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				//滑动到底部的处理
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE && mSearch.hasNextPage) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					searchPageInfo.pageNo++;
					requestSearch(Method.POST, NetInterface.METHOD_SEARCH, getSearchRequestParams(keyword), 
							SearchFragment.this, SearchFragment.this);
				}
			}
		});
		
		//不允许上拉下拉刷新
		mPullListView.setMode(Mode.DISABLED);
		mSearchResultLv = mPullListView.getRefreshableView();
		mSearchResultLv.setOnItemClickListener(this);
		
		//底部添加正在加载视图
		pullHelper = new PullListViewHelper(getActivity(), mSearchResultLv);
		pullHelper.setBottomClick(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					pullHelper.setBottomState(loadState, searchPageInfo.pageSize);	
					requestSearch(Method.POST, NetInterface.METHOD_SEARCH, getSearchRequestParams(keyword), 
							SearchFragment.this, SearchFragment.this);
				}
			}
		});
	}
	
	private void search() {
		keyword = searchEt.getText().toString();
		if(TextUtils.isEmpty(keyword)) {
			ToastHelper.showToastInBottom(getActivity(), "搜索关键字不能为空");
			return;
		}
		
		requestSearch(Method.POST, NetInterface.METHOD_SEARCH, getSearchRequestParams(keyword), this, this);
		showLoading();
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getSearchRequestParams(String keyword) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.PAGE_SIZE, searchPageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, searchPageInfo.pageNo+"");
		params.put(RespParams.USER_ID, CityLifeApp.getInstance().getUser().userId);
		params.put(RespParams.TYPE, searchType+"");
		params.put("keyword", keyword);
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
	private void requestSearch(int method, String methodUrl, Map<String, String> params,	 
			Listener<Search> listenre, ErrorListener errorListener) {			
		if(mSearchRequest != null) {
			mSearchRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mSearchRequest = new SearchRequest(method, url, params, listenre, errorListener);
		mActivity.startRequest(mSearchRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		ToastHelper.showToastInBottom(getActivity(), VolleyErrorHelper.getErrorMessage(error));
		
		if(searchPageInfo.pageNo == 1) {
			showLoadError(this);
		} else {
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_FAIL;
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOAD_FAIL, searchPageInfo.pageSize);
		}
	}
	
	@Override
	public void onReload() {
		searchPageInfo.pageNo = 1;
		requestSearch(Method.POST, NetInterface.METHOD_SEARCH, getSearchRequestParams(keyword), this, this);
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(Search response) {
		
		if(response.respCode == RespCode.SUCCESS) {	
			mSearch = response;
			if(mSearch.totalNum == 0) {
				setEmptyToastText(getResources().getString(R.string.search_no_result, keyword));
				showEmpty();
				return;
			}
			
			showContent();
			if(searchPageInfo.pageNo == 1) {
				mSearch = response;
				mSearchAdapter = new SearchListAdapter(getActivity(), mSearch);
				mSearchResultLv.setAdapter(mSearchAdapter);
			} else {
				mSearch.hasNextPage = response.hasNextPage;
				mSearch.datas.addAll(response.datas);
				mSearchAdapter.notifyDataSetChanged();
			}
			
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
			if(mSearch.hasNextPage) {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOADING, searchPageInfo.pageSize);
			} else {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE, searchPageInfo.pageSize);
			}			
		} else {
			showLoadError(this);
			ToastHelper.showToastInBottom(getActivity(), response.respMsg);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SearchItem sItem = mSearch.datas.get(position-1);
		CommIntent.startDetailPage(getActivity(), sItem.id, sItem.type);
	}	
}
