
package cheng.app.nga.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import cheng.app.nga.R;
import cheng.app.nga.adapter.ActionAccountsAdapter;
import cheng.app.nga.adapter.AppFragmentPagerAdapter;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.fragment.BlockFragment;
import cheng.app.nga.fragment.MessageFragment;
import cheng.app.nga.task.LoadNotificationTask;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AbsThemeActivity implements OnNavigationListener{
    static final String TAG = "MessageActivity";
    static final String[] FRAGMENTTAG = {
        "nga_messagefragment", "nga_blockfragment"
    };
    static final int[] FRAGMENTTITLE = {
            R.string.message, R.string.block
    };
    static final int FRAGMENT_NUMBER = 2;
    ViewPager mPager;
    TitlePageIndicator mIndicator;
    List<Fragment> mFragments = new ArrayList<Fragment>();
    int mAccountIndex = 0;
    NGAApp mApp;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mApp = (NGAApp) getApplication();
        Cursor c = mApp.loadAccounts();
        if (c == null || c.getCount() == 0) {
            Toast.makeText(this, R.string.login_tips, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.message_layout);
        mPager = (ViewPager) findViewById(R.id.message_pager);
        mPager.setOnPageChangeListener(onPageChangeListener);
        mIndicator = (TitlePageIndicator) findViewById(R.id.message_indicator);
        mIndicator.setBackgroundResource(R.drawable.tab_bg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Context context = getSupportActionBar().getThemedContext();
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ActionAccountsAdapter list = new ActionAccountsAdapter(context, c);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setSelectedNavigationItem(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing() && mPager.getAdapter() == null)
                    mPager.setAdapter(new MessagePagerAdapter(getSupportFragmentManager()));
                mIndicator.setViewPager(mPager);
            }
        }, 100);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if(mAccountIndex == itemPosition)
            return true;
        mAccountIndex = itemPosition;
        MessageFragment f1 = (MessageFragment) mFragments.get(0);
        f1.requestLoad(mAccountIndex);
        BlockFragment f2 = (BlockFragment) mFragments.get(1);
        f2.requestLoad(mAccountIndex);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startLoadNotification() {
        new LoadNotificationTask(this).execute(mAccountIndex);
    }

    ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            invalidateOptionsMenu();
        }

    };

    private class MessagePagerAdapter extends AppFragmentPagerAdapter {

        public MessagePagerAdapter(FragmentManager fm) {
            super(fm);
            Fragment f1 = fm.findFragmentByTag(FRAGMENTTAG[0]);
            if (f1 == null) {
                f1 = new MessageFragment();
            }
            mFragments.add(0, f1);
            Fragment f2 = fm.findFragmentByTag(FRAGMENTTAG[1]);
            if (f2 == null) {
                f2 = new BlockFragment();
            }
            mFragments.add(1, f2);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        protected String getTag(int position) {
            return FRAGMENTTAG[position];
        }

        @Override
        public int getCount() {
            return FRAGMENT_NUMBER;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(FRAGMENTTITLE[position]);
        }
    }
}
