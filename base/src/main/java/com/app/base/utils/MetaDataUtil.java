package com.app.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by ${ls} on 2016/2/22.
 * 获取meta-data数据
 */
public class MetaDataUtil {

    public static String getMetaData(Context ctx, String key) {
        String resultData = "";
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                        if(resultData == null) {
                            resultData = String.valueOf(applicationInfo.metaData.getInt(key));
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }
}
