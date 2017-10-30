package com.app.functionbar.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import mabeijianxi.camera.VCamera;
import mabeijianxi.camera.model.AutoVBRMode;
import mabeijianxi.camera.model.BaseMediaBitrateConfig;
import mabeijianxi.camera.model.MediaRecorderConfig;
import mabeijianxi.camera.util.DeviceUtils;

/**
 * 类描述：请输入该类的描述
 * 创建人：ZZH
 * 创建时间：2017/6/6
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class VideoOptions {

    public static void initSmallVideo(Context context) {
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/taolun/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/taolun/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/taolun/");
        }
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(context);
    }

    public static MediaRecorderConfig getVideoConfig(){

        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                //设置为录制完不压缩
//                .doH264Compress(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST)//设置转码速度
//                )
                .setMediaBitrateConfig(new AutoVBRMode()
                        .setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST)//设置码率
                )
                .smallVideoWidth(720)//设置宽度
                .smallVideoHeight(720)//设置高度
                .recordTimeMax(60 * 1000)//最大录制时间
                .maxFrameRate(15)//帧率
                .captureThumbnailsTime(1)//取缩略图时间点
                .recordTimeMin((int) (1.5 * 1000))//最小录制时间
                .build();
        return config;
    }
}
