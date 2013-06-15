package cheng.app.nga.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.content.ReadEntry;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.TextUtil;
import cheng.app.nga.util.TimeUtil;

import java.util.List;

public class TopicListAdapter extends CommonArrayAdapter<ReadEntry> {
    int mFontSize;
    public TopicListAdapter(Context context, List<ReadEntry> list) {
        super(context, list);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mFontSize = mPrefs.getInt(Configs.KEY_LIST_FONT_SIZE, Configs.FONT_SIZE_DEFAULT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.topic_list_item, null);
        }
        ReadEntry item = getItem(position);
        TextView author = (TextView) convertView.findViewById(R.id.author);
        TextView postdate = (TextView) convertView.findViewById(R.id.postdate);
        TextView subject = (TextView) convertView.findViewById(R.id.subject);
        TextView replies = (TextView) convertView.findViewById(R.id.replies);
        TextView lastposter = (TextView) convertView.findViewById(R.id.lastposter);
        TextView lastpost = (TextView) convertView.findViewById(R.id.lastpostdate);
        author.setText(item.author);
        postdate.setText(TimeUtil.formatTime(getContext(), item.postdate * 1000));
        //subject.setText(StringEscapeUtils.unescapeHtml3(item.subject));
        subject.setText(TextUtil.buildNgaTitle(getContext(), item.subject, item.titlefont,
                item.quote_from, item.digest, item.type));
        subject.setTextSize(mFontSize);
        replies.setText(String.valueOf(item.replies));
        lastposter.setText(item.lastposter);
        lastpost.setText(TimeUtil.formatTime(getContext(), item.lastpost * 1000));
        return convertView;
    }
}

