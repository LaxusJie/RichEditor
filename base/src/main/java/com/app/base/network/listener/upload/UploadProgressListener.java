package com.app.base.network.listener.upload;

/**
 * 类描述：上传进度回调类
 * 创建人：haojie
 * 创建时间：2017-05-10
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface UploadProgressListener {
    /**
     * 上传进度
     * @param currentBytesCount
     * @param totalBytesCount
     */
    void onProgress(long currentBytesCount, long totalBytesCount);
}