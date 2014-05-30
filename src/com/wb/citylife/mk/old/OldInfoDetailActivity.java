package com.wb.citylife.mk.old;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.viewpagerindicator.LinePageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.bean.OldInfoDetail;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.task.OldInfoDetailRequest;

public class OldInfoDetailActivity extends BaseActivity implements OnClickListener,
	Listener<OldInfoDetail>, ErrorListener{
	
	private EditText commentEt;
	private Button commentBtn;
	
	private TextView titleTv;
	private ViewPager viewPager;
	private LinePageIndicator pageIndicator;
	
	private TextView usernameTv;
	private TextView timeTv;
	private TextView priceTv;
	private TextView commentTv;
	private TextView clickTv;
	private TextView detailTv;
	private TextView contactTv;
		
	private OldInfoDetailRequest mOldInfoDetailRequest;
	private OldInfoDetail mOldInfoDetail;
	
	private String id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oldinfodetail);
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		id = getIntent().getStringExtra(IntentExtraConfig.DETAIL_ID);
	}

	@Override
	public void initView() {
		commentEt = (EditText) findViewById(R.id.comment_et);
		commentBtn = (Button) findViewById(R.id.comment_btn);
		titleTv = (TextView) findViewById(R.id.title);
		viewPager = (ViewPager) findViewById(R.id.pager);
		pageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
		usernameTv = (TextView) findViewById(R.id.username);
		timeTv = (TextView) findViewById(R.id.time);
		priceTv = (TextView) findViewById(R.id.price);
		commentTv = (TextView) findViewById(R.id.comment);
		clickTv = (TextView) findViewById(R.id.click);
		detailTv = (TextView) findViewById(R.id.detail);
		contactTv = (TextView) findViewById(R.id.contactInfo);
	}

	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		requestOldInfoDetail(Method.POST, NetInterface.METHOD_OLD_INFO_DETAIL, 
				getOldInfoDetailRequestParams(), this, this);
		setIndeterminateBarVisibility(true);		
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
	private Map<String, String> getOldInfoDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);		
		params.put("phoneId", "");
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
	private void requestOldInfoDetail(int method, String methodUrl, Map<String, String> params,	 
			Listener<OldInfoDetail> listenre, ErrorListener errorListener) {			
		if(mOldInfoDetailRequest != null) {
			mOldInfoDetailRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mOldInfoDetailRequest = new OldInfoDetailRequest(method, url, params, listenre, errorListener);
		startRequest(mOldInfoDetailRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(OldInfoDetail response) {		
		setIndeterminateBarVisibility(false);
		if(response.respCode == RespCode.SUCCESS) {
			mOldInfoDetail = response;
		}
	}

}
