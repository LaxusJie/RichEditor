package com.app.functionbar.record;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.app.functionbar.R;
import com.app.functionbar.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * desc：顶部音频播放器
 * author：mgq
 * date：2017-07-06
 */

public class AudioPlayer extends LinearLayout {

    @BindView(R2.id.btn_record_over_pause)
    Button btnPlayPause;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_begin_time)
    TextView tvBeginTime;
    @BindView(R2.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R2.id.tv_complete)
    TextView tvComplete;
    VoiceManager voiceManager;
    private String recordPath;

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }

    public AudioPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        voiceManager = VoiceManager.getInstance();
        voiceManager.setContext(context);
        initLayout(context);
    }

    private void initLayout(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_audio_player, null);
        addView(view);
        ButterKnife.bind(this, view);
    }

    VoiceManager.VoicePlayCallBack voicePlayCallBack = new VoiceManager.VoicePlayCallBack() {
        @Override
        public void voiceTotalLength(long time, String strTime) {
            tvEndTime.setText(strTime);
            progressBar.setMax((int) time);
            LogUtils.d("音频时长：" + strTime);
        }

        @Override
        public void playDoing(long time, String strTime) {
            tvBeginTime.setText(strTime);
            progressBar.setProgress((int) time);
        }

        @Override
        public void playPause() {
        }

        @Override
        public void playStart() {
        }

        @Override
        public void playFinish() {
            stopPlay();
        }
    };

    public void play() {
        this.setVisibility(View.VISIBLE);
        voiceManager.setVoicePlayListener(voicePlayCallBack);
        voiceManager.startPlay(recordPath);
        findViewById(R2.id.btn_record_over_pause).setBackgroundResource(R2.drawable.drawable_audio_pause_selector);
    }

    @OnClick({R2.id.btn_record_over_pause, R2.id.tv_complete})
    public void onActionClick(View view) {
        switch (view.getId()) {
            case R2.id.btn_record_over_pause:
                if (voiceManager.isPlaying()) {
                    view.setBackgroundResource(R2.drawable.drawable_audio_play_selector);
                } else {
                    view.setBackgroundResource(R2.drawable.drawable_audio_pause_selector);
                }
                voiceManager.continueOrPausePlay();
                break;
            case R2.id.tv_complete:
                stopPlay();
                break;
        }
    }

    public void stopPlay() {
        this.setVisibility(View.GONE);
        tvBeginTime.setText("00:00");
        tvEndTime.setText("00:00");
        if (voiceManager != null) {
            voiceManager.stopPlay();
        }
    }
}
