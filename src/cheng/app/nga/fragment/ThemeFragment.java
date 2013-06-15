package cheng.app.nga.fragment;

import cheng.app.nga.R;
import cheng.app.nga.activity.MainActivity;
import cheng.app.nga.util.Configs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;

public class ThemeFragment extends DialogFragment {
    private SharedPreferences mPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int theme = mPref.getInt(Configs.KEY_THEME, 0);
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.theme)
                .setSingleChoiceItems(R.array.themes, theme, mOnClickListener)
                .create();
    }

    private OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int p) {
            dismiss();
            int theme = mPref.getInt(Configs.KEY_THEME, 0);
            if (theme == p) {
                return;
            }
            Editor editor = mPref.edit();
            editor.putInt(Configs.KEY_THEME, p);
            editor.commit();
            Intent intent = new Intent(getActivity(), MainActivity.class);
//            finish();
//            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            overridePendingTransition(R.anim.stay, R.anim.alphaout);
        }
    };
}
