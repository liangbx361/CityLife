package com.wb.citylife.bean;

import java.util.List;

public class EstateList extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<EstateItem> datas;
    		
    public class EstateItem { 
    	public String id;
    	public String name;
    	public String address;
    	public String averagePrice;
    	public String thumbnailUrl;
    }
	
}
