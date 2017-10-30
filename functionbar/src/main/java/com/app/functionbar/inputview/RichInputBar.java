package com.app.functionbar.inputview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.functionbar.R;
import com.app.functionbar.R2;
import com.app.functionbar.utils.ImageOptions;
import com.app.functionbar.utils.UIUtils;
import com.luck.picture.lib.model.PictureConfig;

import butterknife.OnClick;

/**
 * desc：富文本功能栏
 * author：haojie
 * date：2017-09-06
 */
public class RichInputBar extends BaseInputBar {

    private static final int FUNC = 0;
    private static final int KEYBOARD = 1;
    //学生作品
    public static final int TYPE_WORK = 11;
    //教师点评
    public static final int TYPE_COMMENT = 12;
    private View focusView;
    private ViewGroup titleLayout;
    private ViewGroup contentLayout;

    public RichInputBar(Context context) {
        super(context);
    }

    public RichInputBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichInputBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initBar() {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_richbar, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llInputBar.addView(view);
        setVisibility(GONE);
    }

    /**
     * 设置标题布局和内容布局，以便动态计算布局高度
     * @param titleLayout 标题布局
     * @param contentLayout 内容布局
     */
    public void setLayout(ViewGroup titleLayout, ViewGroup contentLayout) {
        this.titleLayout = titleLayout;
        this.contentLayout = contentLayout;
    }

    /**
     * 设置焦点view，主要用于初始化设置默认焦点
     * @param focusView
     */
    public void setFocusView(View focusView) {
        this.focusView = focusView;
    }

    /**
     * 默认全部显示，设置类型根据类型显示不同按钮
     * @param type 场景类型
     */
    public void setType(int type) {
        if (type == TYPE_WORK) {
            findViewById(R.id.button_emoji).setVisibility(GONE);
            findViewById(R.id.button_voice).setVisibility(GONE);
            findViewById(R.id.button_video).setVisibility(GONE);
        } else if (type == TYPE_COMMENT) {
            findViewById(R.id.button_video).setVisibility(GONE);
        }
    }

    @OnClick({R2.id.button_emoji, R2.id.button_image, R2.id.button_voice, R2.id.button_video, R2.id.button_keyboard})
    public void onClick(View view) {
        if (isNeedDisable()) return;
        int viewId = view.getId();
        if (viewId == R.id.button_emoji) {
            setVisible(flInputEmoji);
            changeLayoutSize(FUNC);

        } else if (viewId == R.id.button_image) {
            hideKeyboard();
            PictureConfig.getInstance().init(ImageOptions.getImageOption()).openPhoto(activity, imageCallback);

        } else if (viewId == R.id.button_voice) {
            setVisible(llInputRecord);
            audioFunc.clickAudio();
            changeLayoutSize(FUNC);

        } else if (viewId == R.id.button_video) {
            hideKeyboard();
            videoFunc.clickVideo();

        } else if (viewId == R.id.button_keyboard) {
            clickKeyboard();

        }
    }

    private void clickKeyboard() {
        if (InputTools.isShowKeyboard(activity, focusView)) {
            hideKeyboard();
        } else {
            showKeyboard();
        }
    }

    /**
     * 显示键盘
     */
    public void showKeyboard() {
        setBoxChildGone();
        restoreLayoutSize();
        InputTools.showKeyboard(focusView);
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        setBoxChildGone();
        restoreLayoutSize();
        InputTools.hideKeyboard(this);
    }

    /**
     * 改变布局大小
     * @param type 功能或键盘（目前键盘已改成弹出自动计算）
     */
    public void changeLayoutSize(int type) {
        ViewGroup.LayoutParams layoutParams = contentLayout.getLayoutParams();
        if (type == FUNC) {
            layoutParams.height = UIUtils.getContentViewHeight(activity) - llInputRecord.getLayoutParams().height - titleLayout.getMeasuredHeight();
//            layoutParams.height = UIUtils.getContentViewHeight(activity) - llInputBar.getMeasuredHeight() - llInputRecord.getLayoutParams().height;
        } else {
            //目前键盘已改成弹出自动计算,所以暂时弃用手动计算方法
//            layoutParams.height = UIUtils.getContentViewHeight(activity) - activity.sharePreUtil.loadHeight() - llInputBar.getMeasuredHeight();
        }
        contentLayout.setLayoutParams(layoutParams);
    }

    /**
     * 还原布局大小
     */
    private void restoreLayoutSize() {
        if (contentLayout == null) return;//如果一进页面就获取焦点会为空
        ViewGroup.LayoutParams layoutParams = contentLayout.getLayoutParams();
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        contentLayout.setLayoutParams(layoutParams);
    }

    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            focusView = view;
            setVisibility(VISIBLE);
            showKeyboard();
        } else {
            setVisibility(GONE);
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        focusView = view;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            showKeyboard();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 页面关闭或点击home键
     * 判断录音状态，隐藏键盘
     */
    public void onStop() {
        hideKeyboard();
        if (isNeedDisable()) {
            audioFunc.recordAgain();
        }
    }

    /**
     * 页面点返回键先关掉功能布局，再返回
     * @return
     */
    public boolean onBackPress() {
        if (isFuncShow) {
            hideKeyboard();
            return false;
        }
        return true;
    }

    /**
     * 是否需要禁用页面
     * @return
     */
    public boolean isNeedDisable() {
        return audioFunc.isOperation();
    }

}
