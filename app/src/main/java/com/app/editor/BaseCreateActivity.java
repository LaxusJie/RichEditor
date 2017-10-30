package com.app.editor;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.app.base.network.entity.EditorDataEntity;
import com.app.editor.base.BaseActivity;
import com.app.functionbar.inputview.AudioFunc;
import com.app.functionbar.inputview.BaseInputBar;
import com.app.functionbar.inputview.RichInputBar;
import com.app.functionbar.inputview.VideoFunc;
import com.app.richeditor.editor.RichTextEditor;
import com.app.richeditor.editor.RichTextEditorManager;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.List;

import butterknife.BindView;

/**
 * desc：创建页父类
 * author：haojie
 * date：2017-09-18
 */
public abstract class BaseCreateActivity extends BaseActivity implements RichTextEditor.FunctionBarListener{
    @BindView(R.id.layout_top)
    RelativeLayout layoutTop;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.sv_rich)
    ScrollView svRich;
    @BindView(R.id.richbar)
    public RichInputBar richbar;
    @BindView(R.id.richtexteditor)
    public RichTextEditor richTextEditor;

    public RichTextEditorManager editorManager;
    private Handler uploadHandler;
    private Handler picHandler = new Handler();

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initView() {
        initEditorManager();
        initHint();
        initRichBar();
        initBarListener();
//        MediaUploadUtil.getUploadUrl(this);
    }

    private void initEditorManager() {
        editorManager = new RichTextEditorManager(richTextEditor, activity);
    }

    protected abstract void initHint();

    private void initRichBar() {
        richbar.setLayout(layoutTop, rlContent);
        richbar.setFocusView(richTextEditor.lastFocusEdit);
    }

    /*设置监听器*/
    public void initBarListener() {
        richbar.setEmojiListener(richTextEditor);
        richbar.setAudioCountListener(new AudioFunc.AudioCountListener() {
            @Override
            public int getAudioCount() {
                return editorManager.getMediaCount(RichTextEditor.TYPE_AUDIO);
            }
        });
        richbar.setVideoCountListener(new VideoFunc.VideoCountListener() {
            @Override
            public int getVideoCount() {
                return editorManager.getMediaCount(RichTextEditor.TYPE_VIDEO);
            }
        });
        richbar.setRecordListener(new AudioFunc.RecordListener() {
            @Override
            public void onFinishRecord(EditorDataEntity editorDataEntity) {
                uploadHandler = new Handler();
                editorManager.insertAudio(editorDataEntity);
                editorManager.clearHint();
                svRich.fullScroll(ScrollView.FOCUS_DOWN);
//                startUpload(editorDataEntity, MediaUploadUtil.MEDIA_TYPE_AUDIO);
            }
        });
        richbar.setPictureListener(new BaseInputBar.PictureListener() {
            @Override
            public void onFinishPicture(List<LocalMedia> list) {
                for (int i = 0; i < list.size(); i++) {
                    uploadHandler = new Handler();
                    final EditorDataEntity editorDataEntity = new EditorDataEntity();
                    editorDataEntity.setUrl(list.get(i).getPath());
                    editorDataEntity.setPoster(list.get(i).getPath());
                    editorDataEntity.setType(RichTextEditor.TYPE_IMAGE);
                    editorManager.insertBitmap(editorDataEntity);
//                    startUpload(editorDataEntity, MediaUploadUtil.MEDIA_TYPE_IMAGE);
                }
                editorManager.clearHint();
                picHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        svRich.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 1000);
            }
        });
        richbar.setVideoListener(new VideoFunc.VideoListener() {
            @Override
            public void onFinishVideo(EditorDataEntity editorDataEntity) {
                uploadHandler = new Handler();
                editorManager.insertBitmap(editorDataEntity);
                editorManager.clearHint();
                svRich.fullScroll(ScrollView.FOCUS_DOWN);
//                startUpload(editorDataEntity, MediaUploadUtil.MEDIA_TYPE_VIDEO);
            }
        });
    }

    /*开始上传*/
    private void startUpload(final EditorDataEntity editorDataEntity, final int meidiaType) {
        uploadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                MediaUploadUtil.uploadMediaFile(editorDataEntity, meidiaType, activity, null);
            }
        }, 1000);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        richbar.onFocusChange(view, hasFocus);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return richbar.onTouch(view, motionEvent);
    }

    @Override
    public void onBackPressed() {
        if (!richbar.isNeedDisable() && richbar.onBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (richbar.isNeedDisable()) {
            richTextEditor.lastFocusEdit.setEnabled(false);
            setViewEnable(false);
        } else {
            richTextEditor.lastFocusEdit.setEnabled(true);
            setViewEnable(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    protected abstract void setViewEnable(boolean flag);
}
