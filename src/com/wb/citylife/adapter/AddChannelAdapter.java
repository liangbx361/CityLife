package com.wb.citylife.adapter;

import java.util.List;

import com.wb.citylife.R;
import com.wb.citylife.bean.db.DbChannel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddChannelAdapter extends BaseAdapter implements OnClickListener{
	
	private List<DbChannel> mChannelList;
	private Context mContext;
	
	public AddChannelAdapter(Context context, List<DbChannel> channelList) {
		mContext = context;
		mChannelList = channelList;
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
			view = inflater.inflate(R.layout.channel_add_layout, null);
			holder = new ViewHolder();
			holder.nameTv = (TextView) view.findViewById(R.id.name);
			holder.addIv = (ImageView) view.findViewById(R.id.edit);
			holder.editVg = (ViewGroup) view.findViewById(R.id.edit_layout);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		DbChannel channel = mChannelList.get(position);
		holder.nameTv.setText(channel.name);
		if(channel.isAdd) {
			holder.addIv.setImageResource(R.drawable.channellist_hasadd_icon);
		} else {
			holder.addIv.setImageResource(R.drawable.channellist_noadd_icon);
		}
		
		holder.editVg.setTag(position + "");
		holder.editVg.setOnClickListener(this);
		
		return view;
	}
	
	class ViewHolder {
		TextView nameTv;
		ImageView addIv;
		ViewGroup editVg;
	}

	@Override
	public void onClick(View view) {
		int position = Integer.parseInt(view.getTag().toString());
		DbChannel channel = mChannelList.get(position);
		ImageView addIv = (ImageView) view.findViewById(R.id.edit);
		if(channel.isAdd) {
			addIv.setImageResource(R.drawable.channellist_noadd_icon);
			channel.isAdd = false;
		} else {
			addIv.setImageResource(R.drawable.channellist_hasadd_icon);
			channel.isAdd = true;
		}
	}

}
