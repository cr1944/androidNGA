package cheng.app.nga.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.activity.MessageActivity;
import cheng.app.nga.adapter.CommonArrayAdapter;
import cheng.app.nga.content.BlockEntry;
import cheng.app.nga.loader.BlockLoader;
import cheng.app.nga.util.Configs;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class BlockFragment extends SherlockListFragment implements
    LoaderManager.LoaderCallbacks<List<BlockEntry>>, OnItemLongClickListener {
    static final String TAG = "BlockFragment";
    public static final String KEY_ACCOUNT = "account";
    static int LOADER_TAG = 200;
    ArrayList<BlockEntry> mData;
    BlockAdapter mAdapter;

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
        setEmptyText(getString(R.string.block_empty));
        getListView().setOnItemLongClickListener(this);
        mData = new ArrayList<BlockEntry>();
        mAdapter = new BlockAdapter(getActivity(), mData);
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
        menu.add(0, 100, 0, R.string.menu_add_block)
        .setIcon(R.drawable.ic_plus_holo_dark)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 100:
                new AddBlockFrament().show(getActivity().getSupportFragmentManager(), "addblock");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        BlockEntry item = mAdapter.getItem(position);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(Configs.NUKE_URL + Configs.USER_INFO + item.uid));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        new AlertDialog.Builder(getActivity())
        .setTitle(R.string.delete)
        .setMessage(R.string.delete_block_confirm)
        .setPositiveButton(android.R.string.ok, mListener)
        .setNegativeButton(android.R.string.cancel, null)
        .create().show();
        return true;
    }

    private OnClickListener mListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
        
    };

    @Override
    public Loader<List<BlockEntry>> onCreateLoader(int arg0, Bundle arg1) {
        return new BlockLoader(getActivity(), arg1);
    }

    @Override
    public void onLoadFinished(Loader<List<BlockEntry>> arg0, List<BlockEntry> arg1) {
        mData.clear();
        if (arg1 == null || arg1.isEmpty()) {
            Log.d(TAG, "result is empty!");
        } else {
            mData.addAll(arg1);
        }
        mAdapter.notifyDataSetChanged();
        MessageActivity activity = (MessageActivity) getActivity();
        if (activity != null) {
            activity.startLoadNotification();
        }
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<BlockEntry>> arg0) {
        mAdapter.notifyDataSetChanged();
    }

    class BlockAdapter extends CommonArrayAdapter<BlockEntry> {
        public BlockAdapter(Context context, List<BlockEntry> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView v = (TextView) convertView;
            BlockEntry item = getItem(position);
            v.setText(item.username);
            return convertView;
        }
    }
}
