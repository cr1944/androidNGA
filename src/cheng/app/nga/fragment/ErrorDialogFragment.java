package cheng.app.nga.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import cheng.app.nga.R;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class ErrorDialogFragment extends SherlockDialogFragment {
    String mErrorCode;

    public static ErrorDialogFragment newInstance(String message) {
        ErrorDialogFragment frag = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        mErrorCode = arg.getString("message");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context dialogContext = new ContextThemeWrapper(
                getActivity(), R.style.AppDarkTheme);
        return new AlertDialog.Builder(dialogContext)
        .setTitle(R.string.error_dialog_title)
        .setMessage(mErrorCode)
        .setNegativeButton(android.R.string.ok, null)
        .create();
    }
}
