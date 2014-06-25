package com.wb.citylife.mk.estate;

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
import com.wb.citylife.adapter.EstateListAdapter;
import com.wb.citylife.bean.EstateList;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.task.EstateListRequest;
import com.wb.citylife.widget.PullListViewHelper;

public class EstateListFragment extends BaseExtraLayoutFragment implements Listener<EstateList>, ErrorListener,
	OnItemClickListener, ReloadListener{
	
	public static final String ESTATE_TYPE_NEW = "1";
	public static final String ESTATE_TYPE_HOT = "2";
	
	private BaseNetActivity mActivity;
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;
	
	private ListView mShootLv;
	private EstateListAdapter mEstateAdapter;
	
	private PageInfo shootPageInfo;
	private String mType;
	private int loadState;
		
	private EstateListRequest mEstateListRequest;
	private EstateList mEstateList;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (BaseNetActivity) activity;
		mType = getArguments().getString(IntentExtraConfig.ESTATE_TYPE);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return setContentView(inflater, R.layout.common_pull_list_layout);	
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
				shootPageInfo.pageNo = 1;
				requestEstateList(Method.POST, NetInterface.METHOD_ESTATE_LIST, getEstateListRequestParams(), 
						EstateListFragment.this, EstateListFragment.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理上拉加载				
			}
		});
		
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE && mEstateList.hasNextPage) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					shootPageInfo.pageNo++;	
					requestEstateList(Method.POST, NetInterface.METHOD_ESTATE_LIST, getEstateListRequestParams(), 
							EstateListFragment.this, EstateListFragment.this);
				}
			}
		});
		
		//设置允许下拉刷新
		mPullListView.setMode(Mode.PULL_FROM_START);		
		
		mShootLv = mPullListView.getRefreshableView();
		mShootLv.setOnItemClickListener(this);
		
		//底部添加正在加载视图
		pullHelper = new PullListViewHelper(mActivity, mShootLv);
				
		pullHelper.setBottomClick(new OnClickListener() {
					
			@Override
			public void onClick(View v) {				
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					pullHelper.setBottomState(loadState, shootPageInfo.pageSize);		
					requestEstateList(Method.POST, NetInterface.METHOD_ESTATE_LIST, getEstateListRequestParams(), 
							EstateListFragment.this, EstateListFragment.this);
				}
			}
		});
		
		//启动第一次二手列表请求
		shootPageInfo = new PageInfo();
		requestEstateList(Method.POST, NetInterface.METHOD_ESTATE_LIST, getEstateListRequestParams(), 
				EstateListFragment.this, EstateListFragment.this);
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getEstateListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.PAGE_SIZE, shootPageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, shootPageInfo.pageNo+"");	
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
	private void requestEstateList(int method, String methodUrl, Map<String, String> params,	 
			Listener<EstateList> listenre, ErrorListener errorListener) {			
		if(mEstateListRequest != null) {
			mEstateListRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mEstateListRequest = new EstateListRequest(method, url, params, listenre, errorListener);
		mActivity.startRequest(mEstateListRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		ToastHelper.showToastInBottom(getActivity(), VolleyErrorHelper.getErrorMessage(error));
		
		if(shootPageInfo.pageNo == 1) {
			showLoadError(this);
		} else {
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_FAIL;
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOAD_FAIL, shootPageInfo.pageSize);
		}
	}
	
	@Override
	public void onReload() {
		shootPageInfo.pageNo = 1;
		requestEstateList(Method.POST, NetInterface.METHOD_ESTATE_LIST, getEstateListRequestParams(), 
				EstateListFragment.this, EstateListFragment.this);
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(EstateList response) {		
		mPullListView.onRefreshComplete();
		
		if(response.respCode == RespCode.SUCCESS) {
			if(shootPageInfo.pageNo == 1) {
				mEstateList = response;
				mEstateAdapter = new EstateListAdapter(mActivity, mEstateList);
				mShootLv.setAdapter(mEstateAdapter);
				showContent();
			} else {
				mEstateList.hasNextPage = response.hasNextPage;
				mEstateList.datas.addAll(response.datas);
				mEstateAdapter.notifyDataSetChanged();
			}
			
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
			if(mEstateList.hasNextPage) {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOADING, shootPageInfo.pageSize);
			} else {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE, shootPageInfo.pageSize);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String estateId = mEstateList.datas.get(position).id;
		Intent intent = new Intent(mActivity, EstateDetailActivity.class);
		intent.putExtra(IntentExtraConfig.DETAIL_ID, estateId);
		startActivity(intent);
	}	
}
