
package cheng.app.nga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cheng.app.nga.R;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.fragment.AboutFragment;
import cheng.app.nga.fragment.AccountsFragment;
import cheng.app.nga.fragment.HomePageFragment;
import cheng.app.nga.fragment.RecentViewFragment;
import cheng.app.nga.fragment.SettingFragment;
import cheng.app.nga.fragment.SlidingMenuFragment;
import cheng.app.nga.fragment.TopicListFragment;
import cheng.app.nga.fragment.SlidingMenuFragment.Callbacks;
import cheng.app.nga.widget.TabManager;

import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends AbsMainActivity implements Callbacks{
    static final String TAG = "MainActivity";

    static final String KEY_CUR_FRAGMENT_TAG = "cur_tag";
    static final String TAG_SLIDINGMENU = "nga_slidingmenu";
    static final int DELAY = 40;

    private SlidingMenuFragment mSlidingMenu;
    TabManager mTabManager;
    NGAApp mApp;
    String mCurFragmentTag;
    Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (NGAApp) getApplication();
        setContentView(R.layout.layout_main);
        mTabManager = new TabManager(this, R.id.content_frame);
        mTabManager.addTab(SlidingMenuFragment.TABS[0], RecentViewFragment.class, null);
        mTabManager.addTab(SlidingMenuFragment.TABS[1], HomePageFragment.class, null);
        mTabManager.addTab(SlidingMenuFragment.TABS[2], AccountsFragment.class, null);
        mTabManager.addTab(SlidingMenuFragment.TABS[5], SettingFragment.class, null);
        mTabManager.addTab(SlidingMenuFragment.TABS[6], AboutFragment.class, null);
        if (savedInstanceState != null) {
            mCurFragmentTag = savedInstanceState.getString(KEY_CUR_FRAGMENT_TAG);
        }
        if (TextUtils.isEmpty(mCurFragmentTag)) {
            mCurFragmentTag = SlidingMenuFragment.TABS[0];
        }
        mTabManager.onTabChanged(mCurFragmentTag);
        // check if the content frame contains the menu frame
        if (findViewById(R.id.menu_frame) == null) {
            setBehindContentView(R.layout.menu_frame);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            // add a dummy view
            View v = new View(this);
            setBehindContentView(v);
            getSlidingMenu().setSlidingEnabled(false);
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
        final FragmentManager fm = getSupportFragmentManager();
        mSlidingMenu = (SlidingMenuFragment)fm.findFragmentByTag(TAG_SLIDINGMENU);
        //mRecentView = (RecentViewFragment) fm.findFragmentByTag(TAG_RECENT);
        //mHomePage = (HomePageFragment) fm.findFragmentByTag(TAG_HOME);
        if (mSlidingMenu == null) {
            Log.d(TAG, "mSlidingMenu == null");
            final FragmentTransaction ft = fm.beginTransaction();
            mSlidingMenu = SlidingMenuFragment.newInstance();
            ft.replace(R.id.menu_frame, mSlidingMenu, TAG_SLIDINGMENU);
            ft.commit();
            //fm.executePendingTransactions();
        }
        mSlidingMenu.setCallbacks(this);

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setBehindWidthRes(R.dimen.slidingmenu_offset);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindScrollScale(0.25f);
        sm.setFadeDegree(0.25f);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }
    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onCallback(int position, String tag) {
        switch (position) {
//            case 3: {
//                Intent intent = new Intent(this, MessageActivity.class);
//                startActivity(intent);
//                break;
//            }
            case 3: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClass(this, TopicListActivity.class);
                intent.putExtra("mode", TopicListFragment.MODE_FAVOR);
                startActivity(intent);
                break;
            }
            case 4:
                break;

            default:
                mTabManager.onTabChanged(tag);
                mCurFragmentTag = tag;
                invalidateOptionsMenu();
                break;
        }
        mHandler.postDelayed(new Runnable() {
            public void run() {
                getSlidingMenu().showContent();
            }
        }, DELAY);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_CUR_FRAGMENT_TAG, mCurFragmentTag);
        super.onSaveInstanceState(outState);
    }

    public void onAccountAdded() {
        //mTabHost.setCurrentTab(1);
    }

    public void addBoard(int boardId, String title, String summary) {
        if(mApp.addExtraBoard(boardId, 0, title, summary)) {
            Toast.makeText(this, R.string.add_success, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.add_fail, Toast.LENGTH_SHORT).show();
        }
    }

}
