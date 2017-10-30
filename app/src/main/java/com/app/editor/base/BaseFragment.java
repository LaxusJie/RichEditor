package com.app.editor.base;


import android.os.Bundle;

import com.app.base.FrameBaseFragment;
import com.app.base.utils.TUtil;

/**
 * desc：基类Fragment
 * author：haojie
 * date：2016/12/12
 */
public abstract class BaseFragment<P extends BasePresenter, M extends BaseModel> extends FrameBaseFragment {
    public BaseActivity activity;
    public BaseFragment fragment;
    public P presenter;
    public M model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
        fragment = this;
        presenter = TUtil.getT(this, 0);
        model = TUtil.getT(this, 1);
        if (this instanceof BaseView) {
            presenter.setContext(activity);
            presenter.setVM(this, model);
        }
    }

}
