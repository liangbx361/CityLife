package com.wb.citylife.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.NetConfig;

public class ChannelAdapter extends BaseAdapter {

	private Context mContext;
	private List<DbChannel> mChannelList;

	public ChannelAdapter(Context context, List<DbChannel> channelList, boolean isAdd) {
		mContext = context;
		mChannelList = new ArrayList<DbChannel>();
		for(DbChannel channel : channelList) {
			if(channel.isAdd) {
				mChannelList.add(channel);
			}
		}
		
		if(isAdd) {
			DbChannel addChannel = new DbChannel();
			addChannel.type = ChannelType.CHANNEL_TYPE_ADD;
			addChannel.name = mContext.getString(R.string.add);
			mChannelList.add(addChannel);
		}
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
		View view;
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			view = inflater.inflate(R.layout.type_item_layout, null);
			holder = new ViewHolder();
			holder.icon = (NetworkImageView) view.findViewById(R.id.type_icon);
			holder.name = (TextView) view.findViewById(R.id.type_name);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		DbChannel channel = mChannelList.get(position);
		if(channel.type != ChannelType.CHANNEL_TYPE_ADD) {
			holder.icon.setImageUrl(NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + channel.getImageUrl(), 
					CityLifeApp.getInstance().getImageLoader());
			holder.name.setText(channel.getName());
		} else {
			holder.icon.setImageResource(R.drawable.type_add_icon);
			holder.icon.setDefaultImageResId(R.drawable.type_add_icon);
			holder.name.setText(R.string.add);
		}
		
		return view;
	}

	public class ViewHolder {
		public NetworkImageView icon;
		public TextView name;
	}
}
