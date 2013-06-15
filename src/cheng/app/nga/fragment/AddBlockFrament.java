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

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AddBlockFrament extends SherlockDialogFragment implements OnClickListener{
    static final String TAG = "AddBlockFrament";
    EditText mUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String username = mUserName.getText().toString();
        if (!TextUtils.isEmpty(username)) {
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context dialogContext = new ContextThemeWrapper(
                getActivity(), R.style.AppDarkTheme);
        LayoutInflater inflater = LayoutInflater.from(dialogContext);
        View view = inflater.inflate(R.layout.add_block_layout, null);
        mUserName = (EditText) view.findViewById(R.id.username);
        return new AlertDialog.Builder(dialogContext)
        .setTitle(R.string.menu_add_block)
        .setView(view)
        .setPositiveButton(R.string.submit, this)
        .setNegativeButton(android.R.string.cancel, null)
        .create();
    }
}
