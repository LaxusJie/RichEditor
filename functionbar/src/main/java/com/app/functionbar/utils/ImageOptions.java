package com.app.functionbar.utils;

import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;

/**
 * 类描述：图片设置
 * 创建人：haojie
 * 创建时间：2017-05-25
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ImageOptions {

    //http前缀
    private static final String PREFIX_HTTP = "http";
    //gif后缀
    private static final String SUFFIX_GIF = ".gif";
    //图片限制符
    private static final String IMAGE_LIMIT = "?maxw=";
    //图片列表大小
    private static final String IMAGE_LIST_SIZE = "400";
    //图片内容大小
    private static final String IMAGE_CONTENT_SIZE = "600";

    public static FunctionOptions getImageOption() {
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_IMAGE) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                .setCompress(true) //是否压缩
                .setEnablePixelCompress(true) //是否启用像素压缩
                .setEnableQualityCompress(true) //是否启质量压缩
                .setMaxSelectNum(9) // 可选择图片的数量
                .setMinSelectNum(0)// 图片或视频最低选择数量，默认代表无限制
                .setSelectMode(FunctionConfig.MODE_MULTIPLE) // 单选 or 多选
                .setShowCamera(true) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
                .setEnablePreview(true) // 是否打开预览选项
                .setEnableCrop(false) // 是否打开剪切选项
                .setCircularCut(false)// 是否采用圆形裁剪
                .setMaxB(202400) // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb
                .setGrade(Luban.THIRD_GEAR) // 压缩档次 默认三档
                .setImageSpanCount(4) // 每行个数
                .setCompressFlag(1) // 1 系统自带压缩 2 luban压缩
                .create();
        return options;
    }

    public static FunctionOptions getVideoOption() {
        FunctionOptions options = new FunctionOptions.Builder()
                .setType(FunctionConfig.TYPE_VIDEO) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                .setMaxSelectNum(1) // 可选择图片的数量
                .setMinSelectNum(0)// 图片或视频最低选择数量，默认代表无限制
                .setSelectMode(FunctionConfig.MODE_SINGLE) // 单选 or 多选
                .setShowCamera(false) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
                .setEnablePreview(true) // 是否打开预览选项
                .setPreviewVideo(true) // 是否预览视频(播放) mode or 多选有效
                .setRecordVideoDefinition(FunctionConfig.HIGH) // 视频清晰度
                .setRecordVideoSecond(60) // 视频秒数
                .setCustomQQ_theme(0)// 可自定义QQ数字风格，不传就默认是蓝色风格
                .setGif(false)// 是否显示gif图片，默认不显示
                .setImageSpanCount(4) // 每行个数
                .setVideoS(120)// 查询多少秒内的视频 单位:秒
                .setNumComplete(false) // 0/9 完成  样式
                .create();
        return options;
    }

}
