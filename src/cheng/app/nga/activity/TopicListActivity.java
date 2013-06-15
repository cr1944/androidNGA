
package cheng.app.nga.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import cheng.app.nga.R;
import cheng.app.nga.adapter.ActionAccountsAdapter;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.ReadEntry;
import cheng.app.nga.fragment.TopicListFragment;
import cheng.app.nga.util.Configs;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnCloseListener;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

import org.apache.commons.lang3.ArrayUtils;

public class TopicListActivity extends AbsThemeActivity implements
        ActionBar.OnNavigationListener, OnItemClickListener, OnQueryTextListener, OnCloseListener {
    static final String TAG = "TopicListActivity";
    static final String KEY_NAVIGATION = "key_navigation";
    static final String TAG_LIST = "nga_topiclist";
    TopicListFragment mListFragment;
    CharSequence mTitle;
    int mMode = 0;
    int mFid = 0;
    String mKey;
    int mNavigationItem = 0;
    NGAApp mApp;
    private SearchView mSearchView;
    private boolean mInSearchUi;

    private OnMenuItemClickListener mMenuListener = new OnMenuItemClickListener() {
        
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            enterSearchUi();
            return true;
        }
    };

    private void enterSearchUi() {
        if (mSearchView == null) {
            prepareSearchView();
        }

        final ActionBar actionBar = getSupportActionBar();

        mSearchView.setQuery(null, true);

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        // We need to call this and onActionViewCollapsed() manually, since we are using a custom
        // layout instead of asking the search menu item to take care of SearchView.
        mSearchView.onActionViewExpanded();
        mInSearchUi = true;
        invalidateOptionsMenu();
    }

    private void exitSearchUi() {
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);


        mSearchView.onActionViewCollapsed();
        mInSearchUi = false;
        invalidateOptionsMenu();
    }

    private void prepareSearchView() {
        final View searchViewLayout =
                getLayoutInflater().inflate(R.layout.search_action_bar, null);
        mSearchView = (SearchView) searchViewLayout.findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setQueryHint(getString(R.string.menu_search));
        mSearchView.setIconified(false);

        getSupportActionBar().setCustomView(searchViewLayout,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mApp = (NGAApp) getApplication();
        if (arg0 != null) {
            Log.d(TAG, "recreate");
            mNavigationItem = arg0.getInt(KEY_NAVIGATION, 0);
        }
        setContentView(R.layout.layout_topiclist);
        Intent intent = getIntent();
        if (intent != null) {
            Log.d(TAG, "get args from intent");
            Uri uri = intent.getData();
            if (uri != null) {
                final String value = uri.getQueryParameter("fid");
                if (!TextUtils.isEmpty(value)) {
                    try {
                        mFid = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "can not parse:" + uri);
                    }
                }
            }
            mTitle = intent.getCharSequenceExtra("board_title");
            mMode = intent.getIntExtra("mode", TopicListFragment.MODE_DEFAULT);
            mKey = intent.getStringExtra("key");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mMode == TopicListFragment.MODE_FAVOR) {
            mTitle = getString(R.string.favorite);
            Cursor c = mApp.loadAccounts();
            if (c == null || c.getCount() == 0) {
                Toast.makeText(this, R.string.login_tips, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(mTitle);
            Context context = getSupportActionBar().getThemedContext();
            ActionAccountsAdapter list = new ActionAccountsAdapter(context, c);
            getSupportActionBar().setListNavigationCallbacks(list, this);
            getSupportActionBar().setSelectedNavigationItem(mNavigationItem);
        } else if (mMode == TopicListFragment.MODE_SEARCH) {
            mTitle = getString(R.string.menu_search);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            getSupportActionBar().setTitle(mTitle);
        } else {
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Context context = getSupportActionBar().getThemedContext();
            CharSequence[] strings = context.getResources().getTextArray(R.array.title);
            CharSequence[] titles = ArrayUtils.add(strings, 0, mTitle);
            ArrayAdapter<CharSequence> list = new ArrayAdapter<CharSequence>(context,
                    R.layout.sherlock_spinner_item, titles);
            list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
            getSupportActionBar().setListNavigationCallbacks(list, this);
            getSupportActionBar().setSelectedNavigationItem(mNavigationItem);
        }
        final FragmentManager fm = getSupportFragmentManager();
        mListFragment = (TopicListFragment) fm.findFragmentByTag(TAG_LIST);
        if (mListFragment == null) {
            Log.d(TAG, "mListFragment == null");
            final FragmentTransaction ft = fm.beginTransaction();
            mListFragment = TopicListFragment.newInstance(mMode, mFid, mNavigationItem, 1, 0, mKey);
            ft.replace(R.id.topic_frame, mListFragment, TAG_LIST);
            ft.commit();
        }
        fm.executePendingTransactions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt(KEY_NAVIGATION, mNavigationItem);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        menu.findItem(R.id.menu_search).setOnMenuItemClickListener(mMenuListener);
        //searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem post = menu.findItem(R.id.menu_new_topic);
        final MenuItem search = menu.findItem(R.id.menu_search);
        final MenuItem del = menu.findItem(R.id.menu_delete);
        final MenuItem refresh = menu.findItem(R.id.menu_refresh);
        if (mInSearchUi) {
            post.setVisible(false);
            search.setVisible(false);
            del.setVisible(false);
            refresh.setVisible(false);
        } else if (mMode == TopicListFragment.MODE_FAVOR) {
            post.setVisible(false);
            search.setVisible(false);
        } else if(mMode == TopicListFragment.MODE_SEARCH) {
            post.setVisible(false);
            search.setVisible(false);
            del.setVisible(false);
        } else {
            del.setVisible(false);
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
                if (mListFragment != null) {
                    mListFragment.requestLoad();
                }
                return true;
            case R.id.menu_new_topic:
                Intent intent = new Intent(this, ReplyActivity.class);
                intent.putExtra("fid", mFid);
                intent.putExtra("action", "new");
                startActivity(intent);
                return true;
            case R.id.menu_delete:
                new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_mark_confirm)
                .setPositiveButton(android.R.string.ok, mListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mInSearchUi) {
            exitSearchUi();
        } else {
            super.onBackPressed();
        }
    }

    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mListFragment != null) {
                mListFragment.onCleanMarked();
            }
        }
        
    };

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (mNavigationItem == itemPosition) {
            return true;
        }
        mNavigationItem = itemPosition;
        if (mListFragment != null) {
            mListFragment.onNavigationChanged(itemPosition);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (mListFragment != null) {
            ReadEntry item = mListFragment.getItem(arg2);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClass(this, TopicDetialActivity.class);
            int tid = 0;
            if (item.quote_from != 0) {
                tid = item.quote_from;
            } else {
                tid = item.tid;
            }
            intent.setData(Uri.parse(Configs.READ_URL + "?tid=" + tid));
            intent.putExtra("replies", item.replies);
            intent.putExtra("title", mTitle);
            intent.putExtra("subtitle", item.subject);
            //TODO: use "ifmark" in json data
            intent.putExtra("ifmark", mMode == TopicListFragment.MODE_FAVOR ? 1 : 0);
            startActivity(intent);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            Intent intent = getIntent();
            intent.putExtra("mode", TopicListFragment.MODE_SEARCH);
            intent.putExtra("key", query);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onClose() {
        return false;
    }
}
