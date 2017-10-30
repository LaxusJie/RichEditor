package com.app.base.network.http;

import com.app.base.network.listener.HttpOnNextListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.ref.SoftReference;

import rx.Observable;
import rx.functions.Func1;

/**
 * desc：请填写相关类描述
 * author：haojie
 * date：2017-06-16
 */
public abstract class HttpBaseService<M, T> implements Func1<M, T> {
    /*rx生命周期管理*/
    private SoftReference<RxAppCompatActivity> rxAppCompatActivity;
    /*传递请求*/
    private Observable observable;
    /*回调*/
    private HttpOnNextListener listener;
    /*是否能取消加载框*/
    private boolean cancel;
    /*是否显示加载框*/
    private boolean showProgress;

    public HttpBaseService(RxAppCompatActivity rxAppCompatActivity, Observable observable, HttpOnNextListener listener) {
        setRxAppCompatActivity(rxAppCompatActivity);
        setObservable(observable);
        setListener(listener);
    }

    public RxAppCompatActivity getRxAppCompatActivity() {
        return rxAppCompatActivity.get();
    }

    public void setRxAppCompatActivity(RxAppCompatActivity rxAppCompatActivity) {
        this.rxAppCompatActivity = new SoftReference(rxAppCompatActivity);
    }

    public Observable getObservable() {
        return observable;
    }

    public void setObservable(Observable observable) {
        this.observable = observable;
    }

    public HttpOnNextListener getListener() {
        return listener;
    }

    public void setListener(HttpOnNextListener listener) {
        this.listener = listener;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }
}
