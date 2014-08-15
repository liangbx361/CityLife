package com.wb.citylife.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.DbChannel;

public class OrderChannelAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<DbChannel> mChannelList;

	public OrderChannelAdapter(Context context, List<DbChannel> list) {
		mContext = context;
		mChannelList = list;
	}

	@Override
	public int getCount() {

		return mChannelList.size();
	}

	@Override
	public Object getItem(int position) {

		return mChannelList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			view = inflater.inflate(R.layout.dslvlist_item_handle_right, null);
			holder = new ViewHolder();
			holder.nameTv = (TextView) view.findViewById(R.id.name);
			holder.typeIconIv = (NetworkImageView) view.findViewById(R.id.type_icon);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		DbChannel channel = mChannelList.get(position);
		holder.nameTv.setText(channel.name);
		if(!TextUtils.isEmpty(channel.imageUrl)) {
			holder.typeIconIv.setImageUrl(channel.imageUrl, CityLifeApp.getInstance().getImageLoader());
		} else {
			holder.typeIconIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.trans));
		}
		return view;
	}

	class ViewHolder {
		NetworkImageView typeIconIv;
		TextView nameTv;
	}
}
