
package cheng.app.nga.fragment;

import com.actionbarsherlock.app.SherlockDialogFragment;

import cheng.app.nga.R;
import cheng.app.nga.adapter.ActionAccountsAdapter;
import cheng.app.nga.content.CommentEntry;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.task.CommentTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

public class CommentFragment extends SherlockDialogFragment implements DialogInterface.OnClickListener,
    OnItemSelectedListener {
    NGAApp mApp;
    CommentEntry mEntry;
    EditText mComment;

    public CommentFragment() {
    }

    public CommentFragment(CommentEntry entry) {
        mEntry = entry;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mApp = (NGAApp) getActivity().getApplication();
        if (savedInstanceState != null) {
            mEntry = savedInstanceState.getParcelable("entry");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle arg0) {
        arg0.putParcelable("entry", mEntry);
        super.onSaveInstanceState(arg0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Cursor c = mApp.loadAccounts();
        final Context dialogContext = new ContextThemeWrapper(
                getActivity(), R.style.AppDarkTheme);
        if (c == null || c.getCount() == 0) {
            return new AlertDialog.Builder(dialogContext)
            .setMessage(getString(R.string.login_tips))
            .setNegativeButton(android.R.string.cancel, this)
            .create();
        } else {
            LayoutInflater inflater = LayoutInflater.from(dialogContext);
            View view = inflater.inflate(R.layout.layout_add_comment, null);
            mComment = (EditText) view.findViewById(android.R.id.edit);
            Spinner s = (Spinner) view.findViewById(R.id.account_spinner);
            ActionAccountsAdapter adapter = new ActionAccountsAdapter(dialogContext, c);
            s.setAdapter(adapter);
            s.setOnItemSelectedListener(this);
            return new AlertDialog.Builder(dialogContext)
            .setView(view)
            .setPositiveButton(R.string.submit, this)
            .setNegativeButton(android.R.string.cancel, this)
            .create();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            mEntry.comment = mComment.getText().toString();
            if (!TextUtils.isEmpty(mEntry.comment))
                new CommentTask(getActivity(), R.string.posting).execute(mEntry);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mEntry.account = arg2;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
