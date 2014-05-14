package com.wb.citylife.parser;


import com.wb.citylife.bean.VoteList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VoteListParser {
	public VoteList parse(String resultStr) {		
		
		Gson gson = new Gson();
		VoteList data = gson.fromJson(resultStr, new TypeToken<VoteList>(){}.getType());
		
		return data;
	}
}