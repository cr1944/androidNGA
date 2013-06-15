package cheng.app.nga.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cheng.app.nga.R;
import cheng.app.nga.activity.TopicListActivity;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.NgaBoard;
import cheng.app.nga.loader.CustomLoader;

import java.util.List;

public class RecentViewFragment extends LoaderListFragment<NgaBoard> {
    static final String TAG = "RecentViewFragment";
    NGAApp mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        LOADER_TAG = 6;
        setHasOptionsMenu(true);
        mApp = (NGAApp) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.recent_grid, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onFirstLoad() {
        Log.d(TAG, "onFirstLoad");
        requestLoad(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuitem = menu.findItem(R.id.menu_clean_recent);
        menuitem.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clean_recent:
                mApp.cleanRecent();
                requestLoad(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected BaseAdapter createListAdapter(Context context, List<NgaBoard> list) {
        RecentAdapter adapter = new RecentAdapter(context, list);
        GridView grid = (GridView) getListView();
        grid.setAdapter(adapter);
        return adapter;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        RecentAdapter adapter = (RecentAdapter) getAdapter();
        NgaBoard item = adapter.getItem(arg2);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(getActivity(), TopicListActivity.class);
        intent.setData(Uri.parse("http://bbs.ngacn.cc/thread.php?fid=" + item.id));
        intent.putExtra("board_title", item.title);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    static class RecentListLoader extends CustomLoader<NgaBoard> {
        NGAApp mApp;
        List<NgaBoard> mDatas;
        public RecentListLoader(Activity context) {
            super(context);
            mApp = (NGAApp) context.getApplication();
        }

        @Override
        public List<NgaBoard> loadInBackground() {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mApp.loadRecentItems();
        }
    }

    private class RecentAdapter extends ArrayAdapter<NgaBoard> {
        final LayoutInflater mInflater;
        public RecentAdapter(Context context, List<NgaBoard> list) {
            super(context, 0, list);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.recent_grid_item, null);
            }
            NgaBoard item = getItem(position);
            TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
            text1.setText(item.title);
            int icon = item.icon == 0 ? R.drawable.pdefault : item.icon;
            text1.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0);
            return convertView;
        }
    }

    @Override
    public Loader<List<NgaBoard>> onCreateLoader(int arg0, Bundle arg1) {
        return new RecentListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<NgaBoard>> arg0, List<NgaBoard> arg1) {
        super.onLoadFinished(arg0, arg1);
    }

}
