package cheng.app.nga.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import cheng.app.nga.R;
import cheng.app.nga.activity.MainActivity;
import cheng.app.nga.task.NewAccountTask;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AddAccountFrament extends SherlockDialogFragment implements OnClickListener{
    static final String TAG = "AddAccountFrament";
    EditText mUserName;
    EditText mPassword;

    public static AddAccountFrament newInstance() {
        AddAccountFrament frag = new AddAccountFrament();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String username = mUserName.getText().toString();
        String password = mPassword.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            MainActivity activity = (MainActivity) getActivity();
            new NewAccountTask(activity, R.string.logining).execute(username, password);
        } else {
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context dialogContext = new ContextThemeWrapper(
                getActivity(), R.style.AppDarkTheme);
        LayoutInflater inflater = LayoutInflater.from(dialogContext);
        View view = inflater.inflate(R.layout.add_account_layout, null);
        mUserName = (EditText) view.findViewById(R.id.username);
        mPassword = (EditText) view.findViewById(R.id.password);
        return new AlertDialog.Builder(dialogContext)
        .setTitle(R.string.menu_add_account)
        .setView(view)
        .setPositiveButton(R.string.login, this)
        .setNegativeButton(android.R.string.cancel, null)
        .create();
    }
}
