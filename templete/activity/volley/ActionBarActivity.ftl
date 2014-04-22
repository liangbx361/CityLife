package ${PackageName};

import java.util.HashMap;
<#if isList == "false">
<#else>
import java.util.List;
</#if>
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.activity.base.BaseActivity;
import com.ffcs.findme.config.NetConfig;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;

import com.ffcs.findme.app.FindMeApp;
import ${PackageName}.bean.${DataName};
import ${PackageName}.task.${TaskName};

<#if isList == "false">
public class ${ClassName} extends BaseActivity implements Listener<${DataName}>, ErrorListener{
<#else>
public class ${ClassName} extends BaseActivity implements Listener<List<${DataName}>>, ErrorListener{
</#if>	
	
	private RequestQueue mQueue;	
	private ${TaskName} m${TaskName};
	<#if isList == "false">
	private ${DataName} m${DataName};
	<#else>
	private List<${DataName}> m${DataName}List;
	</#if>
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.${LayoutName});
		
		getIntentData();
		initView();
		
		mQueue = FindMeApp.getInstance().getRequestQueue();	
		//executeRequest("请在这里填写请求的方法", get${TaskName}Params(), this, this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
	}
	
	@Override
	protected void onStop() {		
		if(m${TaskName} != null) {
			m${TaskName}.cancel();
		}
		super.onStop();
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 此处设置ActionBar的菜单按钮		
		setDisplayHomeAsUpEnabled(true);
				
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 此处响应ActionBar的菜单点击事件				
		return super.onOptionsItemSelected(item);
	}
		
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> get${TaskName}Params() {
		Map<String, String> params = new HashMap<String, String>();
		
		//请在这里填写请求参数
		
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
	<#if isList == "false">
	private void executeRequest(int method, String methodUrl, Map<String, String> params,	 
			Listener<${DataName}> listenre, ErrorListener errorListener) {			
	<#else>
			private void executeRequest(int method, String methodUrl, Map<String, String> params,		
			Listener<List<${DataName}>> listenre, ErrorListener errorListener) {
	</#if>
		if(m${TaskName} != null) {
			m${TaskName}.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		m${TaskName} = new ${TaskName}(method, url, params, listenre, errorListener);
		mQueue.add(m${TaskName});
		mQueue.start();
		setIndeterminateBarVisibility(true);
	}
	
	/**
	 * 请求错误处理，提示错误信息或者显示错误页面
	 
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
	<#if isList == "false">
	public void onResponse(${DataName} response) {
		m${DataName} = response;
	<#else>
	public void onResponse(List<${DataName}> response) {
		m${DataName}List = response;
	</#if>	
	setIndeterminateBarVisibility(false);
	}
}
