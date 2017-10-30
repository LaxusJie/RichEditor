package com.app.editor.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.apkfuns.logutils.LogUtils;
import com.app.base.FrameBaseActivity;
import com.app.base.utils.TUtil;

/**
 * desc：Activity基础类
 * author：haojie
 * date：2016/12/12
 */
public abstract class BaseActivity<P extends BasePresenter, M extends BaseModel> extends FrameBaseActivity implements BaseFragment.OnFragmentInteractionListener{


    public P presenter;
    public M model;
    public BaseActivity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
    }

    @Override
    public void getDisplayInfo() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dpi = dm.densityDpi;
        LogUtils.d("dpi=" + dpi + "height" + dm.heightPixels + "width" + dm.widthPixels);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void initData() {
        activity = this;
        initBaseMVP();
    }

    private void initBaseMVP() {
        presenter = TUtil.getT(this, 0);
        model = TUtil.getT(this, 1);
        if (this instanceof BaseView) {
            presenter.setContext(activity);
            presenter.setVM(this, model);
        }
    }

    protected void  initRecylerView() {

    }

    protected void requestData(final int loadType) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}