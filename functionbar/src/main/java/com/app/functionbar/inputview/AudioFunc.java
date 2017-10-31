package com.app.functionbar.inputview;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.app.base.FrameBaseActivity;
import com.app.base.utils.EditorDataEntity;
import com.app.base.utils.ToastUtil;
import com.app.functionbar.R;
import com.app.functionbar.R2;
import com.app.functionbar.record.VoiceLineView;
import com.app.functionbar.record.VoiceManager;
import com.app.functionbar.utils.Constant;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * desc：录音功能抽象
 * author：haojie
 * date：2017-09-06
 */
public class AudioFunc {
    @BindView(R2.id.ll_record_ready)
    AutoLinearLayout llRecordReady;//录音未开始
    @BindView(R2.id.vl_record_go)
    VoiceLineView voiceLine;//录音声线
    @BindView(R2.id.tv_record_go_time)
    TextView tvRecordingTime;//录音时间
    @BindView(R2.id.ll_record_go)
    AutoRelativeLayout llRecordGo;//录音中父布局
    @BindView(R2.id.tv_record_over_begintime)
    TextView tvRecordOverBegintime;//录音试听时间
    @BindView(R2.id.tv_record_over_endtime)
    TextView tvRecordOverEndtime;//录音时长
    @BindView(R2.id.ll_record_over_time)
    LinearLayout llRecordOverTime;
    @BindView(R2.id.tv_record_over_save)
    TextView tvRecordSave;//保存
    @BindView(R2.id.ll_record_over)
    AutoRelativeLayout llRecordOver;//录音完成
    @BindView(R2.id.ll_function_record)
    AutoFrameLayout llFunctionRecord;//录音父布局
    @BindView(R2.id.btn_record_go_pause)
    Button btnRecordGoPause;
    @BindView(R2.id.btn_record_go_continue)
    Button btnRecordGoContinue;
    @BindView(R2.id.btn_record_over_play)
    Button btnRecordOverPlay;
    @BindView(R2.id.btn_record_over_pause)
    Button btnRecordOverPause;

    private FrameBaseActivity activity;
    View view;
    private VoiceManager voiceManager;
    private String recordPath;
    private EditorDataEntity audioEditorDataEntity;
    private boolean isOperation;//是否正在操作
    /*音频个数监听器*/
    private AudioCountListener audioCountListener;
    private RecordListener recordListener;

    public AudioFunc(FrameBaseActivity activity) {
        this.activity = activity;
        view = LayoutInflater.from(activity).inflate(R.layout.layout_record_func, null);
        AutoUtils.auto(view);
        ButterKnife.bind(this, view);
        voiceManager = VoiceManager.getInstance();
        voiceManager.setContext(activity);
        initListener();
    }

    public void clickAudio() {
        if (audioCountListener != null) {
            if (audioCountListener.getAudioCount() >= 2) {
                ToastUtil.show(activity, Constant.AUDIO_TIP);
                return;
            }
        }
    }

