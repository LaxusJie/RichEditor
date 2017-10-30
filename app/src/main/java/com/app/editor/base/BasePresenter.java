package com.app.editor.base;

/**
 * desc：控制类基类
 * author：haojie
 * date：2016/12/12
 */
public abstract class BasePresenter<V, M extends BaseModel> {
    public BaseActivity activity;
    public V view;
    public M model;

    public void setContext(BaseActivity activity) {
        this.activity = activity;
    }

    void setVM(V v, M m) {
        this.view = v;
        this.model = m;
        this.model.activity = activity;
    }
}
