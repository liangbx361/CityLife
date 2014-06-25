package com.wb.citylife.mk.old;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseExtraLayoutFragment;
import com.wb.citylife.activity.base.BaseNetActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.OldInfoListAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.OldInfoList;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.bean.OldInfoList.OldInfoItem;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.task.OldInfoListRequest;
import com.wb.citylife.widget.PullListViewHelper;

public class OldListFragment extends BaseExtraLayoutFragment implements Listener<OldInfoList>, ErrorListener,
	OnItemClickListener, ReloadListener{
	
	public static final String TAG_OLD_INFO = "1";
	public static final String TAG_MY_OLD_INFO = "2";
	
	private BaseNetActivity mActivity;
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;
	
	private ListView mOldListView;
	private OldInfoListAdapter mOldAdapter; 
	
	private PageInfo oldPageInfo;
	private String mType;
	private int loadState;
	
	private OldInfoListRequest mOldInfoListRequest;
	private OldInfoList mOldInfoList;
		
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (BaseNetActivity) activity;
		mType = getArguments().getString(IntentExtraConfig.OLD_TYPE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		return setContentView(inflater, R.layout.old_info_list_layout);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);	
		
		initView(view);
		showLoading();
	}
	
	private void initView(View view) {
		mPullListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);	
		mPullListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理下拉刷新
				oldPageInfo.pageNo = 1;
				requestOldInfoList(Method.POST, NetInterface.METHOD_OLD_INFO_LIST, getOldInfoListRequestParams(), 
						OldListFragment.this, OldListFragment.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理上拉加载				
			}
		});
		
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE && mOldInfoList.hasNextPage) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					oldPageInfo.pageNo++;
					requestOldInfoList(Method.POST, NetInterface.METHOD_OLD_INFO_LIST, getOldInfoListRequestParams(), 
							OldListFragment.this, OldListFragment.this);					
				}
			}
		});
		
		//设置允许下拉刷新
		mPullListView.setMode(Mode.PULL_FROM_START);		
		
		mOldListView = mPullListView.getRefreshableView();
		mOldListView.setOnItemClickListener(this);
		
		//底部添加正在加载视图
		pullHelper = new PullListViewHelper(mActivity, mOldListView);
				
		pullHelper.setBottomClick(new OnClickListener() {
					
			@Override
			public void onClick(View v) {				
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					pullHelper.setBottomState(loadState, oldPageInfo.pageSize);		
					requestOldInfoList(Method.POST, NetInterface.METHOD_OLD_INFO_LIST, getOldInfoListRequestParams(), 
							OldListFragment.this, OldListFragment.this);
				}
			}
		});
		
		//启动第一次二手列表请求
		oldPageInfo = new PageInfo();
		requestOldInfoList(Method.POST, NetInterface.METHOD_OLD_INFO_LIST, getOldInfoListRequestParams(), 
				OldListFragment.this, OldListFragment.this);
	}
		
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getOldInfoListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.PAGE_SIZE, oldPageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, oldPageInfo.pageNo+"");	
		if(mType.equals(TAG_MY_OLD_INFO)) {
			params.put(RespParams.USER_ID, CityLifeApp.getInstance().getUser().userId);
		}
		params.put("type", mType);
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
	private void requestOldInfoList(int method, String methodUrl, Map<String, String> params,	 
			Listener<OldInfoList> listenre, ErrorListener errorListener) {				
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mOldInfoListRequest = new OldInfoListRequest(method, url, params, listenre, errorListener);		
		mActivity.startRequest(mOldInfoListRequest);		
	}
	
	@Override
	public void onErrorResponse(VolleyError error) {
		ToastHelper.showToastInBottom(mActivity, VolleyErrorHelper.getErrorMessage(error));
		
		if(oldPageInfo.pageNo == 1) {
			showLoadError(this);
		} else {
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_FAIL;
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOAD_FAIL, oldPageInfo.pageSize);
		}
	}
	
	@Override
	public void onReload() {
		oldPageInfo.pageNo = 1;
		requestOldInfoList(Method.POST, NetInterface.METHOD_OLD_INFO_LIST, getOldInfoListRequestParams(), 
				OldListFragment.this, OldListFragment.this);
		showLoading();
	}

	@Override
	public void onResponse(OldInfoList response) {
		mActivity.setIndeterminateBarVisibility(false);					
		mPullListView.onRefreshComplete();
		
		if(response.respCode == RespCode.SUCCESS) {
			if(oldPageInfo.pageNo == 1) {
				mOldInfoList = response;
				mOldAdapter = new OldInfoListAdapter(mActivity, mOldInfoList);
				mOldListView.setAdapter(mOldAdapter);
				showContent();
			} else {
				mOldInfoList.hasNextPage = response.hasNextPage;
				mOldInfoList.datas.addAll(response.datas);
				mOldAdapter.notifyDataSetChanged();
			}
			
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
			if(mOldInfoList.hasNextPage) {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOADING, oldPageInfo.pageSize);
			} else {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE, oldPageInfo.pageSize);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		OldInfoItem item = mOldInfoList.datas.get(position);
		Intent intent = new Intent(mActivity, OldInfoDetailActivity.class);
		intent.putExtra(IntentExtraConfig.DETAIL_ID, item.id);
		startActivity(intent);
	}	
}
