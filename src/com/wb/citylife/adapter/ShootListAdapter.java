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
import com.wb.citylife.bean.ShootList;
import com.wb.citylife.bean.ShootList.ShootItem;

public class ShootListAdapter extends BaseAdapter {
	
	private Context mContext;
	private ShootList mShootList;
	
	public ShootListAdapter(Context context, ShootList shootList) {
		mContext = context;
		mShootList = shootList;
	}

	@Override
	public int getCount() {
		return mShootList.datas.size();
	}

	@Override
	public Object getItem(int position) {
		return mShootList.datas.get(position);
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
			view = LayoutInflater.from(mContext).inflate(R.layout.shoot_item_layout, null);
			holder = new ViewHolder();
			holder.thumbIv = (NetworkImageView) view.findViewById(R.id.img);
			holder.titleTv = (TextView) view.findViewById(R.id.title);
			holder.userNameTv = (TextView) view.findViewById(R.id.username);
			holder.commentTv = (TextView) view.findViewById(R.id.comment);
			holder.clickTv = (TextView) view.findViewById(R.id.click);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		ShootItem shootItem = mShootList.datas.get(position);
		holder.thumbIv.setDefaultImageResId(R.drawable.shoot_default_icon);
		holder.thumbIv.setImageUrl(shootItem.thumbnailUrl, CityLifeApp.getInstance().getImageLoader());
		holder.titleTv.setText(shootItem.title);
		holder.commentTv.setText("评论" + shootItem.commentNum + "");
		holder.clickTv.setText("点击" + shootItem.clickNum + "");
		holder.userNameTv.setText(shootItem.name);
		return view;
	}
	
	public class ViewHolder {
		NetworkImageView thumbIv;
		TextView titleTv;
		TextView userNameTv;
		TextView commentTv;
		TextView clickTv;
	}	
}
