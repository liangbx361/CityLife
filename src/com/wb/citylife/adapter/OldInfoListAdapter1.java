package com.wb.citylife.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.ParamsRequest.TagErrorListener;
import com.common.net.volley.ParamsRequest.TagListener;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.OldInfoList;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.mk.old.OldInfoListActivity;
import com.wb.citylife.task.OldInfoListRequest;

public class OldInfoListAdapter1 extends PagerAdapter implements OnItemClickListener,
	Listener<OldInfoList>, ErrorListener{
	
	private String TAG_OLD_INFO = "1";
	private String TAG_MY_OLD_INFO = "2";
	
	private List<PullToRefreshListView> mViewList = new ArrayList<PullToRefreshListView>();
	private OldInfoListActivity mActivity;
	private int pageNum;
	
	private OldInfoListRequest mOldInfoListRequest;
	private OldInfoList mOldInfoList;
	
	public OldInfoListAdapter1(Context context) {
		mActivity = (OldInfoListActivity)context;		
		pageNum = 1;
		if(CityLifeApp.getInstance().checkLogin()) {
			pageNum++;
		}
		
		for(int i=0; i<pageNum; i++) {
			PullToRefreshListView view = (PullToRefreshListView) LayoutInflater.from(context).inflate(R.layout.old_info_list_layout, null);
			view.setOnItemClickListener(this);
			mViewList.add(view);
		}
	}
	
	@Override
	public int getCount() {
		return pageNum;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override  
    public void destroyItem(ViewGroup container, int position,  
            Object object) {  
        container.removeView(mViewList.get(position));  
    }  
	
	@Override  
    public Object instantiateItem(ViewGroup container, int position) { 
		PullToRefreshListView view = mViewList.get(position);
		container.addView(view);
		
		
		
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getOldInfoListRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
				
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
			Listener<OldInfoList> listenre, ErrorListener errorListener, String tag) {				
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mOldInfoListRequest = new OldInfoListRequest(method, url, params, listenre, errorListener);		
		mActivity.startRequest(mOldInfoListRequest);		
		mOldInfoListRequest.setTag(tag);
	}
	
	@Override
	public void onErrorResponse(VolleyError error) {
		mActivity.setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(mActivity, VolleyErrorHelper.getErrorMessage(error));
	}

	@Override
	public void onResponse(OldInfoList response) {
		mOldInfoList = response;
		mActivity.setIndeterminateBarVisibility(false);
	}
}
