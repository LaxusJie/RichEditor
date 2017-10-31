package com.app.editor;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.app.editor.base.BaseActivity;
import com.app.emoji.Emoji;
import com.app.emoji.FaceFragment;
import com.app.functionbar.inputview.RichInputBar;
import com.app.richeditor.editor.RichTextEditor;

import butterknife.BindView;

import static com.app.editor.R.id.sv_main3;

public class Main3Activity extends BaseActivity implements FaceFragment.OnEmojiClickListener, RichTextEditor.FunctionBarListener{
    @BindView(R.id.richbar)
    RichInputBar richbar;
    @BindView(R.id.et_main3)
    EditText etmain3;
    @BindView(sv_main3)
    ScrollView svmain3;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.rl_main3)
    RelativeLayout rlMain3;
    @BindView(R.id.richTextEditor)
    RichTextEditor richTextEditor;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main3;
    }

    @Override
    public void initView() {
        richbar.setLayout(rlTitle, rlMain3);
    }


//    @OnClick(R.id.btn_login_login)
//    public void onClick() {
//        if (easybar.getVisibility() == View.VISIBLE) {
//            easybar.setVisibility(View.GONE);
//            richbar.setVisibility(View.VISIBLE);
//        } else {
//            easybar.setVisibility(View.VISIBLE);
//            richbar.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onEmojiDelete() {
        richTextEditor.onEditorEmojiDelete();
    }

    @Override
    public void onEmojiClick(Emoji emoji) {
        richTextEditor.onEditorEmojiClick(emoji);
    }


    @Override
    public void onBackPressed() {
        if (richbar.onBackPress())
            super.onBackPressed();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        richbar.onFocusChange(v, hasFocus);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return richbar.onTouch(view, motionEvent);
    }

}
