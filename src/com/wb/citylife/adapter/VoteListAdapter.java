package com.wb.citylife.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.VoteList;
import com.wb.citylife.bean.VoteList.VoteItem;

public class VoteListAdapter extends BaseAdapter {
	
	private Context mContext;
	private VoteList mVoteList;
	
	public VoteListAdapter(Context context, VoteList voteList) {
		mContext = context;
		mVoteList = voteList;
	}

	@Override
	public int getCount() {
		return mVoteList.datas.size();
	}

	@Override
	public Object getItem(int position) {
		return mVoteList.datas.get(position);
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
			holder.titleTv = (TextView) view.findViewById(R.id.title);
			holder.descTv = (TextView) view.findViewById(R.id.desc);
			holder.tagTv = (TextView) view.findViewById(R.id.tag);
			holder.tag2Tv = (TextView) view.findViewById(R.id.tag2);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		VoteItem voteItem = mVoteList.datas.get(position);
		holder.thumbIv.setDefaultImageResId(R.drawable.base_list_default_icon);
		holder.thumbIv.setImageUrl(voteItem.thumbnailurl, CityLifeApp.getInstance().getImageLoader());
		holder.titleTv.setText(voteItem.title);
		holder.descTv.setText(voteItem.summary);
		holder.tagTv.setText("点击" + voteItem.clickNum);
		holder.tag2Tv.setText("评论" + voteItem.commentNum);
		
		return view;
	}
	
	public class ViewHolder {
		NetworkImageView thumbIv;
		TextView titleTv;
		TextView descTv;
		TextView tagTv;
		TextView tag2Tv;
	}	
}
