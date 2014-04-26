package com.wb.citylife.bean;

import java.util.List;

public class Advertisement extends BaseBean{

    public boolean hasNextPage;
    public int totalCount;
    public List<AdvItem> resources;
    		
    public class AdvItem { 
    	public int id;
    	public String title;
    	public int weight;
    	public String linkUrl;
    	public String imageUrl;
    }
	
}
