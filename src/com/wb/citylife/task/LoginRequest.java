package com.wb.citylife.task;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.ParamsRequest;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.parser.LoginParser;
import com.wb.citylife.bean.Login;

public class LoginRequest extends ParamsRequest<Login> {
	public LoginRequest (int method, String url, Map<String, String> params, 
			Listener<Login> listenre, ErrorListener errorListener) {
		super(method, url, params, listenre, errorListener);
	}
	
	@Override
	protected Response<Login> parseNetworkResponse(NetworkResponse response) {
		String resultStr = new String(response.data);
		DebugConfig.showLog("volleyresponse", resultStr);
							
		LoginParser parser = new LoginParser();
		return Response.success(parser.parse(resultStr), getCacheEntry());
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders(); 
		
		return headers;
	}
}
