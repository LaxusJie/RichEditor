package com.app.functionbar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.app.functionbar.utils.Constant;

import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.MediaRecorderBase;
import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.MediaConfig;
import mabeijianxi.camera.util.StringUtils;
import mabeijianxi.camera.views.SurfaceVideoView;


/**
 * desc：通用单独播放界面
 * author：mgq
 * date：2017-07-04
 */
public class VideoPlayerActivity extends Activity implements
        SurfaceVideoView.OnPlayStateListener, OnErrorListener,
        OnPreparedListener, OnClickListener, OnCompletionListener,
        OnInfoListener {

    private String videoUri;
    private int videoDuration;
    private String videoScreenshot;
    private String playType;

    /**
     * 播放控件
     */
    private SurfaceVideoView mVideoView;
    /**
     * 暂停按钮
     */
    private View mPlayerStatus;
    private View mLoading;

    /**
     * 是否需要回复播放
     */
    private boolean mNeedResume;

    private VideoTime videoTime;

    private Button btnPause;
    private Button btnAgain;
    private Button btnOk;
    private TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        videoUri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI);
        videoScreenshot = intent.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT);
        videoDuration = intent.getIntExtra(MediaRecorderActivity.VIDEO_DURATION, -1);
        playType = intent.getStringExtra(Constant.PLAY_TYPE);
        if (StringUtils.isEmpty(videoUri)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_video_player);
        mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnAgain = (Button) findViewById(R.id.btn_again);
        btnOk = (Button) findViewById(R.id.btn_ok);
        tvTime = (TextView) findViewById(R.id.tv_video_time);
        btnPause.setOnClickListener(this);
        btnAgain.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        setBtnOk(videoDuration == -1 ?View.GONE:View.VISIBLE);

        int screenWidth = getScreenWidth(this);
        int videoHight = (int) (screenWidth / (MediaRecorderBase.SMALL_VIDEO_WIDTH / (MediaRecorderBase.SMALL_VIDEO_HEIGHT * 1.0f)));
        mVideoView.getLayoutParams().height = videoHight;
        mVideoView.requestLayout();

        mPlayerStatus = findViewById(R.id.iv_play);
        mPlayerStatus.setOnClickListener(this);
        mLoading = findViewById(R.id.loading);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnPlayStateListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setVideoPath(videoUri);

    }

    public int getScreenWidth(Activity context) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        int W = mDisplayMetrics.widthPixels;
        return W;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null && mNeedResume) {
            mNeedResume = false;
            if (mVideoView.isRelease())
                mVideoView.reOpen();
            else
                mVideoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        videoPause();
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {// 跟随系统音量走
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                mVideoView.dispatchKeyEvent(this, event);
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onStateChanged(boolean isPlaying) {
        mPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (!isFinishing()) {
            // 播放失败
        }
        finish();
        return false;

    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.iv_play) {
            if (mVideoView.isRelease())
                mVideoView.reOpen();
            else
                mVideoView.start();
            videoTime = new VideoTime(videoDuration / 1000, 1000);
            videoTime.start();
            setVisiblity(View.GONE, View.VISIBLE);
        } else if (vId == R.id.btn_pause) {
            videoPause();
        } else if (vId == R.id.btn_again) {
            finish();
        } else if (vId == R.id.btn_ok) {
            MediaConfig.OnSelectResultCallback mediaCallBack = MediaConfig.getResultCallback();
            if (mediaCallBack != null && videoScreenshot != null) {
                mediaCallBack.onSelectSuccess(videoDuration, videoUri, videoScreenshot);
            }
            finish();
        }
    }

    /**
     * 视频暂停
     */
    private void videoPause() {
        if (mVideoView != null && mVideoView.isPlaying()) {
            mVideoView.pause();
        }

        if (videoTime != null) {
            videoTime.cancel();
        }
        setVisiblity(View.VISIBLE, View.GONE);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setVisiblity(View.VISIBLE, View.GONE);
        if (!isFinishing())
            mVideoView.reOpen();
    }

    /**
     * 设置显隐
     *
     * @param play
     * @param pause
     */
    private void setVisiblity(int play, int pause) {
        mPlayerStatus.setVisibility(play);
        btnAgain.setVisibility(play);
        setBtnOk(play);
        btnPause.setVisibility(pause);
    }

    private void setBtnOk(int visible){
        if(playType!=null && playType.equals(Constant.VIDEO_IMMEDIATE)){
            btnOk.setVisibility(View.GONE);
        }else{
            btnOk.setVisibility(visible);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                // 音频和视频数据不正确
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (!isFinishing())
                    mVideoView.pause();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isFinishing())
                    mVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (DeviceUtils.hasJellyBean()) {
                    mVideoView.setBackground(null);
                } else {
                    mVideoView.setBackgroundDrawable(null);
                }
                break;
        }
        return false;
    }

    /*视频播放倒计时*/
    class VideoTime extends CountDownTimer {

        /**
         * @param millisInFuture
         * @param countDownInterval
         */
        public VideoTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            tvTime.setText((millisUntilFinished / 1000) + "S");
        }

        @Override
        public void onFinish() {
            videoTime.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            videoPause();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
