package com.wb.citylife.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.DbScrollNews;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.mk.common.CommIntent;
import com.wb.citylife.mk.news.NewsDetailActivity;

public class AdvPagerAdapter extends PagerAdapter implements OnClickListener{
	private int mCount;
	private List<DbScrollNews> scrollNewsList;
	private List<View> mViewList = new ArrayList<View>();
	private Activity mActivity;
	
	public AdvPagerAdapter(Activity activity, List<DbScrollNews> scrollNewsList) {		
		mActivity = activity;
		this.scrollNewsList = scrollNewsList;
		mCount = scrollNewsList.size();
		for(int i=0; i<mCount; i++) {
			DbScrollNews dbScrollNews = scrollNewsList.get(i);
			View view = mActivity.getLayoutInflater().inflate(R.layout.adv_item, null);
			ImageView videoIv = (ImageView) view.findViewById(R.id.vidoe_flag);
			if(dbScrollNews.isVideo && dbScrollNews.type == ChannelType.CHANNEL_TYPE_NEWS) {
				videoIv.setVisibility(View.VISIBLE);
			} else {
				videoIv.setVisibility(View.GONE);
			}
			view.setTag(i+"");
			view.setOnClickListener(this);
			mViewList.add(view);
		}		
	}
	
	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override  
    public void destroyItem(ViewGroup container, int position,  
            Object object) {  
        container.removeView(mViewList.get(position));  
    }  
	
	@Override  
    public int getItemPosition(Object object) {  

		return super.getItemPosition(object);  
    } 
	
	@Override  
    public Object instantiateItem(ViewGroup container, int position) { 
		View view = mViewList.get(position);
		container.addView(view);		
		DbScrollNews item = scrollNewsList.get(position);
		NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.adv_img);
		imageView.setDefaultImageResId(R.drawable.scroll_news_default_icon);
		if(item.imageUrl != null && !item.imageUrl.equals("")) {
			imageView.setImageUrl(item.imageUrl, 
					CityLifeApp.getInstance().getImageLoader());			
		}						
		
		return view;
	}

	@Override
	public void onClick(View v) {
		int index = Integer.parseInt(v.getTag().toString());
		DbScrollNews item = scrollNewsList.get(index);		
		//点击跳转新闻详情页
		CommIntent.startDetailPage(mActivity, item.newsId, item.type);
	}
		 
}
