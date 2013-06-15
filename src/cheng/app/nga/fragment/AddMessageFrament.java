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

public class AddMessageFrament extends SherlockDialogFragment implements OnClickListener{
    static final String TAG = "AddBlockFrament";
    EditText mUserName;
    EditText mTitle;
    EditText mContent;

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
        View view = inflater.inflate(R.layout.new_message_layout, null);
        mUserName = (EditText) view.findViewById(R.id.message_to);
        mTitle = (EditText) view.findViewById(R.id.message_title);
        mContent = (EditText) view.findViewById(R.id.message_content);
        return new AlertDialog.Builder(dialogContext)
        .setTitle(R.string.menu_new_message)
        .setView(view)
        .setPositiveButton(R.string.submit, this)
        .setNegativeButton(android.R.string.cancel, null)
        .create();
    }
}
