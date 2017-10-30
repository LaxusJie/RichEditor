package com.app.functionbar.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.functionbar.R;
import com.app.functionbar.utils.Constant;

/**
 * desc：两个item的竖向的弹框
 * author：ls
 * date：2017/6/27
 */
public class VerticalDialog extends DialogFragment {

    private ClickAction1Listener listener1;
    private ClickAction2Listener listener2;
    private TextView tvDialogAction1;
    private TextView tvDialogAction2;

    public static VerticalDialog newInstance(Bundle bundle, ClickAction1Listener listener1, ClickAction2Listener listener2) {
        VerticalDialog fragment = new VerticalDialog();
        fragment.setArguments(bundle);
        fragment.listener1 = listener1;
        fragment.listener2 = listener2;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(getActivity(), R.style.dialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_vertical, null);
        RelativeLayout rlAction1 = (RelativeLayout) view.findViewById(R.id.rl_dialog_action1);
        RelativeLayout rlAction2 = (RelativeLayout) view.findViewById(R.id.rl_dialog_action2);
        tvDialogAction1 = (TextView) view.findViewById(R.id.tv_dialog_action1);
        tvDialogAction2 = (TextView) view.findViewById(R.id.tv_dialog_action2);
        initText();
        rlAction1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener1.click();
                dismissAllowingStateLoss();
            }
        });
        rlAction2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener2.click();
                dismissAllowingStateLoss();
            }
        });
        customBuilder.setView(view);
        return customBuilder.create();
    }

    private void initText() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            tvDialogAction1.setText(bundle.getCharSequence(Constant.DIALOG_TEXT_TOP)==null?"保存":bundle.getCharSequence(Constant.DIALOG_TEXT_TOP));
            tvDialogAction2.setText(bundle.getCharSequence(Constant.DIALOG_TEXT_BOTTOM)==null?"不保存":bundle.getCharSequence(Constant.DIALOG_TEXT_BOTTOM));
        }
    }


    public interface ClickAction1Listener {
        void click();
    }

    public interface ClickAction2Listener {
        void click();
    }
}
