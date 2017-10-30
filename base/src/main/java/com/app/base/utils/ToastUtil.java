package com.app.base.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 类描述：Toast提示工具类
 * 创建人：haojie
 * 创建时间：2017-05-09
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ToastUtil {
    private static Toast toast;

    public static void show(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
