
package cheng.app.nga.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cheng.app.nga.R;
import cheng.app.nga.adapter.AccountsAdapter;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.NGASQLiteHelper.AccountColumns;
import cheng.app.nga.util.Configs;

public class AccountsFragment extends SherlockListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener {
    static final String TAG = "AccountsFragment";
    static int LOADER_TAG = 100;
    AccountsAdapter mAdapter;
    int mUid;
    NGAApp mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (NGAApp) getActivity().getApplication();
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.account_empty));
        getListView().setBackgroundColor(getResources().getColor(R.color.transparent));
        getListView().setCacheColorHint(getResources().getColor(R.color.transparent));
        getListView().setOnItemLongClickListener(this);
        setListShown(false);
        requestLoad();
    }

    public void requestLoad() {
        getLoaderManager().restartLoader(LOADER_TAG, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem favorite = menu.findItem(R.id.menu_add_account);
        favorite.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_account:
                AddAccountFrament.newInstance().show(getActivity().getSupportFragmentManager(), "addaccount");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG,"onListItemClick");
        Cursor c = (Cursor)mAdapter.getItem(position);
        int newUid = c.getInt(c.getColumnIndex(AccountColumns.UID));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Configs.NUKE_URL + Configs.USER_INFO + newUid));
        startActivity(intent);
        //mApp.setDefaultAccount(newUid);
        //requestLoad();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Cursor c = (Cursor)mAdapter.getItem(arg2);
        mUid = c.getInt(c.getColumnIndex(AccountColumns.UID));
        new AlertDialog.Builder(getActivity())
        .setTitle(R.string.delete)
        .setMessage(R.string.delete_confirm)
        .setPositiveButton(android.R.string.ok, mListener)
        .setNegativeButton(android.R.string.cancel, null)
        .create().show();
        //mApp.deleteAccount(id);
        //requestLoad();
        return true;
    }

    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            mApp.deleteAccount(mUid);
        }
        
    };

    static class AccountLoader extends CursorLoader {
        NGAApp mApp;

        public AccountLoader(Activity context) {
            super(context);
            mApp = (NGAApp) context.getApplication();
        }
        @Override
        public Cursor loadInBackground() {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mApp.loadAccounts();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new AccountLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        if (mAdapter == null) {
            mAdapter = new AccountsAdapter(getActivity(), arg1);
            setListAdapter(mAdapter);
        } else {
            mAdapter.changeCursor(arg1);
        }

        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.changeCursor(null);
    }
}
