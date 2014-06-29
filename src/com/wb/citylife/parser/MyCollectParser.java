package com.wb.citylife.parser;


import com.wb.citylife.bean.MyCollect;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MyCollectParser {
	public MyCollect parse(String resultStr) {		
		
		Gson gson = new Gson();
		MyCollect data = gson.fromJson(resultStr, new TypeToken<MyCollect>(){}.getType());
		
		return data;
	}
}