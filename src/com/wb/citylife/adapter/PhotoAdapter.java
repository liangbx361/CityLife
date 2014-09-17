package com.wb.citylife.adapter;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.common.media.BitmapHelper;
import com.wb.citylife.R;

public class PhotoAdapter extends BaseAdapter {
	
	public int maxNum = 6;
	public int itemWidth = 96;

	private Activity mActivity;
	private List<File> fileList;
	private List<SoftReference<Bitmap>> photoList;

	public PhotoAdapter(Activity activity, List<File> fileList, List<SoftReference<Bitmap>> photoList) {
		mActivity = activity;
		this.fileList = fileList;
		this.photoList = photoList;
		
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		itemWidth = (int) (itemWidth * dm.density);
	}

	@Override
	public int getCount() {
		return fileList.size();
	}

	@Override
	public Object getItem(int position) {
		return fileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHoler holder;
		if (convertView == null) {
			view = LayoutInflater.from(mActivity).inflate(R.layout.photo_item, null);
			holder = new ViewHoler();
			holder.photoIv = (ImageView) view.findViewById(R.id.photo);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHoler) view.getTag();
		}
		
		File picFile = fileList.get(position);
		if(picFile == null) {
			holder.photoIv.setImageResource(R.drawable.publish_add_image_bg);
		} else {
			Bitmap roateBmp = null;
			if(position >= photoList.size()) {
				Bitmap bitmap = BitmapHelper.getScaleBitmap(picFile.getAbsolutePath(), itemWidth);
				int degree = BitmapHelper.readPictureDegree(picFile.getAbsolutePath());
				roateBmp = BitmapHelper.rotaingImageView(degree, 1.0f, bitmap);
				photoList.add(new SoftReference<Bitmap>(roateBmp));
//				bitmap.recycle();
			} else {
				roateBmp = photoList.get(position).get();
				if(roateBmp == null) {
					Bitmap bitmap = BitmapHelper.getScaleBitmap(picFile.getAbsolutePath(), itemWidth);
					int degree = BitmapHelper.readPictureDegree(picFile.getAbsolutePath());
					roateBmp = BitmapHelper.rotaingImageView(degree, 1.0f, bitmap);
					photoList.set(position, new SoftReference<Bitmap>(roateBmp));
					Log.d("reload_bmp", "reload bitmap");
				}
			}				
			
			if(roateBmp != null) {
				holder.photoIv.setImageBitmap(roateBmp);
			}
		}						
		
		return view;
	}

	public class ViewHoler {
		ImageView photoIv;;
	}
	
	public void recycleBmp() {
		for(SoftReference<Bitmap> bmp : photoList) {
			if(bmp.get() != null)
				bmp.get().recycle();
		}
	}
}
