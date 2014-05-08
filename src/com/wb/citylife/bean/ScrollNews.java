package com.wb.citylife.bean;

import java.util.List;

public class ScrollNews extends BaseBean{

    public int totalNum;
    public List<NewsItem> datas;
    		
    public class NewsItem { 
    	public String id;
    	public int type;
    	public String title;
    	public String imageUrl;
    }
	
}
