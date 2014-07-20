package com.wb.citylife.mk.mycenter;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.BaseBean;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.db.DbHelper;
import com.wb.citylife.task.BaseRequest;

public class ModifyGenderActivity extends BaseActivity implements OnClickListener,
	Listener<BaseBean>, ErrorListener{
	
	private Button submitBtn;
	private RadioGroup genderRg;
	private int gender;
	
	private BaseRequest mBaseRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_gender);
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		genderRg = (RadioGroup) findViewById(R.id.gender);
		submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(this);
		
		if(CityLifeApp.getInstance().getUser().gender == 1) {
			genderRg.check(R.id.male);
		} else {
			genderRg.check(R.id.female);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
				
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onClick(View v) {			
		gender = 1;
		if(genderRg.getCheckedRadioButtonId() == R.id.female) {
			gender = 2;
		}
		
		if(gender == CityLifeApp.getInstance().getUser().gender) {
			ToastHelper.showToastInBottom(this, "不能修改为相同的性别");
			return;
		}
		
		setIndeterminateBarVisibility(true);
		requestBase(Method.POST, NetInterface.METHOD_MODIFY_GENDER, getBaseRequestParams(gender), this, this);
	}	
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getBaseRequestParams(int gender) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().getUserId());
		params.put("gender", gender + "");		
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
		startRequest(mBaseRequest);		
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
	public void onResponse(BaseBean response) {
		setIndeterminateBarVisibility(false);
		if(response.respCode == RespCode.SUCCESS) {
			CityLifeApp.getInstance().getUser().gender = gender;
			DbHelper.saveUser(CityLifeApp.getInstance().getUser());
			ToastHelper.showToastInBottom(this, R.string.gender_modify_success);
			finish();
		}
	}
}
