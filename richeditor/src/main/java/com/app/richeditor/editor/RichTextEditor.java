package com.app.richeditor.editor;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.base.utils.EditorDataEntity;
import com.app.richeditor.R;
import com.app.richeditor.utils.ImageUtil;
import com.tb.emoji.Emoji;
import com.tb.emoji.EmojiUtil;
import com.tb.emoji.FaceFragment;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import mabeijianxi.camera.util.StringUtils;

/**
 * desc：这是一个富文本编辑器，给外部提供insertImage接口，添加的图片跟当前光标所在位置有关
 * author：mgq
 * date：2017-06-06
 */
@SuppressLint({"NewApi", "InflateParams"})
public class RichTextEditor extends LinearLayout implements FaceFragment.OnEmojiClickListener{
    private Context context;
    private static final int EDIT_PADDING = 10; // edittext常规padding是10dp
    private static final int EDIT_FIRST_PADDING_TOP = 10; // 第一个EditText的paddingTop值

    private int viewTagIndex = 1; // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
    public LinearLayout allLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
    private LayoutInflater inflater;
    private OnKeyListener keyListener; // 所有EditText的软键盘监听器
    private OnClickListener btnListener; // 图片右上角红叉按钮监听器
    private OnClickListener playListener;//图片中间播放按钮监听器
    private OnClickListener audioListener;//音频Item点击监听
    private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
    public EditText lastFocusEdit; // 最近被聚焦的EditText
    private LayoutTransition mTransitioner; // 只在图片View添加或remove时，触发transition动画
    private int editNormalPadding = 0; //
    public static final String TYPE_TEXT = "textTag";
    public static final String TYPE_VIDEO = "videoTag";
    public static final String TYPE_AUDIO = "audioTag";
    public static final String TYPE_IMAGE = "imageTag";
    public static final String TYPE_ALINK = "alinkTag";

    public static final boolean STATUS_ENABLE = true;
    public static final boolean STATUS_DISABLE = false;
    private String editHint = "*讨论内容";
    private Handler cursorHandler = new Handler();
    private FunctionBarListener functionBarListener;

    public void setEditHint(String editHint) {
        this.editHint = editHint;
        lastFocusEdit.setHint(editHint);
    }

    public RichTextEditor(Context context) {
        this(context, null);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.setOrientation(LinearLayout.VERTICAL);
    }

