package cheng.app.nga.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

import cheng.app.nga.R;
import cheng.app.nga.adapter.MessageAdapter;
import cheng.app.nga.content.MessageEntry;
import cheng.app.nga.loader.MessageLoader;
import cheng.app.nga.util.JsonUtil;
import cheng.app.nga.widget.ErrorDisplayer;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends SherlockListFragment implements
    LoaderManager.LoaderCallbacks<List<MessageEntry>>, OnItemLongClickListener {
    static final String TAG = "MessageFragment";
    public static final String KEY_ACCOUNT = "account";
    static int LOADER_TAG = 201;
    ArrayList<MessageEntry> mData;
    Handler mHandler = new Handler();
    MessageAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.e(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.message_empty));
        getListView().setOnItemLongClickListener(this);
        mData = new ArrayList<MessageEntry>();
        mAdapter = new MessageAdapter(getActivity(), mData);
        setListAdapter(mAdapter);
        setListShown(false);
        requestLoad(0);
    }

    public void requestLoad(int account) {
        Bundle args = new Bundle();
        args.putInt(KEY_ACCOUNT, account);
        getLoaderManager().restartLoader(LOADER_TAG, args, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 99, 0, R.string.menu_new_message)
        .setIcon(R.drawable.ic_menu_new_post_action_bar)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 99:
                new AddMessageFrament().show(getActivity().getSupportFragmentManager(), "addmessage");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Loader<List<MessageEntry>> onCreateLoader(int arg0, Bundle arg1) {
        return new MessageLoader(getActivity(), arg1);
    }

    @Override
    public void onLoadFinished(Loader<List<MessageEntry>> arg0, List<MessageEntry> arg1) {
        mData.clear();
        if (arg1 == null || arg1.isEmpty()) {
            Log.d(TAG, "result is empty!");
        } else {
            mData.addAll(arg1);
        }
        MessageLoader l = (MessageLoader)arg0;
        String result = l.getResult();
        mAdapter.notifyDataSetChanged();

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
        if (TextUtils.isEmpty(result) || !result.equals(JsonUtil.RESULT_OK)) {
            mHandler.post(new ErrorDisplayer(getActivity(), result));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<MessageEntry>> arg0) {
        mAdapter.notifyDataSetChanged();
    }

}
