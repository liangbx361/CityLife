package com.wb.citylife.bean.db;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

import com.wb.citylife.config.DbConfig;

@Table(name = DbConfig.TN_CHANNEL)
public class DbChannel {

	@Id(column = "id")
	public int id;

	//栏目ID
	public String channelId;
	
	//栏目类型
	public int type;
	
	//栏目名称
	public String name;
	
	//栏目图标
	public String imageUrl;
	
	//新闻更新的数量
	public int updateNum;
	
	//是否处于已添加状态
	public boolean isAdd;
	
	//排序的比重
	public int weight;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getUpdateNum() {
		return updateNum;
	}

	public void setUpdateNum(int updateNum) {
		this.updateNum = updateNum;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
