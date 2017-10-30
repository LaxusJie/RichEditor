package com.app.functionbar.inputview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.app.base.FrameBaseActivity;
import com.app.base.utils.PermissionManager;
import com.app.functionbar.R2;
import com.luck.picture.lib.model.PictureConfig;
import com.tb.emoji.FaceFragment;
import com.yalantis.ucrop.entity.LocalMedia;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.List;

import butterknife.ButterKnife;

/**
 * desc：基类功能栏
 * author：haojie
 * date：2017-09-06
 */
public abstract class BaseInputBar extends AutoLinearLayout {
    private PictureListener pictureListener;
    protected FrameBaseActivity activity;
    protected FaceFragment faceFragment;
    protected AudioFunc audioFunc;
    protected VideoFunc videoFunc;
    protected LinearLayout llInputBar;//功能栏填充布局
    protected AutoRelativeLayout llInputBox;//盒子布局
    protected FrameLayout flInputEmoji;//表情布局
    protected AutoFrameLayout llInputRecord;//录音布局
    protected boolean isFuncShow;//是否功能栏正在显示

    public BaseInputBar(Context context) {
        super(context);
        init(context);
    }

    public BaseInputBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseInputBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        if (!(context instanceof FrameBaseActivity)) {
            throw new IllegalStateException("需要继承BaseActivity");
        }
        this.activity = (FrameBaseActivity) context;
        audioFunc = new AudioFunc(activity);
        videoFunc = new VideoFunc(activity);
        PermissionManager.getInstance().getMediaPermission(activity);
        initView();
    }

    private void initView() {
        initParent();
        initFunc();
        ButterKnife.bind(this);
    }

    /**
     * 初始化父容器
     */
    private void initParent() {
        setOrientation(VERTICAL);
        llInputBar = new LinearLayout(activity);
        llInputBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(llInputBar);
        View vLine = new View(activity);
        vLine.setBackgroundResource(R2.color.colorGray7);
        vLine.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        addView(vLine);
        llInputBox = new AutoRelativeLayout(activity);
        llInputBox.setLayoutParams(new AutoRelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(llInputBox);
    }

    /**
     * 初始化功能
     */
    private void initFunc() {
        initBar();//初始化功能栏
        initEmoji();//表情布局
        initRecord();//音频布局
        setBoxChildGone();//初始化将盒子内布局隐藏
        getKeyBoardHeight();//获取键盘高度
    }

    /**
     * 具体功能栏由子类填充
     */
    public abstract void initBar();

    /**
     * 初始化表情
     */
    protected void initEmoji() {
        if (faceFragment == null) {
            faceFragment = FaceFragment.Instance();
        }
        flInputEmoji = new FrameLayout(activity);
        flInputEmoji.setId(View.generateViewId());
        flInputEmoji.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        llInputBox.addView(flInputEmoji);
        activity.getSupportFragmentManager().beginTransaction().add(flInputEmoji.getId(), faceFragment).commit();
    }

    /**
     * 初始化音频
     */
    private void initRecord() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        audioFunc.view.setLayoutParams(layoutParams);
        llInputBox.addView(audioFunc.view);
        llInputRecord = audioFunc.llFunctionRecord;
    }

    /**
     * 获取软键盘的高度，初始化功能栏高度，让两者保持一致
     */
    public void getKeyBoardHeight() {
        for (int i = 0; i < llInputBox.getChildCount(); i++) {
            ViewGroup.LayoutParams linearParams = llInputBox.getChildAt(i).getLayoutParams();
            linearParams.height = 700;
            llInputBox.getChildAt(i).setLayoutParams(linearParams);
        }
    }

    public void setVisible(@NonNull final View view) {
        setBoxChildGone();
        if (InputTools.isShowKeyboard(activity, this)) {
            InputTools.hideKeyboard(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(VISIBLE);
                }
            }, 200);
        } else {
            view.setVisibility(VISIBLE);
        }
        isFuncShow = true;
    }

    public void setBoxChildGone() {
        for (int i = 0; i < llInputBox.getChildCount(); i++) {
            llInputBox.getChildAt(i).setVisibility(GONE);
        }
        isFuncShow = false;
    }

    /*
    * 选择图片
    * */
    public PictureConfig.OnSelectResultCallback imageCallback = new PictureConfig.OnSelectResultCallback() {
        //图片多选
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            if (pictureListener != null) {
                pictureListener.onFinishPicture(resultList);
            }
        }

        //视频单选
        @Override
        public void onSelectSuccess(LocalMedia media) {
        }
    };

    /**
     * 图片回调监听
     */
    public interface PictureListener {
        void onFinishPicture(List<LocalMedia> list);
    }

    /**
     * 设置图片监听
     */
    public void setPictureListener(PictureListener pictureListener) {
        this.pictureListener = pictureListener;
    }

    public void setAudioCountListener(AudioFunc.AudioCountListener audioCountListener) {
        audioFunc.setAudioCountListener(audioCountListener);
    };

    public void setVideoCountListener(VideoFunc.VideoCountListener videoCountListener) {
        videoFunc.setVideoCountListener(videoCountListener);
    }

    public void setRecordListener(AudioFunc.RecordListener recordListener) {
        audioFunc.setRecordListener(recordListener);
    }

    public void setVideoListener(VideoFunc.VideoListener videoListener) {
        videoFunc.setVideoListener(videoListener);
    }

    public void setEmojiListener(FaceFragment.OnEmojiClickListener listener) {
        faceFragment.setListener(listener);
    }
}
