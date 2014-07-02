package com.wb.citylife.bean;

import java.util.ArrayList;
import java.util.List;

public class EstateDetail extends BaseBean{
	
	public String title;
	public int favourState;
    public int favourNum;
    public int collectState;
    public String averagePrice;
    public String phone;
    public String address;
    public String saleAddress;
    public String detail;
    public String longitude;
    public String latitude;
    public ArrayList<ImagesItem> imagesUrl;
    public List<VideoItem> videosUrl;
    		
    public class VideoItem { 
    	public String name;
    	public String videoUrl;
    }    			
}
