package com.app.richeditor.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * desc：正则表达式相关工具类
 * author：mgq
 * date：2017-08-24
 */

public class RegexUtils {
    /**
     * 过滤Html标签
     * @param reg 正则表达式
     * @param htmlStr 要匹配的文本
     * @param replaceTag 替换的内容
     */
    public static String getFilterString(String reg, String htmlStr,String replaceTag) {
        Matcher matcher = getMatcher(reg,htmlStr);
        htmlStr = matcher.replaceAll(replaceTag);
        return htmlStr;
    }


    /*
     * 获取正则匹配对象
     */
    public static Matcher getMatcher(String reg, String htmlStr){
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlStr);
        return matcher;
    }

    /**
     * 拆分标签匹配的单个属性
     * @param reg 匹配的正则
     * @param htmlStr 要匹配的内容
     */
    public static List<String> splitHtmlProperty(String reg, String htmlStr) {
        List<String> splitArray = new ArrayList();
        Matcher matcher = getMatcher(reg,htmlStr);
        while (matcher.find()) {
            splitArray.add(matcher.group(1));
        }
        return splitArray;
    }

    /*获取要匹配的正则表达式*/
    public static String getRegExpression(String element, String attr) {
        String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?(\\s.*?)?>";
        return reg;
    }

}
