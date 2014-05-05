package com.wb.citylife.parser;


import com.wb.citylife.bean.Channel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ChannelParser {
	public Channel parse(String resultStr) {		
		
		Gson gson = new Gson();
		Channel data = gson.fromJson(resultStr, new TypeToken<Channel>(){}.getType());
		
		return data;
	}
}