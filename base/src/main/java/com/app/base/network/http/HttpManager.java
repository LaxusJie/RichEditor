package com.app.base.network.http;

import android.content.Context;

import com.apkfuns.logutils.LogUtils;
import com.app.base.R;
import com.app.base.network.exception.RetryWhenNetworkException;
import com.app.base.network.interceptor.TokenInterceptor;
import com.app.base.network.listener.HttpOnNextListener;
import com.app.base.network.subscribers.ProgressSubscriber;
import com.app.base.utils.NetWorkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * desc：网络请求管理类-对retrofit封装
 * author：haojie
 * date：2017-06-06
 */
public class HttpManager {
    /*超时时间-默认6秒*/
    private static final int CONNECTION_TIME = 6;
    private static final int MAXAGE = 60 * 60; // 有网络时 设置缓存超时时间1个小时
    private static final int MAXSTALE = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
    private static final int CACHE_SIZE = 1024 * 1024 * 100;//100mb
    private HttpManager(){}

    private static HttpManager instance;
    private Context context;
    public static HttpManager getInstance() {
        if(instance == null) {
            instance = new HttpManager();
        }
        return instance;
    }

    /**
     * 获取httpclient
     * @return
     */
    private OkHttpClient getHttpClient(Context context) {
        this.context = context;
        File cacheFile = new File(context.getCacheDir(), "responses");
        Cache cache = new Cache(cacheFile, CACHE_SIZE);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(CONNECTION_TIME, TimeUnit.SECONDS)
                .connectTimeout(CONNECTION_TIME, TimeUnit.SECONDS)
                .addInterceptor(getHttpLoggingInterceptor())
                .addInterceptor(NETCONNECT_INTERCEPTOR)
//                .addNetworkInterceptor(NETWORK_INTERCEPTOR)
//                .addInterceptor(OFFLINE_INTERCEPTOR)
                .addInterceptor(new TokenInterceptor())
                .cache(cache)
                .build();
        return okHttpClient;
    }

    /**
     * 应用拦截器处理无网缓存请求头
     */
    private final Interceptor NETCONNECT_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request()
                    .newBuilder();
            if (NetWorkUtil.isNetConnected(context)) {
                return chain.proceed(builder.build());
            } else {
                throw new RuntimeException() {
                    @Override
                    public String getMessage() {
                        return context.getString(R.string.network_nonet);
                    }
                };
            }
        }
    };

    /**
     * 网络拦截器处理缓存响应头
     */
    private final Interceptor NETWORK_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            String cacheControl = originalResponse.header("Cache-Control");
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + MAXSTALE)
                        .build();
            } else {
                return originalResponse;
            }
        }
    };

    /**
     * 应用拦截器处理无网缓存请求头
     */
    private final Interceptor OFFLINE_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetWorkUtil.isNetConnected(context)) {
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
                        .build();
            }
            return chain.proceed(request);
        }
    };

    /**
     * 日志输出
     * @return
     */
    private HttpLoggingInterceptor getHttpLoggingInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtils.d(message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    /**
     * 创建retrofit对象
     * @param baseUrl
     * @return
     */
    public Retrofit createRetrofit(Context context, String baseUrl) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        /*创建retrofit对象*/
        Retrofit retrofit = new Retrofit.Builder()
                .client(getHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return retrofit;
    }

    /**
     * 请求方法
     * @param activity
     * @param observable
     * @param listener
     */
    public void doHttpUpload(RxAppCompatActivity activity, Observable observable, HttpOnNextListener listener) {
        HttpUploadService httpService = new HttpUploadService(activity, observable, listener);
        httpService.setShowProgress(true);
        doHttp(httpService);
    }

    /**
     * 请求方法
     * @param activity
     * @param observable
     * @param listener
     */
    public void doHttpRequest(RxAppCompatActivity activity, Observable observable, HttpOnNextListener listener) {
        HttpService httpService = new HttpService(activity, observable, listener);
        doHttp(httpService);
    }

    /**
     * 请求方法
     * @param activity
     * @param observable
     * @param listener
     * @param flag 显示进度条
     */
    public void doHttpRequest(RxAppCompatActivity activity, Observable observable, HttpOnNextListener listener, boolean flag) {
        HttpService httpService = new HttpService(activity, observable, listener);
        httpService.setShowProgress(flag);
        doHttp(httpService);
    }

    /**
     * 请求方法
     * @param httpService
     */
    public void doHttp(HttpBaseService httpService) {
        /*rx处理*/
        ProgressSubscriber subscriber = new ProgressSubscriber(httpService);
        Observable observable = httpService.getObservable()
                 /*失败后的retry配置*/
                .retryWhen(new RetryWhenNetworkException())
                /*生命周期管理*/
                .compose(httpService.getRxAppCompatActivity().bindUntilEvent(ActivityEvent.DESTROY))
                /*http请求线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/
                .map(httpService);
        /*数据回调*/
        observable.subscribe(subscriber);
    }
}
