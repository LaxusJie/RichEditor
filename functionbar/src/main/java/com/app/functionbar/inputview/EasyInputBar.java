package com.app.functionbar.inputview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.app.functionbar.R;
import com.app.functionbar.R2;
import com.app.functionbar.utils.ImageOptions;
import com.luck.picture.lib.model.PictureConfig;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * desc：包含输入框的功能栏
 * author：haojie
 * date：2017-09-06
 */
public class EasyInputBar extends BaseInputBar {

    View llEasybarFunc;
    @BindView(R2.id.et_easybar_input)
    public EditText etEasybarInput;
    private OnSendListener sendListener;
    public EasyInputBar(Context context) {
        super(context);
    }

    public EasyInputBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initBar() {
        View layoutEasyBar = LayoutInflater.from(activity).inflate(R.layout.layout_easybar, null);
        layoutEasyBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llInputBar.addView(layoutEasyBar);
        llEasybarFunc = LayoutInflater.from(activity).inflate(R.layout.layout_easybar_func, null);
        llEasybarFunc.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        llInputBox.addView(llEasybarFunc);
    }

    @OnClick({R2.id.btn_easybar_open, R2.id.btn_easybar_emoji, R2.id.btn_easybar_send, R2.id.iv_easybar_func_photo, R2.id.iv_easybar_func_record,R2.id.et_easybar_input})
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_easybar_open) {
            setVisible(llEasybarFunc);
        } else if (viewId == R.id.btn_easybar_emoji) {
            setVisible(flInputEmoji);
        } else if (viewId == R.id.btn_easybar_send) {
            sendListener.sendClick(etEasybarInput.getText().toString());
        } else if (viewId == R.id.iv_easybar_func_photo) {
            PictureConfig.getInstance().init(ImageOptions.getImageOption()).openPhoto(activity, imageCallback);
        } else if (viewId == R.id.iv_easybar_func_record) {
            setVisible(llInputRecord);
        } else if (viewId == R.id.et_easybar_input) {
            setBoxChildGone();
            InputTools.showKeyboard(this);
        }
    }

    @OnFocusChange(R2.id.et_easybar_input)
    public void onFocusChanged(boolean focused) {
        setBoxChildGone();
    }

    public void showKeyboard() {
        etEasybarInput.requestFocus();
        InputTools.showKeyboard(etEasybarInput);
    }

    public void hideKeyboard() {
        etEasybarInput.clearFocus();
        setBoxChildGone();
        InputTools.hideKeyboard(this);
    }

    public EditText getEditText() {
        return etEasybarInput;
    }

    public void setText(String content) {
        etEasybarInput.setText(content);
        etEasybarInput.setSelection(content.length());
    }

    public void clearEditText() {
        etEasybarInput.setText("");
    }

    public interface OnSendListener {
        void sendClick(String text);
    }

    public void setSendListener(OnSendListener sendListener) {
        this.sendListener = sendListener;
    }

}
