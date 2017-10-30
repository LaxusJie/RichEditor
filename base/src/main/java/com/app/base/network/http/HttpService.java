package com.app.base.network.http;

import com.app.base.network.entity.ResultEntity;
import com.app.base.network.exception.HttpTimeException;
import com.app.base.network.listener.HttpOnNextListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Observable;

/**
 * desc：http服务-请求和回调封装
 * author：haojie
 * date：2017-06-06
 */
public class HttpService<T> extends HttpBaseService<ResultEntity<T>, T> {

    public HttpService(RxAppCompatActivity rxAppCompatActivity, Observable observable, HttpOnNextListener listener) {
        super(rxAppCompatActivity, observable, listener);
    }

    @Override
    public T call(ResultEntity<T> httpResult) {
        if (httpResult.getCode() != 200) {
            throw new HttpTimeException(httpResult.getMessage());
        }
        return httpResult.getData();
    }

}
