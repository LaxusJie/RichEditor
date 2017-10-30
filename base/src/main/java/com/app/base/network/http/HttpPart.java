package com.app.base.network.http;

import android.support.annotation.NonNull;

import com.app.base.network.listener.upload.ProgressRequestBody;
import com.app.base.network.listener.upload.UploadProgressListener;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * desc：http获取part
 * author：haojie
 * date：2017-06-06
 */
public class HttpPart {
    public static final String IMAGETYPE = "image/jpeg";
    public static final String VIDEOTYPE = "video/mp4";

    /**
     * 获取图片part
     * @param filepath 文件路径
     * @param paramName 参数名
     * @return
     */
    @NonNull
    public static MultipartBody.Part getImagePart(String filepath, String paramName) {
        File file=new File(filepath);
        RequestBody requestBody=RequestBody.create(MediaType.parse(IMAGETYPE),file);
        return MultipartBody.Part.createFormData(paramName, file.getName(), requestBody);
    }

    /**
     * 获取图片part，支持进度反馈
     * @param filepath 文件路径
     * @param paramName 参数名
     * @param listener 进度回调
     * @return
     */
    @NonNull
    public static MultipartBody.Part getImagePart(String filepath, String paramName, UploadProgressListener listener) {
        return getPart(IMAGETYPE, filepath, paramName, listener);
    }

    /**
     * 获取视频part，支持进度反馈
     * @param filepath 文件路径
     * @param paramName 参数名
     * @param listener 进度回调
     * @return
     */
    @NonNull
    public static MultipartBody.Part getVideoPart(String filepath, String paramName, UploadProgressListener listener) {
        return getPart(VIDEOTYPE, filepath, paramName, listener);
    }

    /**
     * 通用获取part方法
     * @param mediaType 文件类型
     * @param filepath 文件路径
     * @param paramName 参数名
     * @param listener 进度回调
     * @return
     */
    @NonNull
    private static MultipartBody.Part getPart(String mediaType, String filepath, String paramName, UploadProgressListener listener) {
        File file=new File(filepath);
        RequestBody requestBody=RequestBody.create(MediaType.parse(mediaType),file);
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody, listener);
        return MultipartBody.Part.createFormData(paramName, file.getName(), progressRequestBody);
    }

}
