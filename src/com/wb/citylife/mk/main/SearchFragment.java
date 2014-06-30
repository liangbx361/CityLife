package com.wb.citylife.mk.main;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.IBaseNetActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.bean.Search;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespParams;
import com.wb.citylife.task.SearchRequest;

public class SearchFragment extends Fragment implements Listener<Search>, ErrorListener{
	
	private IBaseNetActivity mActivity;
	
	private EditText searchEt;
	private Button searchBtn;
	
	private SearchRequest mSearchRequest;
	private Search mSearch;
	private PageInfo searchPageInfo;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity =  (IBaseNetActivity) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_search, container, false);		
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
	}
	
	private void search() {
		String searchStr = searchEt.getText().toString();
		if(TextUtils.isEmpty(searchStr)) {
			ToastHelper.showToastInBottom(getActivity(), "搜索关键字不能为空");
			return;
		}
		
		requestSearch(Method.POST, NetInterface.METHOD_SEARCH, getSearchRequestParams(searchStr), this, this);
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
		params.put(RespParams.TYPE, "0");
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
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(Search response) {
		mSearch = response;
	}
}
