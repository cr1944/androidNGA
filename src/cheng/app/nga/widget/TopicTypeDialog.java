
package cheng.app.nga.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import cheng.app.nga.R;
import cheng.app.nga.content.TopicKeysEntry;

import java.util.ArrayList;

public class TopicTypeDialog extends AlertDialog implements OnClickListener {
    static final String TAG = "TopicTypeFragment";
    ListView mListView;
    Callback mCallback;
    ArrayList<TopicKeysEntry> mTopicKeys;
    public interface Callback {
        public void onCallback(String value);
    }

    public void setCallback(Callback l) {
        mCallback = l;
    }

    public TopicTypeDialog(Context context, Callback l) {
        super(context);
        setCallback(l);
    }

    public void setContent(ArrayList<TopicKeysEntry> list) {
        mTopicKeys = list;
        if (mTopicKeys != null) {
            mListView.setAdapter(new KeyAdapter(getContext(), mTopicKeys));
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        String result = "";
        for (int i = 0; i < mListView.getCount(); i++) {
            if (mListView.isItemChecked(i)) {
                result += mTopicKeys.get(i).key;
            }
        }
        if (mCallback != null) {
            mCallback.onCallback(result);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_topictype, null);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        setTitle(R.string.topic_type);
        setView(view);
        setButton(DialogInterface.BUTTON_POSITIVE, 
                getContext().getText(android.R.string.ok), this);
        setButton(DialogInterface.BUTTON_NEGATIVE,
                getContext().getText(android.R.string.cancel), this);
        super.onCreate(savedInstanceState);
    }

    class KeyAdapter extends ArrayAdapter<TopicKeysEntry> {
        private LayoutInflater mInflater;

        public KeyAdapter(Context context, ArrayList<TopicKeysEntry> list) {
            super(context, 0, mTopicKeys);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = this.mInflater.inflate(android.R.layout.simple_list_item_multiple_choice, null);
            }
            CheckedTextView view = (CheckedTextView) convertView;
            TopicKeysEntry item = getItem(position);
            view.setText(item.key);
            if (item.top == 1) {
                view.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                view.setTypeface(Typeface.DEFAULT);
            }
            return convertView;
        }

    }
}
