package com.app.base.network.subscribers;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.apkfuns.logutils.LogUtils;
import com.app.base.R;
import com.app.base.network.http.HttpBaseService;
import com.app.base.network.listener.HttpOnNextListener;
import com.app.base.utils.LoadingFragment;
import com.app.base.utils.ToastUtil;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.ref.SoftReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * desc：用于在Http请求开始时，自动显示一个ProgressDialog
 * author：haojie
 * date：2017-06-06
 */
public class ProgressSubscriber<T> extends Subscriber<T> {
    /*是否弹框*/
    private boolean showPorgress = true;
    /* 软引用回调接口*/
    private HttpOnNextListener mSubscriberOnNextListener;
    /*软引用反正内存泄露*/
    private SoftReference<RxAppCompatActivity> mActivity;
    /*加载框可自己定义*/
    private LoadingFragment loadingFragment;

    /**
     * 构造
     *
     * @param httpService
     */
    public ProgressSubscriber(HttpBaseService httpService) {
        this.mSubscriberOnNextListener = httpService.getListener();
        this.mActivity = new SoftReference<>(httpService.getRxAppCompatActivity());
        setShowPorgress(httpService.isShowProgress());
        if (httpService.isShowProgress()) {
            initProgressDialog(httpService.isCancel());
        }
    }


    /**
     * 初始化加载框
     */
    private void initProgressDialog(boolean cancel) {
        FragmentActivity activity = mActivity.get();
        if (loadingFragment == null && activity != null) {
            loadingFragment = LoadingFragment.getInstance();
            if (loadingFragment.isAdded()) {
                activity.getSupportFragmentManager().beginTransaction().show(loadingFragment).commitAllowingStateLoss();
            } else {
                activity.getSupportFragmentManager().beginTransaction().remove(loadingFragment).commitAllowingStateLoss();
            }
            loadingFragment.setCancelable(cancel);
            if (cancel) {
                loadingFragment.setCancelListener(new LoadingFragment.CancelListener() {
                    @Override
                    public void onCancel() {
                        if (mSubscriberOnNextListener != null) {
                            mSubscriberOnNextListener.onCancel();
                        }
                        onCancelProgress();
                    }
                });
            }
        }
    }


    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        if (!isShowPorgress()) return;
        FragmentActivity context = mActivity.get();
        if (loadingFragment == null || context == null) return;
        if (!loadingFragment.isVisible()) {
            loadingFragment.show(context.getSupportFragmentManager());
        }
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (!isShowPorgress()) return;
        loadingFragment.dismissAllowingStateLoss();
    }


    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();
        errorDo(e);
    }

    /*错误统一处理*/
    private void errorDo(Throwable e) {
        Context context = mActivity.get();
        if (context == null) return;
        if (e instanceof SocketTimeoutException) {
            ToastUtil.show(context, context.getString(R.string.network_fail));
        } else if (e instanceof ConnectException) {
            ToastUtil.show(context, context.getString(R.string.network_fail));
        } else {
            ToastUtil.show(context, e.getMessage());
        }
        LogUtils.e(e.getMessage());
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onError(e);
        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            LogUtils.d(t);
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }


    public boolean isShowPorgress() {
        return showPorgress;
    }

    /**
     * 是否需要弹框设置
     *
     * @param showPorgress
     */
    public void setShowPorgress(boolean showPorgress) {
        this.showPorgress = showPorgress;
    }
}