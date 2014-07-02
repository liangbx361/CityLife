package com.wb.citylife.bean.db;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

import com.wb.citylife.config.DbConfig;

@Table(name=DbConfig.TN_USER)
public class User {
	
	@Id(column="id")
	public int id;
	
	public String userId = "";
	
	public String avatarUrl;
	
	public String nickname;
	
	public String accessToken;
	
	public String userphone;
	
	/**
	 * 登录状态 0：为登出 1：为登录
	 */
	public int isLogin;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(int isLogin) {
		this.isLogin = isLogin;
	}

	public String getUserphone() {
		return userphone;
	}

	public void setUserphone(String userphone) {
		this.userphone = userphone;
	}
	
	
	
	
}
