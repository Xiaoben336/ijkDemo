package com.example.zjf.ijkdemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MainActivity extends AppCompatActivity implements VideoPlayerListener {
	private static final String TAG = "MainActivity";
	private VideoPlayerFrame mVideoPlayerFrame = null;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		mVideoPlayerFrame = (VideoPlayerFrame) findViewById(R.id.videoPlayerFrame);
		mVideoPlayerFrame.setVideoListener(this);
		mVideoPlayerFrame.setPath("http://223.110.242.130:6610/gitv/live1/G_CCTV-1-HQ/1.m3u8");
		try {
			mVideoPlayerFrame.load();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mContext,"播放失败",Toast.LENGTH_SHORT);
		}
	}

	@Override
	protected void onResume() {
		if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		super.onResume();
	}

	@Override
	public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
		Log.d(TAG,"onBufferingUpdate i = " + i);
	}

	@Override
	public void onCompletion(IMediaPlayer iMediaPlayer) {
		Log.d(TAG,"onCompletion");
	}

	@Override
	public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
		Log.d(TAG,"onError");
		mVideoPlayerFrame.stop();
		mVideoPlayerFrame.release();
		return false;
	}

	@Override
	public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
		return false;
	}

	@Override
	public void onPrepared(IMediaPlayer iMediaPlayer) {
		Log.d(TAG,"onPrepared");
		mVideoPlayerFrame.start();
	}

	@Override
	public void onSeekComplete(IMediaPlayer iMediaPlayer) {

	}

	@Override
	public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

	}
}
