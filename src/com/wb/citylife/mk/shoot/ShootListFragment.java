package com.wb.citylife.mk.shoot;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
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
import com.wb.citylife.adapter.ShootListAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.BaseBean;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.bean.ShootList;
import com.wb.citylife.bean.ShootList.ShootItem;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.dialog.ConfirmDialog;
import com.wb.citylife.task.BaseRequest;
import com.wb.citylife.task.ShootListRequest;
import com.wb.citylife.widget.PullListViewHelper;

public class ShootListFragment extends BaseExtraLayoutFragment implements Listener<ShootList>, ErrorListener,
	OnItemClickListener, OnItemLongClickListener, ReloadListener{
	
	public static final String SHOOT_TYPE_NEW = "1";
	public static final String SHOOT_TYPE_HOT = "2";
	public static final String SHOOT_TYPE_MY = "3";
	
	private BaseNetActivity mActivity;
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;
	
	private ListView mShootLv;
	private ShootListAdapter mShootAdapter;
	
	private PageInfo shootPageInfo;
	private String mType;
	private int loadState;
		
	private ShootListRequest mShootListRequest;
	private ShootList mShootList;
	
	//删除已发布信息
	private BaseRequest mBaseRequest;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (BaseNetActivity) activity;
		mType = getArguments().getString(IntentExtraConfig.SHOOT_TYPE);
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
				requestShootList(Method.POST, NetInterface.METHOD_SHOOT_LIST, getShootListRequestParams(), 
						ShootListFragment.this, ShootListFragment.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				//处理上拉加载				
			}
		});
		
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE && mShootList.hasNextPage) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					shootPageInfo.pageNo++;	
					requestShootList(Method.POST, NetInterface.METHOD_SHOOT_LIST, getShootListRequestParams(), 
							ShootListFragment.this, ShootListFragment.this);
				}
			}
		});
		
		//设置允许下拉刷新
		mPullListView.setMode(Mode.PULL_FROM_START);		
		
		mShootLv = mPullListView.getRefreshableView();
		mShootLv.setOnItemClickListener(this);
		if(mType.equals(SHOOT_TYPE_MY)) {
			mShootLv.setOnItemLongClickListener(this);
		}
		
		//底部添加正在加载视图
		pullHelper = new PullListViewHelper(mActivity, mShootLv);
				
		pullHelper.setBottomClick(new OnClickListener() {
					
			@Override
			public void onClick(View v) {				
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					pullHelper.setBottomState(loadState, shootPageInfo.pageSize);		
					requestShootList(Method.POST, NetInterface.METHOD_SHOOT_LIST, getShootListRequestParams(), 
							ShootListFragment.this, ShootListFragment.this);
				}
			}
		});
		
		//启动第一次二手列表请求
		shootPageInfo = new PageInfo();
		requestShootList(Method.POST, NetInterface.METHOD_SHOOT_LIST, getShootListRequestParams(), 
				ShootListFragment.this, ShootListFragment.this);
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getShootListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.PAGE_SIZE, shootPageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, shootPageInfo.pageNo+"");	
//		if(mType.equals(SHOOT_TYPE_MY)) {
			params.put(RespParams.USER_ID, CityLifeApp.getInstance().getUser().userId);
//		}
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
	private void requestShootList(int method, String methodUrl, Map<String, String> params,	 
			Listener<ShootList> listenre, ErrorListener errorListener) {			
		if(mShootListRequest != null) {
			mShootListRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mShootListRequest = new ShootListRequest(method, url, params, listenre, errorListener);
		mActivity.startRequest(mShootListRequest);		
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
		requestShootList(Method.POST, NetInterface.METHOD_SHOOT_LIST, getShootListRequestParams(), 
				ShootListFragment.this, ShootListFragment.this);
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(ShootList response) {		
		mActivity.setIndeterminateBarVisibility(false);
		mPullListView.onRefreshComplete();
		
		if(response.respCode == RespCode.SUCCESS) {
			if(response.totalNum == 0) {
				if(mType.equals("3")) {
					setEmptyToastText(R.string.my_shoot_list_empty_toast);
				} else {
					setEmptyToastText(R.string.shoot_list_empty_toast);
				}
				showEmpty();
				return;
			}
			
			if(shootPageInfo.pageNo == 1) {
				mShootList = response;
				mShootAdapter = new ShootListAdapter(mActivity, mShootList);
				mShootLv.setAdapter(mShootAdapter);
				showContent();
			} else {
				mShootList.hasNextPage = response.hasNextPage;
				mShootList.datas.addAll(response.datas);
				mShootAdapter.notifyDataSetChanged();
			}
			
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
			if(mShootList.hasNextPage) {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOADING, shootPageInfo.pageSize);
			} else {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE, shootPageInfo.pageSize);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ShootItem sItem = mShootList.datas.get(position-1);
		Intent intent = new Intent(mActivity, ShootDetailActivity.class);
		intent.putExtra(IntentExtraConfig.DETAIL_ID, sItem.id);
		startActivity(intent);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		final ShootItem sItem = mShootList.datas.get(position-1);
		String title = mActivity.getResources().getString(R.string.toast);
		String toast = mActivity.getResources().getString(R.string.del_shoot_info_toast, sItem.title);
		new ConfirmDialog().getDialog(mActivity, title, toast, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				requestBase(Method.POST, NetInterface.METHOD_DEL_PUSH_INFO, getBaseRequestParams(sItem.id),
						new BaseListener(), ShootListFragment.this);
			}
			
		}).show();
		return true;
	}	
	
	//删除发布的二手信息
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getBaseRequestParams(String id) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.USER_ID, CityLifeApp.getInstance().getUser().userId);
		params.put(RespParams.ID, id);
		params.put(RespParams.TYPE, "2");
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
	private void requestBase(int method, String methodUrl, Map<String, String> params,	 
			Listener<BaseBean> listenre, ErrorListener errorListener) {			
		if(mBaseRequest != null) {
			mBaseRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mBaseRequest = new BaseRequest(method, url, params, listenre, errorListener);
		mActivity.startRequest(mBaseRequest);		
	}
	
	class BaseListener implements Listener<BaseBean> {

		@Override
		public void onResponse(BaseBean response) {			
			if(response.respCode == RespCode.SUCCESS) {
				//删除成功，则重新加载
				onReload();
			} else {
				ToastHelper.showToastInBottom(mActivity, response.respMsg);
			}
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == ResultCode.REFRESH_MY_SHOOT_LIST) {
			onReload();
		}
	}	
}
