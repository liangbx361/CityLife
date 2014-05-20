package com.wb.citylife.task;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.ParamsRequest;
import com.wb.citylife.bean.OldInfoList;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.parser.OldInfoListParser;

public class OldInfoListRequest extends ParamsRequest<OldInfoList> {
	
	public OldInfoListRequest(int method, String url, Map<String, String> params,
			Listener<OldInfoList> listener, ErrorListener errorListener) {
		super(method, url, params, listener, errorListener);
	}
	
	@Override
	protected Response<OldInfoList> parseNetworkResponse(NetworkResponse response) {
		String resultStr = new String(response.data);
		DebugConfig.showLog("volleyresponse", resultStr);
							
		OldInfoListParser parser = new OldInfoListParser();
		return Response.success(parser.parse(resultStr), getCacheEntry());
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders(); 
		
		return headers;
	}
}
