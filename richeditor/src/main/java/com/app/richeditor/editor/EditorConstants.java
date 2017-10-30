package com.app.richeditor.editor;

/**
 * desc：编辑器相关的常量值
 * author：mgq
 * date：2017-06-06
 */

public class EditorConstants {
    /*定义匹配script的正则表达式*/
    static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    /*定义匹配style的正则表达式*/
    static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    /*定义匹配HTML标签的正则表达式*/
    static final String REGEX_HTML = "</?(?!img|video|audio|a|h\\\\d)[^>]+>";
    /*匹配图片标签*/
    static final String REGEX_IMG = "img";
    /*匹配音频标签*/
    static final String REGEX_AUDIO = "audio";
    /*匹配视频*/
    static final String REGEX_VEDIO = "video";
    /*匹配资源和附件*/
    static final String REGEX_ATTACHMENT = "a";
    /*src属性*/
    static final String ATTR_SRC = "src";
    /*href属性*/
    static final String ATTR_HREF = "href";
    /*name属性*/
    static final String ATTR_NAME = "name";
    /*aliasname属性*/
    static final String ATTR_ALIASNAME = "aliasname";
    /*path属性*/
    static final String ATTR_PATH = "path";
    /*poster属性*/
    static final String ATTR_POST = "poster";
    /*消息模块中的匹配带有title属性的span标签*/
    public static final String REGEX_SPAN = "<span*?\\stitle=['\"]?([\\s\\S]*?)['\"]>";
    /*过滤所有以<开头以>结尾的标签*/
    public static final String REGEX_HTML_ALL = "<([^>]*)>";
    /*拆分字符串标签*/
    static final String SPLITTAG = "splitTag";
}
