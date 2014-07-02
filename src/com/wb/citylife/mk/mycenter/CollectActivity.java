package com.wb.citylife.mk.mycenter;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.adapter.CollectListAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.MyCollect;
import com.wb.citylife.bean.MyCollect.CollectItem;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.mk.common.CommIntent;
import com.wb.citylife.task.MyCollectRequest;
import com.wb.citylife.widget.PullListViewHelper;

/**
 * 收藏模块
 * @author liangbx
 *
 */
public class CollectActivity extends BaseActivity implements Listener<MyCollect>, 
	ErrorListener, ReloadListener, OnItemClickListener{
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;
	private int loadState;
	
	private ListView mCollectLv;
	private CollectListAdapter mCollectAdapter;
	
	private MyCollectRequest mMyCollectRequest;
	private MyCollect mMyCollect;
	private PageInfo collectPageInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_collect_list);
		
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
				collectPageInfo.pageNo = 1;
				requestMyCollect(Method.POST, NetInterface.METHOD_MY_COLLECT, getMyCollectRequestParams(), CollectActivity.this, CollectActivity.this);
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
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE && mMyCollect.hasNextPage) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					collectPageInfo.pageNo++;
					requestMyCollect(Method.POST, NetInterface.METHOD_MY_COLLECT, getMyCollectRequestParams(), 
							CollectActivity.this, CollectActivity.this);
				}
			}
		});
		
		//设置允许下拉刷新
		mPullListView.setMode(Mode.PULL_FROM_START);
						
		mCollectLv = mPullListView.getRefreshableView();
		mCollectLv.setOnItemClickListener(this);
		
		//底部添加正在加载视图
		pullHelper = new PullListViewHelper(this, mCollectLv);
		pullHelper.setBottomClick(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_FAIL) {
					//加载失败，点击重试
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					pullHelper.setBottomState(loadState, collectPageInfo.pageSize);	
					requestMyCollect(Method.POST, NetInterface.METHOD_MY_COLLECT, getMyCollectRequestParams(), 
							CollectActivity.this, CollectActivity.this);
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		collectPageInfo = new PageInfo();
		requestMyCollect(Method.POST, NetInterface.METHOD_MY_COLLECT, getMyCollectRequestParams(), CollectActivity.this, CollectActivity.this);
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
	private Map<String, String> getMyCollectRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(RespParams.PAGE_SIZE, collectPageInfo.pageSize+"");
		params.put(RespParams.PAGE_NO, collectPageInfo.pageNo+"");		
		params.put(RespParams.USER_ID, CityLifeApp.getInstance().getUser().userId);
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
	private void requestMyCollect(int method, String methodUrl, Map<String, String> params,	 
			Listener<MyCollect> listenre, ErrorListener errorListener) {			
		if(mMyCollectRequest != null) {
			mMyCollectRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mMyCollectRequest = new MyCollectRequest(method, url, params, listenre, errorListener);
		startRequest(mMyCollectRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
		
		if(collectPageInfo.pageNo == 1) {
			showLoadError(this);
		} else {
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_FAIL;
			pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOAD_FAIL, collectPageInfo.pageSize);
		}
	}
	
	@Override
	public void onReload() {
		collectPageInfo.pageNo = 1;
		requestMyCollect(Method.POST, NetInterface.METHOD_MY_COLLECT, getMyCollectRequestParams(), CollectActivity.this, CollectActivity.this);
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(MyCollect response) {
		
		if(response.respCode == RespCode.SUCCESS) {
			if(response.totalNum == 0) {
				setEmptyToastText(R.string.collect_empty_toast);
				showEmpty();
				return;
			}
			
			showContent();
			if(collectPageInfo.pageNo == 1) {
				mMyCollect = response;
				mCollectAdapter = new CollectListAdapter(this, mMyCollect);
				mCollectLv.setAdapter(mCollectAdapter);
			} else {
				mMyCollect.hasNextPage = response.hasNextPage;
				mMyCollect.datas.addAll(response.datas);
				mCollectAdapter.notifyDataSetChanged();
			}
			
			loadState = PullListViewHelper.BOTTOM_STATE_LOAD_IDLE;
			if(mMyCollect.hasNextPage) {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_LOADING, collectPageInfo.pageSize);
			} else {
				pullHelper.setBottomState(PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE, collectPageInfo.pageSize);
			}
		} else {
			ToastHelper.showToastInBottom(this, response.respMsg);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CollectItem cItem = mMyCollect.datas.get(position-1);
		CommIntent.startDetailPage(this, cItem.id, cItem.type);
	}
	
}
