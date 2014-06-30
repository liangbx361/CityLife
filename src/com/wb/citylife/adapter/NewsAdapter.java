package com.wb.citylife.adapter;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.NewsList;
import com.wb.citylife.bean.NewsList.NewsItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	
	private Context mContext;
	private NewsList mNewsList;
	
	public NewsAdapter(Context context, NewsList newsList) {
		mContext = context;
		mNewsList = newsList;
	}

	@Override
	public int getCount() {
		return mNewsList.datas.size();
	}

	@Override
	public Object getItem(int position) {
		return mNewsList.datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder;
		if(convertView == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.news_item_layout, null);
			holder = new ViewHolder();
			holder.thumbIv = (NetworkImageView) view.findViewById(R.id.img);
			holder.videoIv = (ImageView) view.findViewById(R.id.video);
			holder.titleTv = (TextView) view.findViewById(R.id.title);
			holder.descTv = (TextView) view.findViewById(R.id.desc);
			holder.tagTv = (TextView) view.findViewById(R.id.tag);
			holder.tag2Tv = (TextView) view.findViewById(R.id.tag2);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		NewsItem newsItem = mNewsList.datas.get(position);
		holder.thumbIv.setDefaultImageResId(R.drawable.base_list_default_icon);
		holder.thumbIv.setImageUrl(newsItem.thumbnailurl, CityLifeApp.getInstance().getImageLoader());		
		holder.titleTv.setText(newsItem.title);
		holder.descTv.setText(newsItem.summary);
		holder.tagTv.setText("点击" + newsItem.clickNum);
		holder.tag2Tv.setText("评论" + newsItem.commentNum);
		
		if(newsItem.type == 0) {
			holder.videoIv.setVisibility(View.GONE);
		} else {
			holder.videoIv.setVisibility(View.VISIBLE);
		}
		
		return view;
	}
	
	public void notifyDataSetChanged(NewsList newsList) {
		mNewsList = newsList;
		super.notifyDataSetChanged();
	}
	
	public class ViewHolder {
		NetworkImageView thumbIv;
		ImageView videoIv;
		TextView titleTv;
		TextView descTv;
		TextView tagTv;
		TextView tag2Tv;
	}	
}
