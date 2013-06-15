package cheng.app.nga.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;

import cheng.app.nga.R;


import java.lang.ref.WeakReference;

public abstract class ProgressTask<Params, Result, Target extends Activity> extends WeakAsyncTask<Params, Void, Result, Target>{
    static final String TAG = "ProgressTask";
    private WeakReference<ProgressDialog> mProgress;
    private int mLoadingText;

    public ProgressTask(Target target, int text) {
        super(target);
        mLoadingText = text;
    }

    /** {@inheritDoc} */
    @Override
    protected void onPreExecute(Target target) {
        final Context context = new ContextThemeWrapper(
                target, R.style.AppDarkTheme);

        mProgress = new WeakReference<ProgressDialog>(ProgressDialog.show(context, null,
                context.getText(mLoadingText)));
    }

    /** {@inheritDoc} */
    @Override
    protected void onPostExecute(Target target, Result result) {
        final ProgressDialog dialog = mProgress.get();
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Error dismissing progress dialog", e);
            }
        }
    }
}
