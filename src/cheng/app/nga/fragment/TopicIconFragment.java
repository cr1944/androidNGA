package cheng.app.nga.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import cheng.app.nga.R;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class TopicIconFragment extends SherlockDialogFragment implements OnClickListener{
    static final String KEY_CHECKED_ITEM = "key_checked_item";
    Callback mCallback;
    int mItemChecked = -1;
    static int[] ICONS = {
        R.drawable.i1,
        R.drawable.i2,
        R.drawable.i3,
        R.drawable.i4,
        R.drawable.i5,
        R.drawable.i6,
        R.drawable.i7,
        R.drawable.i8,
        R.drawable.i9,
        R.drawable.i10,
        R.drawable.i11,
        R.drawable.i12,
        R.drawable.i13,
        R.drawable.i14
    };

    public TopicIconFragment() {
    }

    public TopicIconFragment(Callback l) {
        mCallback = l;
    }

    public interface Callback {
        public void onCallback(int value);
    }

    public void setCallback(Callback l) {
        mCallback = l;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        mItemChecked = which;
        if (mCallback != null) {
            mCallback.onCallback(which + 1);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mItemChecked = savedInstanceState.getInt(KEY_CHECKED_ITEM, -1);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle arg0) {
        if (arg0 != null) {
            arg0.putInt(KEY_CHECKED_ITEM, mItemChecked);
        }
        super.onSaveInstanceState(arg0);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context dialogContext = new ContextThemeWrapper(
                getActivity(), R.style.AppDarkTheme);
        return new AlertDialog.Builder(dialogContext)
        .setSingleChoiceItems(new IconAdapter(dialogContext), mItemChecked, this)
        .setNegativeButton(android.R.string.cancel, null)
        .setTitle(R.string.topic_icon)
        .create();
    }

    class IconAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public IconAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(android.R.layout.simple_list_item_single_choice, null);
            }
            CheckedTextView view = (CheckedTextView) convertView;
            int res = ICONS[position];
            view.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0);
            return convertView;
        }

        @Override
        public int getCount() {
            return ICONS.length;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
