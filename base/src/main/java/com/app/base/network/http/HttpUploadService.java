package com.app.base.network.http;

import com.app.base.network.listener.HttpOnNextListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Observable;

/**
 * desc：http服务-上传请求和回调封装
 * author：haojie
 * date：2017-06-16
 */
public class HttpUploadService<T> extends HttpBaseService<T, T> {

    public HttpUploadService(RxAppCompatActivity rxAppCompatActivity, Observable observable, HttpOnNextListener listener) {
        super(rxAppCompatActivity, observable, listener);
    }

    @Override
    public T call(T t) {
        return t;
    }
}
