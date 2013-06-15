package cheng.app.nga.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import cheng.app.nga.R;

public class ErrorDisplayer implements Runnable {
    Context mActivity;
    String mErrorCode;

    public ErrorDisplayer(Context activity, String error) {
        mErrorCode = error;
        mActivity = activity;
    }

    public ErrorDisplayer(Context activity, int errorRes) {
        mErrorCode = activity.getString(errorRes);
        mActivity = activity;
    }

    @Override
    public void run() {
        onCreateDialog().show();
    }

    private Dialog onCreateDialog() {
        if (TextUtils.isEmpty(mErrorCode)) {
            mErrorCode = mActivity.getString(R.string.error_unknown);
        }
        String message = mActivity.getString(R.string.error_message, mErrorCode);
        final Context dialogContext = new ContextThemeWrapper(
                mActivity, R.style.AppDarkTheme);
        return new AlertDialog.Builder(dialogContext)
        .setTitle(R.string.error_dialog_title)
        .setMessage(message)
        .setNegativeButton(android.R.string.ok, null)
        .create();
    }
}
