package com.wb.citylife.bean;

import java.util.List;

public class Search extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<SearchItem> datas;
    		
    public class SearchItem { 
    	public String id;
    	public int type;
    	public String time;
    	public String title;
    	public String desc;
    }
	
}