    @OnClick({R2.id.btn_record_ready_start, R2.id.btn_record_go_pause, R2.id.btn_record_go_continue, R2.id.btn_record_go_stop, R2.id.btn_record_over_play,
            R2.id.btn_record_over_pause, R2.id.tv_record_over_again, R2.id.tv_record_over_save})
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_record_ready_start) {
            if (audioCountListener != null) {
                if (audioCountListener.getAudioCount() >= 2) {
                    ToastUtil.show(activity, Constant.AUDIO_TIP);
                    return;
                }
            }
            isOperation = true;
            llRecordReady.setVisibility(View.GONE);
            llRecordGo.setVisibility(View.VISIBLE);
            voiceManager.startVoiceRecord(Environment.getExternalStorageDirectory().getPath() + "/TaoLun/audio");

        } else if (vId == R.id.btn_record_go_pause) {
            if (voiceManager != null) {
                voiceManager.pauseOrStartVoiceRecord();
            }
            btnRecordGoPause.setVisibility(View.GONE);
            btnRecordGoContinue.setVisibility(View.VISIBLE);

        } else if (vId == R.id.btn_record_go_continue) {
            if (voiceManager != null) {
                voiceManager.pauseOrStartVoiceRecord();
            }
            btnRecordGoContinue.setVisibility(View.GONE);
            btnRecordGoPause.setVisibility(View.VISIBLE);

        } else if (vId == R.id.btn_record_go_stop) {
            llRecordGo.setVisibility(View.GONE);
            llRecordOver.setVisibility(View.VISIBLE);
            if (voiceManager != null) {
                voiceManager.stopVoiceRecord();
            }

        } else if (vId == R.id.btn_record_over_play) {
            if (voiceManager.isStoping()) {
                voiceManager.continueOrPausePlay();
            } else {
                voiceManager.startPlay(recordPath);
            }
            btnRecordOverPause.setVisibility(View.VISIBLE);
            btnRecordOverPlay.setVisibility(View.GONE);

        } else if (vId == R.id.btn_record_over_pause) {
            if (voiceManager.isPlaying()) {
                voiceManager.continueOrPausePlay();
            }
            btnRecordOverPause.setVisibility(View.GONE);
            btnRecordOverPlay.setVisibility(View.VISIBLE);

        } else if (vId == R.id.tv_record_over_again) {
            recordAgain();

        } else if (vId == R.id.tv_record_over_save) {
            recordSave();

        }
    }

    /**
     * 录音完成后重录
     */
    public void recordAgain() {
        voiceManager.stopPlay();
        File file = new File(recordPath == null ? "" : recordPath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        tvRecordOverBegintime.setText("00:00");
        llRecordReady.setVisibility(View.VISIBLE);
        llRecordGo.setVisibility(View.GONE);
        llRecordOver.setVisibility(View.GONE);
        btnRecordGoContinue.setVisibility(View.GONE);
        btnRecordGoPause.setVisibility(View.VISIBLE);
        btnRecordOverPause.setVisibility(View.GONE);
        btnRecordOverPlay.setVisibility(View.VISIBLE);
        isOperation = false;
    }

    /**
     * 录音后保存
     */
    private void recordSave() {
        if (recordListener != null && audioEditorDataEntity != null) {
            recordListener.onFinishRecord(audioEditorDataEntity);
        }
        voiceManager.stopPlay();
        tvRecordOverBegintime.setText("00:00");
        llRecordReady.setVisibility(View.VISIBLE);
        llRecordOver.setVisibility(View.GONE);
        btnRecordGoContinue.setVisibility(View.GONE);
        btnRecordGoPause.setVisibility(View.VISIBLE);
        btnRecordOverPause.setVisibility(View.GONE);
        btnRecordOverPlay.setVisibility(View.VISIBLE);
        audioEditorDataEntity = null;
        isOperation = false;
    }

    public void initListener() {
        //录音监听
        voiceManager.setVoiceRecordListener(new VoiceManager.VoiceRecordCallBack() {
            @Override
            public void recDoing(long time, String strTime) {
                tvRecordingTime.setText(strTime);
                if (time == 300) {
                    voiceManager.stopVoiceRecord();
                    voiceLine.setPause();
                }
            }

            @Override
            public void recVoiceGrade(int grade) {
                voiceLine.setVolume(grade);
            }

            @Override
            public void recStart(boolean init) {
                voiceLine.setContinue();
            }

            @Override
            public void recPause(String str) {
                voiceLine.setPause();
            }

            @Override
            public void recException() {
                recordAgain();
            }

            @Override
            public void recFinish(long length, String strLength, String path) {
                recordPath = path;
                tvRecordOverEndtime.setText("/" + strLength);
                audioEditorDataEntity = new EditorDataEntity();
                audioEditorDataEntity.setUrl(path);
                audioEditorDataEntity.setType(EditorDataEntity.TYPE_AUDIO);
                audioEditorDataEntity.setMediaDuration(strLength);
                LogUtils.d("音频时长:"+strLength+"|");
            }
        });

        //播放录音监听
        voiceManager.setVoicePlayListener(new VoiceManager.VoicePlayCallBack() {
            @Override
            public void voiceTotalLength(long time, String strTime) {
            }

            @Override
            public void playDoing(long time, String strTime) {
                tvRecordOverBegintime.setText(strTime);
            }

            @Override
            public void playPause() {
            }

            @Override
            public void playStart() {
            }

            @Override
            public void playFinish() {
                btnRecordOverPlay.setVisibility(View.VISIBLE);
                btnRecordOverPause.setVisibility(View.GONE);
                tvRecordOverBegintime.setText("00:00");
            }
        });
    }

    public boolean isOperation() {
        return isOperation;
    }

    /**
     * 获取录音个数监听
     */
    public interface AudioCountListener {
        int getAudioCount();
    }

    /**
     * 录音回调监听
     */
    public interface RecordListener {
        void onFinishRecord(EditorDataEntity editorDataEntity);
    }

    public void setAudioCountListener(AudioCountListener audioCountListener) {
        this.audioCountListener = audioCountListener;
    }

    /**
     * 设置录音监听
     */
    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }
}
