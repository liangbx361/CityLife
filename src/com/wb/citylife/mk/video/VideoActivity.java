package com.wb.citylife.mk.video;

import com.wb.citylife.R;
import com.wb.citylife.config.IntentExtraConfig;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class VideoActivity extends Activity{
	

//	private String path = "http://192.168.10.109:8081/CityLife/videos/love.flv";
	private String path = "rtsp://218.204.223.237:554/live/1/67A7572844E51A64/wkr226mcctpkzwxh.sdp";
	private VideoView mVideoView;

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

			mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					// optional need Vitamio 4.0
					mediaPlayer.setPlaybackSpeed(1.0f);
				}
			});
		}

	}
	
	private void getIntentData() {
		path = getIntent().getStringExtra(IntentExtraConfig.VIDEO_PATH);
	}
	
	private void initView() {
		mVideoView = (VideoView) findViewById(R.id.surface_view);
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
