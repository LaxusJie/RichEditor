package com.app.richeditor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;

/**
 * desc：图片工具类
 * author：mgq
 * date：2017-09-12
 */

public class ImageUtil {
    /**
     * 根据view的宽度，动态缩放bitmap尺寸
     *
     * @param width view的宽度
     */
    public static Bitmap getScaledBitmap(String filePath, int width) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int sampleSize = options.outWidth > width ? options.outWidth / width
                    + 1 : 1;
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            return BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 显示缩放后的bitmap图片
     * @param bitmap
     * @param imageView
     * @param context
     */
    public static void showScaledBitmap(Bitmap bitmap, ImageView imageView,String path, Context context) {
        try {
            int bWidth = bitmap.getWidth();
            int bHeight = bitmap.getHeight();
            int width = UIUtils.getScreenWidth(context) - 86;
            int height = width * bHeight / bWidth;
            setImageViewParam(imageView,width,height);
            Glide.with(context).load(path).into(imageView);
            LogUtils.d("width:--"+width+"|height:"+height);
            LogUtils.d("bwidth:--"+bWidth+"|bheight:"+bHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setImageViewParam(ImageView imageView,int width,int height){
        ViewGroup.LayoutParams para = imageView.getLayoutParams();
        para.width = width;
        para.height = height;
        imageView.setLayoutParams(para);
    }
}
