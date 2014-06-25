package com.wb.citylife.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.ImagesItem;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.mk.img.ImageBrowseActivity;

public class ImageAdapter extends PagerAdapter implements OnClickListener{
	private int mCount;
	private String[] mImages;
	private List<View> mViewList = new ArrayList<View>();
	private Activity mActivity;
	
	public ImageAdapter(Activity activity, String[] images) {		
		mActivity = activity;
		mImages = images;
		mCount = mImages.length;
		for(int i=0; i<mCount; i++) {
			View view = mActivity.getLayoutInflater().inflate(R.layout.adv_item, null);
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

		NetworkImageView imageView = (NetworkImageView) view;
		imageView.setDefaultImageResId(R.drawable.old_info_default_icon);
		if(mImages[position] != null && !mImages[position].equals("")) {
			imageView.setImageUrl(NetConfig.getPictureUrl(mImages[position]), 
					CityLifeApp.getInstance().getImageLoader());			
		}						
		
		return view;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(mActivity, ImageBrowseActivity.class);
		ImagesItem imgItem = new ImagesItem();
		imgItem.name = "";
		imgItem.imageNum = mImages.length;
		imgItem.images = mImages;
		ArrayList<ImagesItem> imgList = new ArrayList<ImagesItem>();
		imgList.add(imgItem);
		intent.putParcelableArrayListExtra(IntentExtraConfig.ESTATE_IMAGE_DATA, imgList);
		intent.putExtra(IntentExtraConfig.ESTATE_DIS_TAB, false);		
		mActivity.startActivity(intent);
	}
			 
}
