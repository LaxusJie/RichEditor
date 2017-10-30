package com.app.base.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.base.R;


/**
 * desc：加载动画框
 * author：ls
 * date：2017/6/23
 */
public class LoadingFragment extends DialogFragment {

    private AnimationDrawable mAnimation;
    protected CancelListener cancelListener;

    public LoadingFragment() {

    }

    private static class LoadingContainer {
        private static LoadingFragment instance = new LoadingFragment();
    }

    public static LoadingFragment getInstance() {
        return LoadingContainer.instance;
    }

    public static LoadingFragment newInstance(Bundle bundle) {
        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_loading_progressbar, container, false);
        ImageView iv_loading = (ImageView) view.findViewById(R.id.iv_loading);
        mAnimation = (AnimationDrawable) iv_loading.getBackground();
        mAnimation.start();
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        return dialog;
    }

    public void show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        if (!this.isAdded()) {
            ft.add(this, "loading");
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public void dismissAllowingStateLoss() {
        if (getDialog() != null && getDialog().isShowing()) {
            if (mAnimation.isRunning()) {
                mAnimation.stop();
            }
            super.dismissAllowingStateLoss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        cancelListener.onCancel();
    }

    /**
     * 接收dialog listener对象
     *
     * @param cancelListener
     */
    public void setCancelListener(CancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public interface CancelListener {
        void onCancel();
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;

        window.setAttributes(windowParams);
    }
}
