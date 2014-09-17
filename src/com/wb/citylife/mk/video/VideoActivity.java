package com.wb.citylife.mk.video;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.config.IntentExtraConfig;

public class VideoActivity extends Activity{
	

//	private String path = "http://192.168.10.109:8081/CityLife/videos/love.flv";
	private String path = "rtsp://218.204.223.237:554/live/1/67A7572844E51A64/wkr226mcctpkzwxh.sdp";
	private VideoView mVideoView;
	private LinearLayout mLoadLayout;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		
		setContentView(R.layout.activity_video);
		
		getIntentData();
		initView();
		
		if (TextUtils.isEmpty(path)) {
			// Tell the user to provide a media file URL/path.
			Toast.makeText(VideoActivity.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
			return;
		} else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
			mVideoView.setVideoPath(path);
			mVideoView.setMediaController(new MediaController(this));
			mVideoView.requestFocus();
			
			//播放结束/播放失败处理
			mVideoView.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					finish();
				}
			});

			mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					// optional need Vitamio 4.0
					mediaPlayer.setPlaybackSpeed(1.0f);
					mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
					mLoadLayout.setVisibility(View.GONE);
				}
			});			
			mVideoView.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
				
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					DebugConfig.showLog("videoActivity", "percent=" + percent);
					if(percent > 50) {
						mLoadLayout.setVisibility(View.GONE);
						mVideoView.setVisibility(View.VISIBLE);
//						if(!mVideoView.isPlaying()) {
//							mVideoView.start();							
//						}
					} else {
//						mLoadLayout.setVisibility(View.VISIBLE);
//						mVideoView.setVisibility(View.INVISIBLE);
						if(mVideoView.isPlaying()) {
							mVideoView.pause();
						}
					}
					
				}
			});
		}
	}
	
	private void getIntentData() {
		path = getIntent().getStringExtra(IntentExtraConfig.VIDEO_PATH);
	}
	
	private void initView() {
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mLoadLayout = (LinearLayout) findViewById(R.id.loading_layout);
	}
	
	public void openVideo(View View) {
	  mVideoView.setVideoPath(path);
	}
	
	
	public void openVideo2(View view) {
	  mVideoView.setVideoPath(path);
	}
	
	public void onConfigurationChanged(Configuration newConfig) { 
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		
		if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {			 
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(attrs); 
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); 
			mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
		} else {
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN); 			
			getWindow().setAttributes(attrs); 
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
			mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
		}
		
		super.onConfigurationChanged(newConfig);
	}
}
