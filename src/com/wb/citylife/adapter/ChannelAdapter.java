package com.wb.citylife.adapter;

import java.util.LinkedList;
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
import com.wb.citylife.db.DbHelper;

public class ChannelAdapter extends BaseAdapter {

	private Context mContext;
	private List<DbChannel> mChannelList;
	private LinkedList<DbChannel> mShowList;

	public ChannelAdapter(Context context, List<DbChannel> channelList, boolean isAdd) {
		mContext = context;
		mChannelList = channelList;
		mShowList = new LinkedList<DbChannel>();
		
		for(DbChannel channel : mChannelList) {
			if(channel.isAdd) {
				mShowList.add(channel);
			}
		}
		
		if(isAdd) {
			DbChannel addChannel = new DbChannel();
			addChannel.type = ChannelType.CHANNEL_TYPE_ADD;
			addChannel.name = mContext.getString(R.string.add);
			mShowList.add(addChannel);
		}
	}

	@Override
	public int getCount() {
		return mShowList.size();
	}

	@Override
	public Object getItem(int position) {
		return mShowList.get(position);
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
		
		DbChannel channel = mShowList.get(position);
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
	
	/**
	 * 增加栏目
	 * @param channel
	 */
	public void addChannel(DbChannel channel) {
		mShowList.add(mShowList.size()-2, channel);
		notifyDataSetChanged();
	}
	
	/**
	 * 删除栏目
	 * @param position
	 */
	public void delChannel(int position) {
		mShowList.get(position).setAdd(false);
		DbHelper.orderChannel(mChannelList);		
		
		mShowList.remove(position);
		notifyDataSetChanged();				
	}
	
	/**
	 * 根据显示位置获取在真实列表中的栏目
	 * @param position
	 * @return
	 */
	public DbChannel getRealChannel(int position) {
		DbChannel showChannel = mShowList.get(position);
		for(DbChannel channel : mChannelList) {
			if(showChannel.channelId.equals(channel.channelId)) {
				return channel;				
			}
		}
		return null;
	}
	
	public class ViewHolder {
		public NetworkImageView icon;
		public TextView name;
	}
}
