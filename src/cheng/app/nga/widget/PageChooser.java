package cheng.app.nga.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import android.view.ContextThemeWrapper;

import net.simonvt.numberpicker.NumberPicker;

import cheng.app.nga.R;

public class PageChooser implements Runnable {
    Context mActivity;
    int mPage;
    int mMaxPage;
    private OnNumberSetListener mListener;

    public PageChooser(Context activity, OnNumberSetListener l, int page) {
        mPage = page;
        mMaxPage = 0;
        mActivity = activity;
        mListener = l;
    }

    public PageChooser(Context activity, OnNumberSetListener l, int page, int maxPage) {
        mPage = page;
        mMaxPage = maxPage;
        mActivity = activity;
        mListener = l;
    }

    @Override
    public void run() {
        if(VERSION.SDK_INT >= 11) {
            onCreateDialogv11().show();
        } else {
            onCreateDialog().show();
        }
    }

    private Dialog onCreateDialog() {
        final Context dialogContext = new ContextThemeWrapper(
                mActivity, R.style.AppDarkTheme);
        final NumberPicker mNumberPicker = new NumberPicker(dialogContext);
        OnClickListener l = new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onNumberSet(mNumberPicker.getValue());
                }
            }
        };
        if (mMaxPage == 0) {
            int start = 1;
            int end = mPage + 5;
            mNumberPicker.setMinValue(start);
            mNumberPicker.setMaxValue(end);
        } else {
            int start = 1;
            int end = mMaxPage;
            mNumberPicker.setMinValue(start);
            mNumberPicker.setMaxValue(end);
        }
        mNumberPicker.setValue(mPage);
        return new AlertDialog.Builder(dialogContext)
        .setTitle(R.string.pick_page_title)
        .setView(mNumberPicker)
        .setPositiveButton(android.R.string.ok, l)
        .setNegativeButton(android.R.string.cancel, null)
        .create();
    }

    @TargetApi(11)
    private Dialog onCreateDialogv11() {
        final Context dialogContext = new ContextThemeWrapper(
                mActivity, R.style.AppDarkTheme);
        final android.widget.NumberPicker mNumberPicker = new android.widget.NumberPicker(dialogContext);
        OnClickListener l = new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onNumberSet(mNumberPicker.getValue());
                }
            }
        };
        if (mMaxPage == 0) {
            int start = mPage - 5 > 0 ? (mPage - 5) : 1;
            int end = start + 10;
            mNumberPicker.setMinValue(start);
            mNumberPicker.setMaxValue(end);
        } else {
            mNumberPicker.setMinValue(1);
            mNumberPicker.setMaxValue(mMaxPage);
        }
        mNumberPicker.setValue(mPage);
        return new AlertDialog.Builder(dialogContext)
        .setTitle(R.string.pick_page_title)
        .setView(mNumberPicker)
        .setPositiveButton(android.R.string.ok, l)
        .setNegativeButton(android.R.string.cancel, null)
        .create();
    }

    public interface OnNumberSetListener {
        public void onNumberSet(int selectedNumber);
    }

}