    public RichTextEditor(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (context instanceof FunctionBarListener) {
            functionBarListener = (FunctionBarListener) context;
        }
        inflater = LayoutInflater.from(context);
        this.setOrientation(LinearLayout.VERTICAL);

        // 1. 初始化allLayout
        allLayout = new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        allLayout.setBackgroundColor(Color.WHITE);
        setupLayoutTransitions();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        AutoUtils.auto(allLayout);
        addView(allLayout, layoutParams);

        // 2. 初始化键盘退格监听
        // 主要用来处理点击回删按钮时，view的一些列合并操作
        keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    EditText edit = (EditText) v;
                    onBackspacePress(edit);
                }
                return false;
            }
        };

        // 3. 图片叉掉处理
        btnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout parentView = (RelativeLayout) v.getParent();
                onImageCloseClick(parentView);
            }
        };

        focusListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (functionBarListener != null) {
                    if (hasFocus) {
                        lastFocusEdit = (EditText) v;
                    }
                    functionBarListener.onFocusChange(v, hasFocus);
                }
            }
        };

        LayoutParams firstEditParam = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        editNormalPadding = dip2px(EDIT_PADDING);
        EditText firstEdit = createEditText(editHint);
        firstEditParam.setMargins(0, 15, 0, 15);
        LinearLayout llEditText = new LinearLayout(context);
        firstEdit.setLayoutParams(firstEditParam);
        llEditText.addView(firstEdit);
        allLayout.addView(llEditText);
        lastFocusEdit = firstEdit;
    }

    /*功能栏监听*/
    public interface FunctionBarListener {
        void onFocusChange(View view, boolean hasFocus);

        boolean onTouch(View view, MotionEvent motionEvent);
    }

    /**
     * 处理软键盘backSpace回退事件
     *
     * @param editTxt 光标所在的文本输入框
     */
    private void onBackspacePress(EditText editTxt) {
        int startSelection = editTxt.getSelectionStart();
        // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
        if (startSelection == 0) {
            int editIndex = allLayout.indexOfChild((View) editTxt.getParent());
            View preView = allLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
            // 则返回的是null
            if (null != preView) {
                if (preView instanceof RelativeLayout || preView instanceof FrameLayout) {
                    // 光标EditText的上一个view对应的是图片
                    onImageCloseClick(preView);
                } else if (preView instanceof LinearLayout) {
                    // 光标EditText的上一个view对应的还是文本框EditText
                    String str1 = editTxt.getText().toString();
                    EditText preEdit = (EditText) preView.findViewById(R.id.edit_text);
                    String str2 = preEdit.getText().toString();

                    // 合并文本view时，不需要transition动画
                    allLayout.setLayoutTransition(null);
                    allLayout.removeView((View) editTxt.getParent());
                    allLayout.setLayoutTransition(mTransitioner); // 恢复transition动画

                    // 文本合并
                    preEdit.setText(context.getString(R.string.two_text, str2, str1));
                    preEdit.requestFocus();
                    preEdit.setCursorVisible(true);
                    preEdit.setSelection(str2.length(), str2.length());
                    lastFocusEdit = preEdit;
                }
            }
        }
    }

    /**
     * 处理图片叉掉的点击事件
     *
     * @param view 整个image对应的relativeLayout view
     */
    private void onImageCloseClick(View view) {
        if (!mTransitioner.isRunning()) {
            allLayout.removeView(view);
        }
    }

    /**
     * 生成文本输入框
     */
    private EditText createEditText(String hint) {
        final EditText editText = (EditText) inflater.inflate(R.layout.edit_edittext_item,
                null);
        editText.setOnKeyListener(keyListener);
        editText.setTag(viewTagIndex++);
        editText.setPadding(editNormalPadding, dip2px(EDIT_FIRST_PADDING_TOP), editNormalPadding, 0);
        editText.setHint(hint);
        editText.setTextSize(15);
        editText.setOnFocusChangeListener(focusListener);
        editText.setFocusable(true);
        editText.setGravity(Gravity.TOP);
        editText.setAutoLinkMask(Linkify.WEB_URLS);
        editText.setLinksClickable(true);
        editText.setMovementMethod(LinkMovementMethod.getInstance());
        editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean touchFlag = false;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (!functionBarListener.onTouch(view, motionEvent)) return false;
                    EditText etContent = (EditText) view;
                    setEditTextFocus();
                    if (TextUtils.isEmpty(etContent.getText())) {
                        etContent.setSelection(0);
                        touchFlag = true;
                    }
                }
                return touchFlag;
            }
        });
        return editText;
    }

    /**
     * 生成音频布局
     */
    private FrameLayout createAudioLayout(EditorDataEntity editorDataEntity) {
        FrameLayout layoutAudio = (FrameLayout) inflater.inflate(R.layout.edit_audioview, null);
        TextView tvAudioDuration = (TextView) layoutAudio.findViewById(R.id.tv_audio_duration);
        tvAudioDuration.setText(editorDataEntity.getMediaDuration());
        layoutAudio.setTag(R.id.richeditor_audio, editorDataEntity);
        layoutAudio.setOnClickListener(audioListener);


        View closeView = layoutAudio.findViewById(R.id.image_close);
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageCloseClick((FrameLayout) v.getParent().getParent());
            }
        });
        return layoutAudio;
    }


    /**
     * 生成图片布局
     */
    private RelativeLayout createImageLayout(EditorDataEntity editorDataEntity) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.edit_imageview, null);
        View playView;
        TextView tvDuration;

        if (editorDataEntity.getType().equals(TYPE_VIDEO)) {
            playView = layout.findViewById(R.id.image_play);
            tvDuration = (TextView) layout.findViewById(R.id.video_duration);
            playView.setVisibility(View.VISIBLE);
            tvDuration.setVisibility(View.VISIBLE);
            tvDuration.setText(editorDataEntity.getMediaDuration());
            playView.setOnClickListener(playListener);
            playView.setTag(editorDataEntity);
        }

        View closeView = layout.findViewById(R.id.image_close);
        layout.setTag(R.id.richeditor_video, editorDataEntity);
        closeView.setOnClickListener(btnListener);
        return layout;
    }

    /**
     * 添加图片
     */
    public void insertImage(EditorDataEntity editorDataEntity) {
        Bitmap bmp = ImageUtil.getScaledBitmap(editorDataEntity.getPoster(), getWidth());
        editorDataEntity.setBitmapResource(bmp);
        insertDataInfo(editorDataEntity);
    }

    /**
     * 添加音频
     */
    public void insertAudio(EditorDataEntity editorDataEntity) {
        insertDataInfo(editorDataEntity);
    }

    /**
     * 插入数据信息
     */
    public void insertDataInfo(EditorDataEntity editorDataEntity) {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        String editStr1 = cursorIndex==-1?lastEditStr:lastEditStr.substring(0, cursorIndex).trim();
        int lastEditIndex = allLayout.indexOfChild((View) lastFocusEdit.getParent());

        if (lastEditStr.length() == 0 || editStr1.length() == 0) {
            // 如果EditText为空，或者光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
            if (editorDataEntity.getType().equals(TYPE_AUDIO)) {
                addAudioViewAtIndex(lastEditIndex, editorDataEntity);
            } else {
                addImageViewAtIndex(lastEditIndex, editorDataEntity);
            }

        } else {
            // 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
            lastFocusEdit.setText(editStr1);
            String editStr2 = cursorIndex==-1?"":lastEditStr.substring(cursorIndex).trim();
            if (allLayout.getChildCount() - 1 == lastEditIndex
                    || editStr2.length() > 0) {
                addEditTextAtIndex(lastEditIndex + 1, editStr2, editorDataEntity.getType());
            }
            if (editorDataEntity.getType().equals(TYPE_AUDIO)) {
                addAudioViewAtIndex(lastEditIndex + 1, editorDataEntity);
            } else {
                addImageViewAtIndex(lastEditIndex + 1, editorDataEntity);
            }
        }
        hideKeyBoard();
    }

    /**
     * 隐藏小键盘
     */
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
    }

    /**
     * 在特定位置插入EditText
     *
     * @param index   位置
     * @param editStr EditText显示的文字
     */
    public void addEditTextAtIndex(final int index, String editStr, String tag) {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout llEditText = new LinearLayout(context);
        final EditText etText = createEditText("");
        etText.setText(editStr == null ? "" : editStr);
        etText.setTag(tag);
        layoutParams.setMargins(0, 0, 26, 30);
        etText.setLayoutParams(layoutParams);
        aLinkClick(etText);
        llEditText.addView(etText);


        // 请注意此处，EditText添加、或删除不触动Transition动画
        allLayout.setLayoutTransition(null);
        allLayout.addView(llEditText, index);
        allLayout.setLayoutTransition(mTransitioner); // remove之后恢复transition动画
    }

    /*超链接点击*/
    public void aLinkClick(EditText etText) {
        if (etText.getText() == null) {
            return;
        }
        Spannable sp = etText.getText();
        URLSpan[] urls = sp.getSpans(0, etText.getText().length(), URLSpan.class);
        SpannableStringBuilder style = new SpannableStringBuilder(etText.getText());
        style.clearSpans();
        for (URLSpan url : urls) {
            ClickURLSpan clickURLSpan = new ClickURLSpan(url.getURL());
            style.setSpan(clickURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        etText.setText(style);
    }

    /*点击超链接处理*/
    private class ClickURLSpan extends ClickableSpan {
        private String url;

        ClickURLSpan(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri contentUrl = Uri.parse(url);
            intent.setData(contentUrl);
            context.startActivity(intent);
        }
    }

    /**
     * 在指定位置添加Audio
     */
    public void addAudioViewAtIndex(final int index, EditorDataEntity editorDataEntity) {
        final FrameLayout layoutAudio = createAudioLayout(editorDataEntity);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(26, 15, 26, 15);
        layoutAudio.setLayoutParams(layoutParams);
        allLayout.addView(layoutAudio, index);
        showEmojiIcon();
    }

    /**
     * 在特定位置添加ImageView
     */
    public void addImageViewAtIndex(final int index, final EditorDataEntity editorDataEntity) {
        final RelativeLayout imageLayout = createImageLayout(editorDataEntity);
        DataImageView imageView = (DataImageView) imageLayout
                .findViewById(R.id.edit_imageView);
        imageView.setImageBitmap(editorDataEntity.getBitmapResource());
        imageView.setBitmap(editorDataEntity.getBitmapResource());
        imageView.setAbsolutePath(editorDataEntity.getPoster());
        // 调整imageView的高度
        int imageHeight = getWidth() * editorDataEntity.getBitmapResource().getHeight() / editorDataEntity.getBitmapResource().getWidth();
        RelativeLayout.LayoutParams ivParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, imageHeight);
        imageView.setLayoutParams(ivParams);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(26, 15, 26, 15);
        imageLayout.setLayoutParams(layoutParams);
        allLayout.addView(imageLayout, index);
        showEmojiIcon();
    }


    /**
     * 初始化transition动画
     */
    private void setupLayoutTransitions() {
        mTransitioner = new LayoutTransition();
        allLayout.setLayoutTransition(mTransitioner);
        mTransitioner.addTransitionListener(new TransitionListener() {

            @Override
            public void startTransition(LayoutTransition transition,
                                        ViewGroup container, View view, int transitionType) {
            }

            @Override
            public void endTransition(LayoutTransition transition,
                                      ViewGroup container, View view, int transitionType) {
            }
        });
        mTransitioner.setDuration(300);
    }

    /**
     * dp和pixel转换
     *
     * @param dipValue dp值
     * @return 像素值
     */
    public int dip2px(float dipValue) {
        float m = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    /**
     * 构建编辑器数据(对外提供的接口, 生成编辑数据上传)
     */
    public List<EditorDataEntity> buildEditData() {
        List<EditorDataEntity> dataList = new ArrayList<>();
        int num = allLayout.getChildCount();
        for (int index = 0; index < num; index++) {
            View itemView = allLayout.getChildAt(index);
            EditorDataEntity itemData = null;
            if (itemView instanceof LinearLayout) {
                itemData = new EditorDataEntity();
                EditText item = (EditText) itemView.findViewById(R.id.edit_text);
                if (StringUtils.isEmpty(item.getText().toString().trim())) {
                    continue;
                }
                itemData.setText(item.getText().toString());
                itemData.setType(RichTextEditor.TYPE_TEXT);
            } else if (itemView instanceof RelativeLayout) {
                itemData = (EditorDataEntity) itemView.getTag(R.id.richeditor_video);
            } else if (itemView instanceof FrameLayout) {
                itemData = (EditorDataEntity) itemView.getTag(R.id.richeditor_audio);
            }
            dataList.add(itemData);
        }
        return dataList;
    }

    /**
     * 设置编辑器的状态
     *
     * @param status :STATUS_ENABLE 可编辑  STATUS_DISABLE不可编辑
     */
    public void setRichTextEditorStatus(boolean status) {
        if (status) {
            return;
        }
        int num = allLayout.getChildCount();
        for (int i = 0; i < num; i++) {
            View itemView = allLayout.getChildAt(i);
            if (itemView instanceof EditText) {
                EditText etContent = (EditText) itemView;
                if (String.valueOf(etContent.getTag()).equals(RichTextEditor.TYPE_ALINK)) {
                    continue;
                }
                etContent.setEnabled(false);
                if (i == num - 1) {
                    etContent.setHint("");
                }
            } else if (itemView instanceof RelativeLayout || itemView instanceof FrameLayout) {
                ImageView ivClose = (ImageView) itemView.findViewById(R.id.image_close);
                ivClose.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置编辑器背景色
     */
    public void setEditorBackground(int color) {
        int num = allLayout.getChildCount();
        for (int i = 0; i < num; i++) {
            View view = allLayout.getChildAt(i);
            if (view instanceof FrameLayout) {
                continue;
            }
            view.setBackgroundColor(ContextCompat.getColor(context, color));
        }
        allLayout.setBackgroundColor(ContextCompat.getColor(context, color));
    }

    /*清除EditText的hint*/
    public void clearHint() {
        int num = allLayout.getChildCount();
        for (int i = 0; i < num; i++) {
            if (allLayout.getChildAt(i) instanceof LinearLayout) {
                final LinearLayout llEditText = (LinearLayout) allLayout.getChildAt(i);
                final EditText editText = (EditText) llEditText.findViewById(R.id.edit_text);
                editText.setHint("");
                if (i == num - 1) {
                    cursorHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            editText.setCursorVisible(true);
                            editText.requestFocus();
                            editText.setText("   ");
                            editText.setSelection(0, 1);
                            editText.setHighlightColor(ContextCompat.getColor(context, R.color.colorWhite1));
                        }
                    }, 1000);
                }
            }
        }
    }

    /*显示表情图标*/
    public void showEmojiIcon() {
        int num = allLayout.getChildCount();
        for (int i = 0; i < num; i++) {
            if (allLayout.getChildAt(i) instanceof LinearLayout) {
                EditText edContent = (EditText) allLayout.getChildAt(i).findViewById(R.id.edit_text);
                EmojiUtil.displayTextAction(edContent, context);
            }
        }
    }

    /**
     * 设置当前输入框聚焦
     * @return 当前输入框
     */
    public EditText setEditTextFocus() {
        int index = lastFocusEdit.getSelectionStart();
        if (!lastFocusEdit.hasFocus()) {
            lastFocusEdit.requestFocus();
        }
        lastFocusEdit.setSelection(index < 0 ? lastFocusEdit.length() : index);
        return lastFocusEdit;
    }

    /**
     * 删除表情方法
     */
    public void onEditorEmojiDelete() {
        EmojiUtil.deleteEmojiAction(lastFocusEdit, context);
        EmojiUtil.displayTextAction(lastFocusEdit, context);
    }

    /**
     * 点击表情方法
     * @param emoji 点击到的表情
     */
    public void onEditorEmojiClick(Emoji emoji) {
        EmojiUtil.clickEmojiAction(emoji, lastFocusEdit, context);
    }


    @Override
    public void onEmojiDelete() {
        onEditorEmojiDelete();
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        onEditorEmojiClick(emoji);
    }

}
