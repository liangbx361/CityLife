package com.wb.citylife.mk.common;

import android.content.Context;
import android.content.Intent;

import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.mk.estate.EstateDetailActivity;
import com.wb.citylife.mk.news.NewsDetailActivity;
import com.wb.citylife.mk.old.OldInfoDetailActivity;
import com.wb.citylife.mk.shoot.ShootDetailActivity;
import com.wb.citylife.mk.vote.VoteDetailActivity;

public class CommIntent {
	
	public static void startDetailPage(Context context, String id, int type) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(IntentExtraConfig.DETAIL_ID, id);
		switch(type) {
		case ChannelType.CHANNEL_TYPE_NEWS:
			intent.setClass(context, NewsDetailActivity.class);
			break;
		case ChannelType.CHANNEL_TYPE_VOTE:
			intent.setClass(context, VoteDetailActivity.class);
			break;
			
		case ChannelType.CHANNEL_TYPE_OLD_MARKET:
			intent.setClass(context, OldInfoDetailActivity.class);
			break;
			
		case ChannelType.CHANNEL_TYPE_SHOOT:
			intent.setClass(context, ShootDetailActivity.class);
			break;
			
		case ChannelType.CHANNEL_TYPE_HOUSE:
			intent.setClass(context, EstateDetailActivity.class);
			break;
		}
		context.startActivity(intent);
	}
}
