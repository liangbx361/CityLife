package com.wb.citylife.mk.merchant;

import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.wb.citylife.R;
import com.wb.citylife.R.id;
import com.wb.citylife.R.layout;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.MerchantListAdapter;
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
import com.wb.citylife.bean.MerchantList;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.task.MerchantListRequest;
import com.wb.citylife.widget.PullListViewHelper;

public class MerchantListActivity extends BaseActivity implements Listener<MerchantList>, 
	ErrorListener, ReloadListener{
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;	
	private ListView mMerchantLv;
	private MerchantListAdapter mMerchantAdapter;
		
	private MerchantListRequest mMerchantListRequest;
	private MerchantList mMerchantList;
	private PageInfo merchantPInfo;
	private int loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_list);
		
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
				merchantPInfo.pageNo = 1;
				requestMerchantList(Method.POST, NetInterface.METHOD_MERCHANT_LIST, getMerchantListRequestParams(), 
						MerchantListActivity.this, MerchantListActivity.this);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				
			}
		});
		
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE && mMerchantList.hasNextPage) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					merchantPInfo.pageNo++;
					requestMerchantList(Method.POST, NetInterface.METHOD_MERCHANT_LIST, getMerchantListRequestParams(), 
							MerchantListActivity.this, MerchantListActivity.this);
				}
			}
		});
		
		//设置请允许下拉刷新
		mPullListView.setMode(Mode.PULL_FROM_START);
		mMerchantLv = mPullListView.getRefreshableView();
		mMerchantLv.setDividerHeight(0);
		mMerchantLv.setSelector(new ColorDrawable(getResources().getColor(R.color.transparent)));
		
		//底部添加正在加载视图
		pullHelper = new PullListViewHelper(this, mMerchantLv);
		pullHelper.setBottomClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					pullHelper.setBottomState(loadState, merchantPInfo.pageSize);
					requestMerchantList(Method.POST, NetInterface.METHOD_MERCHANT_LIST, getMerchantListRequestParams(), 
							MerchantListActivity.this, MerchantListActivity.this);
				}
			}			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		merchantPInfo = new PageInfo();
		requestMerchantList(Method.POST, NetInterface.METHOD_MERCHANT_LIST, 
				getMerchantListRequestParams(), this, this);		
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
	private Map<String, String> getMerchantListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.PAGE_SIZE, merchantPInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, merchantPInfo.pageNo+"");		
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
	private void requestMerchantList(int method, String methodUrl, Map<String, String> params,	 
			Listener<MerchantList> listenre, ErrorListener errorListener) {			
		if(mMerchantListRequest != null) {
			mMerchantListRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mMerchantListRequest = new MerchantListRequest(method, url, params, listenre, errorListener);
		startRequest(mMerchantListRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
		if(merchantPInfo.pageNo == 1) {
			showLoadError(this);
		} else {
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_FAIL;
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOAD_IDLE, merchantPInfo.pageSize);
		}
	}
	
	@Override
	public void onReload() {
		merchantPInfo.pageNo = 1;
		requestMerchantList(Method.POST, NetInterface.METHOD_MERCHANT_LIST, 
				getMerchantListRequestParams(), this, this);	
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(MerchantList response) {
		mPullListView.onRefreshComplete();
		mMerchantList = response;
		setIndeterminateBarVisibility(false);
		if(merchantPInfo.pageNo == 1) {
			mMerchantList = response;
			mMerchantAdapter = new MerchantListAdapter(this, mMerchantList);
			mMerchantLv.setAdapter(mMerchantAdapter);
			showContent();
		} else {
			mMerchantList.hasNextPage = response.hasNextPage;
			mMerchantList.datas.addAll(response.datas);
			mMerchantAdapter.notifyDataSetChanged();			
		}
		
		loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
		if(mMerchantList.hasNextPage) {
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOADING, merchantPInfo.pageSize);
		} else {
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE, merchantPInfo.pageSize);
		}
	}	
}
