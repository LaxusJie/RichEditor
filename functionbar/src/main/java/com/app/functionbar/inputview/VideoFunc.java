package com.app.functionbar.inputview;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;

import com.app.base.FrameBaseActivity;
import com.app.base.utils.EditorDataEntity;
import com.app.base.utils.ToastUtil;
import com.app.functionbar.R;
import com.app.functionbar.VideoPlayerActivity;
import com.app.functionbar.dialog.VerticalDialog;
import com.app.functionbar.record.VoiceTimeUtils;
import com.app.functionbar.utils.Constant;
import com.app.functionbar.utils.ImageOptions;
import com.app.functionbar.utils.VideoOptions;
import com.luck.picture.lib.model.PictureConfig;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

import mabeijianxi.camera.LocalMediaCompress;
import mabeijianxi.camera.model.AutoVBRMode;
import mabeijianxi.camera.model.BaseMediaBitrateConfig;
import mabeijianxi.camera.model.LocalMediaConfig;
import mabeijianxi.camera.model.OnlyCompressOverBean;
import mabeijianxi.camera.util.MediaConfig;
import mabeijianxi.camera.util.StringUtils;

/**
 * desc：视频功能抽象
 * author：haojie
 * date：2017-09-06
 */
public class VideoFunc {
    private FrameBaseActivity activity;
    private VoiceTimeUtils ts;
    private VideoListener videoListener;
    private ProgressDialog mProgressDialog;

    public VideoFunc(FrameBaseActivity activity) {
        this.activity = activity;
        VideoOptions.initSmallVideo(activity);
    }

    /*视频个数监听器*/
    private VideoCountListener videoCountListener;

    VerticalDialog.ClickAction1Listener clickAction1Listener = new VerticalDialog.ClickAction1Listener() {
        @Override
        public void click() {
            MediaConfig.getInstance().goSmallVideoRecorder(activity, VideoPlayerActivity.class.getName(), VideoOptions.getVideoConfig(), mediaCallBack);
        }
    };
    VerticalDialog.ClickAction2Listener clickAction2Listener = new VerticalDialog.ClickAction2Listener() {
        @Override
        public void click() {
            PictureConfig.getInstance().init(ImageOptions.getVideoOption()).openPhoto(activity, resultCallback);
        }
    };

    public void clickVideo() {
        if (videoCountListener != null && videoCountListener.getVideoCount() >= 2) {
            ToastUtil.show(activity, Constant.VIDEO_TIP);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.DIALOG_TEXT_TOP, activity.getString(R.string.shoot_video));
        bundle.putString(Constant.DIALOG_TEXT_BOTTOM, activity.getString(R.string.select_video));
        VerticalDialog.newInstance(bundle, clickAction1Listener, clickAction2Listener).show(activity.getFragmentManager(), "show");
    }

    private void compressVideo(LocalMedia media) {
        ts = VoiceTimeUtils.timeSpanSecond(media.getDuration() / 1000);
        final EditorDataEntity videoSelectEditorDataEntity = new EditorDataEntity();
        videoSelectEditorDataEntity.setType(EditorDataEntity.TYPE_VIDEO);
        videoSelectEditorDataEntity.setMediaDuration(String.format("%02d:%02d",
                ts.mSpanMinute, ts.mSpanSecond));

        BaseMediaBitrateConfig compressMode = new AutoVBRMode();//压缩配置
        compressMode.setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST);

        LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
        final LocalMediaConfig config = buidler
                .setVideoPath(media.getPath())
                .captureThumbnailsTime(1)
                .doH264Compress(compressMode)
                .setFramerate(20)
                .build();

        new Thread() {
            @Override
            public void run() {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress("", "请稍等，正在压缩中...", -1);
                    }
                });

                OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                    }
                });
                videoSelectEditorDataEntity.setUrl(onlyCompressOverBean.getVideoPath());
                videoSelectEditorDataEntity.setPoster(onlyCompressOverBean.getPicPath());

                //回调
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        videoListener.onFinishVideo(videoSelectEditorDataEntity);
                    }
                });
            }
        }.start();
    }

    /**
     * 视频录制回调
     */
    private MediaConfig.OnSelectResultCallback mediaCallBack = new MediaConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(int duration, String path, String thumbnailPath) {
            EditorDataEntity videoRecordEditorDataEntity = new EditorDataEntity();
            ts = VoiceTimeUtils.timeSpanSecond(duration / 1000);
            videoRecordEditorDataEntity.setType(EditorDataEntity.TYPE_VIDEO);
            videoRecordEditorDataEntity.setMediaDuration(String.format("%02d:%02d",
                    ts.mSpanMinute, ts.mSpanSecond));
            videoRecordEditorDataEntity.setUrl(path);
            videoRecordEditorDataEntity.setPoster(thumbnailPath);
            videoListener.onFinishVideo(videoRecordEditorDataEntity);
        }
    };

    /**
     * 选择图片
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
        }

        //单选
        @Override
        public void onSelectSuccess(LocalMedia media) {
            compressVideo(media);
        }
    };

    private void showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(activity, theme);
            else
                mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 视频回调监听
     */
    public interface VideoListener {
        void onFinishVideo(EditorDataEntity editorDataEntity);
    }

    /**
     * 获取视频个数监听
     */
    public interface VideoCountListener {
        int getVideoCount();
    }

    public void setVideoCountListener(VideoCountListener videoCountListener) {
        this.videoCountListener = videoCountListener;
    }

    /**
     * 设置视频监听
     */
    public void setVideoListener(VideoListener videoListener) {
        this.videoListener = videoListener;
    }

}
