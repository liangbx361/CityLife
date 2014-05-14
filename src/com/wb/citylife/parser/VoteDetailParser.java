package com.wb.citylife.parser;


import com.wb.citylife.bean.VoteDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VoteDetailParser {
	public VoteDetail parse(String resultStr) {		
		
		Gson gson = new Gson();
		VoteDetail data = gson.fromJson(resultStr, new TypeToken<VoteDetail>(){}.getType());
		
		return data;
	}
}