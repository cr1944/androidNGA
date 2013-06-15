
package cheng.app.nga.activity;

import com.actionbarsherlock.view.MenuItem;

import cheng.app.nga.R;
import cheng.app.nga.adapter.CommonArrayAdapter;
import cheng.app.nga.content.MentionEntry;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.TimeUtil;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationActivity extends AbsThemeActivity implements OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setEmptyView(findViewById(android.R.id.empty));
        Intent intent = getIntent();
        ArrayList<MentionEntry> items = intent.getParcelableArrayListExtra("notification");
        if (items != null) {
            lv.setAdapter(new NotifyAdapter(this, items));
        }
        lv.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        MentionEntry item = (MentionEntry) arg0.getAdapter().getItem(arg2);
        String s = Configs.READ_URL + "?tid=" + item._ABOUT_ID;
        final Uri data = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW, data);
        intent.setClass(this, TopicDetialActivity.class);
        startActivity(intent);
    }

    class NotifyAdapter extends CommonArrayAdapter<MentionEntry> {

        public NotifyAdapter(Context context, ArrayList<MentionEntry> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.notification_list_item, null);
            }
            MentionEntry item = getItem(position);
            TextView from = (TextView) convertView.findViewById(R.id.from);
            TextView time = (TextView) convertView.findViewById(R.id.time);
            TextView subject = (TextView) convertView.findViewById(R.id.subject);
            from.setText(item._FROM_UNAME + "(" + item._FROM_UID + ")");
            time.setText(TimeUtil.formatTime(getContext(), item._TIME * 1000));
            subject.setText(getString(R.string.noti_text, item._TEXT));
            return convertView;
        }
    }
}
