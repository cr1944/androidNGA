
package cheng.app.nga.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import cheng.app.nga.R;
import cheng.app.nga.fragment.ReplyFragment;
import cheng.app.nga.fragment.TopicDetialFragment;
import cheng.app.nga.task.MarkTask;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TopicDetialActivity extends AbsThemeActivity {
    static final String TAG = "TopicDetialActivity";
    static final String TAG_LIST = "nga_detial";
    TopicDetialFragment mDetialFragment;
    ReplyFragment mReplyFragment;
    int mTid;
    int mPid;
    int mIfmark;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_topicdetial);
        Intent intent = getIntent();
        if (intent != null) {
            Log.d(TAG, "get args from intent");
            Uri uri = intent.getData();
            if (uri != null) {
                final String value = uri.getQueryParameter("tid");
                if (!TextUtils.isEmpty(value)) {
                    try {
                        mTid = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "can not parse:" + uri);
                    }
                } else {
                    final String pid = uri.getQueryParameter("pid");
                    if (!TextUtils.isEmpty(pid)) {
                        try {
                            mPid = Integer.parseInt(pid);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "can not parse:" + uri);
                        }
                    }
                }
            }
            CharSequence title = intent.getCharSequenceExtra("title");
            String subtitle = intent.getStringExtra("subtitle");
            mIfmark = intent.getIntExtra("ifmark", 0);
            if (!TextUtils.isEmpty(title))
                getSupportActionBar().setTitle(title);
            if (!TextUtils.isEmpty(subtitle))
                getSupportActionBar().setSubtitle(subtitle);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final FragmentManager fm = getSupportFragmentManager();
        mDetialFragment = (TopicDetialFragment) fm.findFragmentByTag(TAG_LIST);
        if (mDetialFragment == null) {
            Log.d(TAG, "mDetialFragment == null");
            final FragmentTransaction ft = fm.beginTransaction();
            mDetialFragment = TopicDetialFragment.newInstance(mTid, mPid);
            ft.replace(R.id.detial_frame, mDetialFragment, TAG_LIST);
            ft.commit();
        }
        fm.executePendingTransactions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.detial_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem reply = menu.findItem(R.id.menu_reply_topic);
        final MenuItem mark = menu.findItem(R.id.menu_mark);
        final MenuItem refresh = menu.findItem(R.id.menu_refresh);
        if (mTid == 0) {
            reply.setVisible(false);
            mark.setVisible(false);
            refresh.setVisible(false);
        }
        if (mIfmark == 1) {
            mark.setIcon(R.drawable.ic_menu_star_holo_light);
            mark.setTitle(R.string.menu_del_mark);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_refresh:
                mDetialFragment.requestLoad();
                return true;
            case R.id.menu_reply_topic:
                Intent intent = new Intent(this, ReplyActivity.class);
                intent.putExtra("tid", mTid);
                intent.putExtra("action", "reply");
                startActivity(intent);
                return true;
            case R.id.menu_mark:
                Integer[] params = new Integer[] {mIfmark, mTid};
                new MarkTask(this, R.string.operating).execute(params);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDetialFragment.goBack()) {
            return;
        }
        super.onBackPressed();
    }
}
