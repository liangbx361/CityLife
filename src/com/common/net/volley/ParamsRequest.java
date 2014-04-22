package com.common.net.volley;

import java.util.HashMap;
import java.util.Map;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public abstract class ParamsRequest<T> extends Request<T> {
	private Map<String, String> params;
	private Listener<T> mListener;
	
	public ParamsRequest(int method, String url, Map<String, String> params, Listener<T> listenre, ErrorListener errorListener) {
		super(method, url + formatUrlParams(method, params), errorListener);
		mListener = listenre;
		this.params = params;	
	}
	
	@Override
	abstract protected Response<T> parseNetworkResponse(NetworkResponse response);
	
	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return params;
	}
	
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Charset", "UTF-8"); 
		return headers;
	}
	
	/**
	 * @param params
	 * @return
	 */
	private static String formatUrlParams(int method, Map<String, String> params) {		
		String query="";
		if(method == Method.GET || method == Method.DELETE || method == Method.PUT) {
			String[] keys = params.keySet().toArray(new String[] {});
			for(int i = 0, size  = keys.length; i < size; i++) {
				String key = keys[i];
				String value = params.get(key).replaceAll("\n", "");
				query += (i == 0 ? "?" : "&");
				query += key;
				query += "=";
				query += Uri.encode(value);
			}
		}
		return query;
	}
}
