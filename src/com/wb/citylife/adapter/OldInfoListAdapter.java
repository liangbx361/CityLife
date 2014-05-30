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
import com.wb.citylife.bean.OldInfoList;
import com.wb.citylife.bean.OldInfoList.OldInfoItem;

public class OldInfoListAdapter extends BaseAdapter {
	
	private Context mContext;
	private OldInfoList mOldInfoList;
	
	public OldInfoListAdapter(Context context, OldInfoList oldInfoList) {
		mContext = context;
		mOldInfoList = oldInfoList;
	}

	@Override
	public int getCount() {
		return mOldInfoList.datas.size();
	}

	@Override
	public Object getItem(int position) {
		return mOldInfoList.datas.get(position);
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
			view = LayoutInflater.from(mContext).inflate(R.layout.old_info_item_layout, null);
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
		
		OldInfoItem oldInfoItem = mOldInfoList.datas.get(position);
		holder.thumbIv.setDefaultImageResId(R.drawable.base_list_default_icon);
		holder.thumbIv.setImageUrl(oldInfoItem.thumbnailUrl, CityLifeApp.getInstance().getImageLoader());
		holder.titleTv.setText(oldInfoItem.title);
		holder.descTv.setText(oldInfoItem.summary);
		holder.tag2Tv.setText(oldInfoItem.price+"");
		
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
