package com.example.zjf.ijkdemo;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.android.exoplayer.util.NalUnitUtil;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoPlayerFrame extends FrameLayout {
	private static final String TAG = "VideoPlayerFrame";

	/**
	 * 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
	 */
	private IMediaPlayer mIMediaPlayer = null;
	/**
	 * 视频请求header
	 */
	private Map<String,String> mHeader;
	private SurfaceView mSurfaceView = null;
	/**
	 * 是否硬解码
	 */
	private boolean mEnableMediaCodec = false;
	/**
	 * 视频文件路径
	 */
	private String mPath;

	private Context mContext;

	private AudioManager mAudioManager;

	private VideoPlayerListener mListener;

	public VideoPlayerFrame(Context context) {
		this(context,null);
	}

	public VideoPlayerFrame(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public VideoPlayerFrame(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init(mContext);
	}

	/**
	 *
	 * @param context
	 */
	private void init(Context context){
		setBackgroundColor(Color.BLACK);
		createSurfaceView();
		mAudioManager = (AudioManager)mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
	}

	private void createSurfaceView() {
		mSurfaceView = new SurfaceView(mContext);
		mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				if (mIMediaPlayer != null) {
					mIMediaPlayer.setDisplay(holder);
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {

			}
		});
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
				, LayoutParams.MATCH_PARENT, Gravity.CENTER);
		addView(mSurfaceView,0,layoutParams);
	}

	/**
	 * 设置ijkplayer的监听
	 */
	private void setListener(IMediaPlayer player){
		player.setOnPreparedListener(mPreparedListener);
		player.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
	}
	/**
	 * 设置自己的player回调
	 */
	public void setVideoListener(VideoPlayerListener listener){
		mListener = listener;
	}
	/**
	 * 设置播放地址
	 * @param path
	 */
	public void setPath(String path) {
		setPath(path,null);
	}

	public void setPath(String path,Map<String,String> header){
		mPath = path;
		mHeader = header;
	}

	/**
	 * 加载视频
	 * @throws IOException
	 */
	public void load() throws IOException {
		if (mIMediaPlayer != null) {
			mIMediaPlayer.stop();
			mIMediaPlayer.release();
		}
		mIMediaPlayer = createPlayer();
		setListener(mIMediaPlayer);
		mIMediaPlayer.setDisplay(mSurfaceView.getHolder());
		mIMediaPlayer.setDataSource(mContext, Uri.parse(mPath),mHeader);

		mIMediaPlayer.prepareAsync();
	}

	/**
	 * 开始播放视频
	 */
	public void start() {
		if (mIMediaPlayer != null) {
			mIMediaPlayer.start();
		}
	}

	/**
	 * 暂停播放视频
	 */
	public void pause() {
		if (mIMediaPlayer != null) {
			mIMediaPlayer.pause();
		}
	}

	/**
	 *停止播放视频
	 */
	public void stop() {
		if (mIMediaPlayer != null) {
			mIMediaPlayer.stop();
		}
	}

	/**
	 * 重新播放视频
	 */
	public void reset() {
		if (mIMediaPlayer != null) {
			mIMediaPlayer.reset();
		}
	}

	/**
	 *释放对象
	 */

	public void release() {
		if (mIMediaPlayer != null) {
			mIMediaPlayer.reset();
			mIMediaPlayer.release();
			mIMediaPlayer = null;
		}
	}
	/**
	 * 创建IMediaPlayer
	 * @return
	 */
	private IMediaPlayer createPlayer(){
		IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);

		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

		ijkMediaPlayer.setVolume(1.0f, 1.0f);

		setEnableMediaCodec(ijkMediaPlayer,mEnableMediaCodec);
		return ijkMediaPlayer;
	}

	/**
	 * 是否开启硬解码
	 * @param ijkMediaPlayer
	 * @param isEnable
	 */
	private void setEnableMediaCodec(IjkMediaPlayer ijkMediaPlayer, boolean isEnable) {
		int value = isEnable ? 1 : 0;
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);//开启硬解码
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
		ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
	}


	private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener(){

		@Override
		public void onPrepared(IMediaPlayer iMediaPlayer) {
			if(mListener != null){
				mListener.onPrepared(iMediaPlayer);
			}
		}
	};

	private IMediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
		@Override
		public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
			int videoWidth = iMediaPlayer.getVideoWidth();
			int videoHeight = iMediaPlayer.getVideoHeight();
			if (videoWidth != 0 && videoHeight != 0) {
				mSurfaceView.getHolder().setFixedSize(videoWidth, videoHeight);
			}
		}
	};
}
