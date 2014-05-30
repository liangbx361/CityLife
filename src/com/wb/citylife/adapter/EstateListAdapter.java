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
import com.wb.citylife.bean.EstateList;
import com.wb.citylife.bean.EstateList.EstateItem;

public class EstateListAdapter extends BaseAdapter {
	
	private Context mContext;
	private EstateList mEstateList;
	
	public EstateListAdapter(Context context, EstateList estateList) {
		mContext = context;
		mEstateList = estateList;
	}

	@Override
	public int getCount() {
		return mEstateList.datas.size();
	}

	@Override
	public Object getItem(int position) {
		return mEstateList.datas.get(position);
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
			view = LayoutInflater.from(mContext).inflate(R.layout.estate_item_layout, null);
			holder = new ViewHolder();
			holder.thumbIv = (NetworkImageView) view.findViewById(R.id.img);
			holder.nameTv = (TextView) view.findViewById(R.id.name);
			holder.addressTv = (TextView) view.findViewById(R.id.address);
			holder.priceTv = (TextView) view.findViewById(R.id.price);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		EstateItem estateItem = mEstateList.datas.get(position);
		holder.thumbIv.setDefaultImageResId(R.drawable.shoot_default_icon);
		holder.thumbIv.setImageUrl(estateItem.thumbnailUrl, CityLifeApp.getInstance().getImageLoader());
		holder.nameTv.setText(estateItem.name);
		holder.addressTv.setText(estateItem.address);
		holder.priceTv.setText(estateItem.averagePrice+"");
		return view;
	}
	
	public class ViewHolder {
		NetworkImageView thumbIv;
		TextView nameTv;
		TextView addressTv;
		TextView priceTv;
	}	
}
