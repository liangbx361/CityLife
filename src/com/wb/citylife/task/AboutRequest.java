package com.wb.citylife.task;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.ParamsRequest;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.parser.AboutParser;
import com.wb.citylife.bean.About;

public class AboutRequest extends ParamsRequest<About> {
	public AboutRequest (int method, String url, Map<String, String> params, 
			Listener<About> listenre, ErrorListener errorListener) {
		super(method, url, params, listenre, errorListener);
	}
	
	@Override
	protected Response<About> parseNetworkResponse(NetworkResponse response) {
		String resultStr = new String(response.data);
		DebugConfig.showLog("volleyresponse", resultStr);
							
		AboutParser parser = new AboutParser();
		return Response.success(parser.parse(resultStr), getCacheEntry());
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders(); 
		
		return headers;
	}
}
