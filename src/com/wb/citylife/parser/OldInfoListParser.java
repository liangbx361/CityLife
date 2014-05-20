package com.wb.citylife.parser;


import com.wb.citylife.bean.OldInfoList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OldInfoListParser {
	public OldInfoList parse(String resultStr) {		
		
		Gson gson = new Gson();
		OldInfoList data = gson.fromJson(resultStr, new TypeToken<OldInfoList>(){}.getType());
		
		return data;
	}
}