package com.app.base.network.interceptor;

import android.content.Context;

import com.app.base.utils.NetWorkUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * desc：缓存拦截器（无效）需要查明无效具体原因
 * author：haojie
 * date：2017-06-06
 */
@Deprecated
public class CacheInterceptor implements Interceptor{

    private static final int MAXAGE = 60*60; // 有网络时 设置缓存超时时间1个小时
    private static final int MAXSTALE = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
    private Context context;
    public CacheInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (NetWorkUtil.isNetConnected(context)) {
            request= request.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK)//有网络时只从网络获取
                    .build();
        }else {
            request= request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
                    .build();
        }
        Response response = chain.proceed(request);

        if (NetWorkUtil.isNetConnected(context)) {
            response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + MAXAGE)
                    .build();
        } else {
            response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + MAXSTALE)
                    .build();
        }
        return response;
    }
}
