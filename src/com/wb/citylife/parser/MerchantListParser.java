package com.wb.citylife.parser;


import com.wb.citylife.bean.MerchantList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MerchantListParser {
	public MerchantList parse(String resultStr) {		
		
		Gson gson = new Gson();
		MerchantList data = gson.fromJson(resultStr, new TypeToken<MerchantList>(){}.getType());
		
		return data;
	}
}