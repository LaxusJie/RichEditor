package com.app.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * desc：框架Fragment<p>
 * 继承他的Activity必须实现接口<p>
 * author：haojie
 * date：2017-06-06
 */
public abstract class FrameBaseFragment extends Fragment {

    //默认参数
    protected static final String PARAM1 = "param1";
    protected static final String PARAM2 = "param2";
    protected String mParam1;
    protected String mParam2;
    protected View rootView;
    protected boolean isFirstRun;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(PARAM1);
            mParam2 = getArguments().getString(PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), null);
            ButterKnife.bind(this, rootView);
            isFirstRun = true;
        } else {
            isFirstRun = false;
            initView();
            return rootView;
        }
        initView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉Activity，当前Fragment在栈顶
        mListener.setSelectedFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * fragment对外接口，适用于通用返回键处理等
     */
    public interface OnFragmentInteractionListener {
        public void setSelectedFragment(FrameBaseFragment selectedFragment);
    }

    public abstract int getLayoutId();

    public abstract void initView();

    public abstract boolean onBackPressed();
}
