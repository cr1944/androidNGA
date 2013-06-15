package cheng.app.nga.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.content.MessageEntry;
import cheng.app.nga.util.TimeUtil;

import java.util.List;

public class MessageAdapter extends CommonArrayAdapter<MessageEntry> {
    public MessageAdapter(Context context, List<MessageEntry> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.message_list_item, null);
        }
        TextView from = (TextView) convertView.findViewById(R.id.from);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView subject = (TextView) convertView.findViewById(R.id.subject);
        MessageEntry item = getItem(position);
        subject.setText(item.subject + "(" + item.posts + ")");
        from.setText(item.from_username);
        time.setText(TimeUtil.formatTime(getContext(), item.time * 1000));
        return convertView;
    }
}

