package com.app.base.utils;

import android.text.Html;
import android.text.TextUtils;
import android.widget.EditText;

import com.apkfuns.logutils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * desc：文字工具类
 * author：haojie
 * date：2017-07-04
 */
public class TextUtil {
    public static final String PATTERN_HOUR_DATE = "HH:mm";
    public static final String PATTERN_MONTH_DATE = "MM-dd HH:mm";
    public static final String PATTERN_YEAR_DATE = "yyyy-MM-dd HH:mm";

    /**
     * 验证手机格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return mobiles.matches(telRegex);
    }


    /**
     * 文本框非空验证
     *
     * @param eText
     * @return
     */
    public static boolean editTextJudgeEmpty(EditText... eText) {
        for (int i = 0; i < eText.length; i++) {
            if (TextUtils.isEmpty(eText[i].getText().toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证邮箱格式
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        String mailRegex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";//
        return email.matches(mailRegex);
    }

    /**
     * 验证字符串是否为空
     *
     * @param args
     * @return
     */
    public static boolean isEmpty(String... args) {
        if (args != null) {
            for (String str : args) {
                if (str == null || str.equals("")) {
                    return true;
                }
            }
        } else
            return true;
        return false;
    }

    /**
     * 过滤字符串中的HTML
     *
     * @param string
     */
    public static String deleteHtmlString(String string) {
        return Html.fromHtml(string).toString().trim();
    }

    /**
     * 判断一个字符串是否都为数字
     *
     * @param strNum
     * @return
     */
    public static boolean isDigit(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }

    /**
     * 截取数字
     *
     * @param content
     * @return
     */
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 截取非数字
     *
     * @param content
     * @return
     */
    public static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 判断一个字符串是否含有数字
     *
     * @param content
     * @return
     */
    public static boolean HasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 转换时间
     *
     * @param time
     * @return
     */
    public static Date transTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        try {
            return sdf.parse(time);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据当前时间判断传递的时间间隔
     *
     * @param createDate
     * @return
     */
    public static String getTimeText(String createDate) {
        String r = "";
        if (TextUtil.isEmpty(createDate)) return r;
        long createTime = Long.parseLong(createDate);
        if (isThisYear(createTime)) {//本年
            if(isToday(createTime)){
                r = getFormatString(createDate, PATTERN_HOUR_DATE);
            }else{
                r = getFormatString(createDate, PATTERN_MONTH_DATE);
            }
        } else {//本年以外的
            r = getFormatString(createDate, PATTERN_YEAR_DATE);
        }
        LogUtils.d("时间：" + getFormatString(createDate, PATTERN_YEAR_DATE));
        return r;
    }

    //判断选择的日期是否是今天
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    //判断选择的日期是否是本年
    public static boolean isThisYear(long time) {
        return isThisTime(time, "yyyy");
    }

    private static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    private static String getFormatString(String createDate, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String r = format.format(Long.parseLong(createDate));
        return r;
    }

    /**
     * 去掉首行和末尾的换行符
     *
     * @param text
     * @return
     */
    public static String removeSomeChar(String text, String ch) {
        text = removeBeginChar(text, ch);
        text = removeEndChar(text, ch);
        return text;
    }

    /**
     * 去掉字符串开始的字符
     *
     * @param bText
     * @param ch
     * @return
     */
    public static String removeBeginChar(String bText, String ch) {
        if (bText.startsWith(ch)) {
            bText = bText.substring(1, bText.length());
            bText = removeBeginChar(bText, ch);
        }
        return bText;
    }

    /**
     * 去掉字符串结尾的字符
     *
     * @param eText
     * @param ch
     * @return
     */
    public static String removeEndChar(String eText, String ch) {
        if (eText.endsWith(ch)) {
            eText = eText.substring(0, eText.length() - 1);
            eText = removeEndChar(eText, ch);
        }
        return eText;
    }

    /**
     * 验证字符串是否是一个合法的日期格式
     */
    public static boolean isValidDate(String date, String template) {
        boolean convertSuccess = true;
        // 指定日期格式
        SimpleDateFormat format = new SimpleDateFormat(template, Locale.CHINA);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2015/02/29会被接受，并转换成2015/03/01
            format.setLenient(false);
            format.parse(date);
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }
}
