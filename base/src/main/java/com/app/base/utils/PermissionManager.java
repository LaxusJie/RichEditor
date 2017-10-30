package com.app.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * 类描述：权限管理类
 * 创建人：haojie
 * 创建时间：2016-12-28
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class PermissionManager {
    private static final int REQUEST_PERMISSION = 127;
    private PermissionManager(){}

    private static PermissionManager instance;

    public static PermissionManager getInstance() {
        if(instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

    /**
     * 项目启动获取定位、sd卡读取、录音权限
     */
    public void getStartPermission(Activity context) {
        getPermission(context, new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION,READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE,RECORD_AUDIO});
    }

    public boolean getCallPermission(Activity context) {
        return getPermission(context, Manifest.permission.CALL_PHONE);
    }

    public boolean getCameraPermission(Activity context) {
        return getPermission(context, Manifest.permission.CAMERA);
    }

    public void getMediaPermission(Activity context) {
        getPermission(context, new String[]{WRITE_EXTERNAL_STORAGE,RECORD_AUDIO,READ_EXTERNAL_STORAGE});
    }

    private boolean getPermission(Activity context, String str) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                context.requestPermissions(new String[]{str}, REQUEST_PERMISSION);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private void getPermission(Activity context, String[] strs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            for (int i = 0; i < strs.length; i++) {
                if (context.checkSelfPermission(strs[i]) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(strs[i]);
                }
            }
            if (permissions.size() > 0) {
                context.requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSION);
            }
        }
    }

}
