package mabeijianxi.camera.util;

import android.app.Activity;
import android.content.Intent;

import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.model.MediaRecorderConfig;

import static mabeijianxi.camera.MediaRecorderActivity.MEDIA_RECORDER_CONFIG_KEY;
import static mabeijianxi.camera.MediaRecorderActivity.OVER_ACTIVITY_NAME;

/**
 * 类描述：获取录制视频回调
 * 创建人：ZZH
 * 创建时间：2017/6/15
 */
public class MediaConfig {

    public static MediaConfig sInstance;

    public static MediaConfig getInstance() {
        if (sInstance == null) {
            synchronized (MediaConfig.class) {
                if (sInstance == null) {
                    sInstance = new MediaConfig();
                }
            }
        }
        return sInstance;
    }

    public MediaConfig() {

    }

    public static OnSelectResultCallback resultCallback;

    public static OnSelectResultCallback getResultCallback() {
        return resultCallback;
    }


    /**
     * @param context
     * @param overGOActivityName 录制结束后需要跳转的Activity全类名
     */
    public static void goSmallVideoRecorder(Activity context, String overGOActivityName, MediaRecorderConfig mediaRecorderConfig,OnSelectResultCallback resultCall) {
        resultCallback = resultCall;
        context.startActivity(new Intent(context, MediaRecorderActivity.class).putExtra(OVER_ACTIVITY_NAME, overGOActivityName).putExtra(MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig));
    }

    /*
    * 录制完毕回调
    * */
    public interface OnSelectResultCallback {
        void onSelectSuccess(int duration,String path,String thumbnailPath);
    }
}
