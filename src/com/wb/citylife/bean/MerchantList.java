package com.wb.citylife.bean;

import java.util.List;

public class MerchantList extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<MerchantItem> datas;
    		
    public class MerchantItem { 
    	public String id;
    	public String name;
    	public String logoUrl;
    }
	
}
