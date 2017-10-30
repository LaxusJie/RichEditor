package com.app.editor;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * desc：学生社区发布主题页
 * author：ls
 * date：2017/7/4
 */
public class PublishActivity extends BaseCreateActivity {

    @BindView(R.id.tv_name_length)
    TextView tvNameLength;
    @BindView(R.id.et_theme_name)
    EditText etThemeName;


    @Override
    public int getLayoutId() {
        return R.layout.activity_publish_theme;
    }

    @Override
    public void initView() {
        initText();
        super.initView();
    }

    private void initText() {
        tvNameLength.setText("0/40");
    }

    @Override
    protected void initHint() {

    }

    @Override
    protected void setViewEnable(boolean flag) {
        etThemeName.setEnabled(flag);
    }


    @OnClick({R.id.ll_richeditor})
    public void onClick(View view) {
        if (richbar.isNeedDisable()) return;
        switch (view.getId()) {
            case R.id.ll_richeditor:
                richTextEditor.setEditTextFocus();
                richbar.showKeyboard();
                break;
        }
    }

}
