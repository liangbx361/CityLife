package com.wb.citylife.bean;

import java.io.Serializable;
import java.util.List;

public class Channel extends BaseBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4284877931311257910L;
	
	public int totalNum;
    public List<ChannelItem> datas;
    
    
    public class ChannelItem { 
    	public String id;
    	public int type;
    	public String name;
    	public String imageUrl;
    	public int updateNum;
    	public boolean isAdd;
    }
	
}
