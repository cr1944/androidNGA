
package cheng.app.nga.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import cheng.app.nga.R;
import cheng.app.nga.util.Configs;

public class FontSizeFragment extends DialogFragment implements OnSeekBarChangeListener {
    private SeekBar mSeekBar;
    private TextView mSampleText;
    private SharedPreferences mPref;
    int mType;
    private static final int TYPE_LIST = 1;
    private static final int TYPE_TEXT = 2;

    private OnClickListener mOnClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            switch (whichButton) {
                case DialogInterface.BUTTON_POSITIVE:
                    setFontSize(mSeekBar.getProgress() + Configs.MINIMUM_FONTSIZE);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
            dismiss();
        }
    };

    private void setFontSize(int fontsize) {
        Editor editor = mPref.edit();
        if (mType == TYPE_LIST)
            editor.putInt(Configs.KEY_LIST_FONT_SIZE, fontsize);
        else
            editor.putInt(Configs.KEY_TEXT_FONT_SIZE, fontsize);
        editor.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mType = args.getInt("type", TYPE_TEXT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.font_setting_view, null);
        mSeekBar = (SeekBar) view.findViewById(R.id.font_size_seekbar);
        mSampleText = (TextView) view.findViewById(R.id.font_size_sample);
        mSeekBar.setMax(Configs.MAXIMUM_FONTSIZE - Configs.MINIMUM_FONTSIZE);
        int fontsize;
        int title;
        if (mType == TYPE_LIST) {
            fontsize = mPref.getInt(Configs.KEY_LIST_FONT_SIZE, Configs.FONT_SIZE_DEFAULT);
            title = R.string.list_font_size;
        } else {
            fontsize = mPref.getInt(Configs.KEY_TEXT_FONT_SIZE, Configs.FONT_SIZE_DEFAULT);
            title = R.string.text_font_size;
        }
        mSeekBar.setProgress(fontsize - Configs.MINIMUM_FONTSIZE);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSampleText.setTextSize(fontsize);
        return new AlertDialog.Builder(getActivity()).setTitle(title).setView(view)
                .setPositiveButton(android.R.string.ok, mOnClickListener)
                .setNegativeButton(android.R.string.cancel, mOnClickListener).create();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSampleText.setTextSize(progress + Configs.MINIMUM_FONTSIZE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
