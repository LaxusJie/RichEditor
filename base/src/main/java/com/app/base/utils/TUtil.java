package com.app.base.utils;

import com.apkfuns.logutils.LogUtils;

import java.lang.reflect.ParameterizedType;

/**
 * Created by HJ on 2016/10/10.
 * 泛型对象获取类
 */
public class TUtil {
    public static <T> T getT(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            LogUtils.w(o.getClass().toString() + "没有实现MVP模式");
        }
        return null;
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
