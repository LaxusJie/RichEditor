package com.app.base.network.interceptor;

import com.app.base.utils.TokenUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

     @Override
     public Response intercept(Chain chain) throws IOException {
         Request oldRequest = chain.request();
         Response response = null;
         // 新的请求,添加参数
         Request newRequest = addParam(oldRequest);
         response = chain.proceed(newRequest);
         return response;
     }

     /**
      * 添加公共参数
      *
      * @param oldRequest
      * @return
      */
     private Request addParam(Request oldRequest) {
         HttpUrl.Builder builder = oldRequest.url()
                 .newBuilder()
                 .setEncodedQueryParameter("token", TokenUtils.getInstance().getToken());

         Request newRequest = oldRequest.newBuilder()
                 .method(oldRequest.method(), oldRequest.body())
                 .url(builder.build())
                 .build();

         return newRequest;
     }
 }