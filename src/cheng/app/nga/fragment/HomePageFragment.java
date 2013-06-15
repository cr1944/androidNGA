
package cheng.app.nga.fragment;

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
import android.widget.BaseAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.util.List;

import za.co.immedia.pinnedheaderlistview.PinnedHeaderListView;

import cheng.app.nga.R;
import cheng.app.nga.activity.TopicListActivity;
import cheng.app.nga.adapter.SectionedAdapter;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.NgaBoard;
import cheng.app.nga.content.NgaCategory;
import cheng.app.nga.loader.HomePageLoader;
import cheng.app.nga.util.Configs;

public class HomePageFragment extends LoaderListFragment<NgaCategory> {
    static final String TAG = "HomePageFragment";
    NGAApp mApp;
    PinnedHeaderListView mListView;

    private PinnedHeaderListView.OnItemClickListener mItemClickListener = new PinnedHeaderListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
            SectionedAdapter adapter = (SectionedAdapter) adapterView.getAdapter();
            NgaBoard board = (NgaBoard) adapter.getItem(section, position);
            mApp.addToRecent(board.id, board.icon, board.title, board.summary);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClass(getActivity(), TopicListActivity.class);
            intent.setData(Uri.parse(Configs.THREAD_URL + "?fid=" + board.id));
            intent.putExtra("board_title", board.title);
            startActivity(intent);
        }

        @Override
        public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
        }
    };

    public static HomePageFragment newInstance() {
        HomePageFragment f = new HomePageFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (NGAApp) getActivity().getApplication();
        LOADER_TAG = 5;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.homepage_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (PinnedHeaderListView) getListView();
        mListView.setOnItemClickListener(mItemClickListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onFirstLoad() {
        requestLoad(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem add = menu.findItem(R.id.menu_add_board);
        add.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_board:
                AddBoardFrament.newInstance().show(getActivity().getSupportFragmentManager(), "addboard");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected BaseAdapter createListAdapter(Context context, List<NgaCategory> list) {
        SectionedAdapter adapter = new SectionedAdapter(context, list);
        mListView.setAdapter(adapter);
        return adapter;
    }

    @Override
    public Loader<List<NgaCategory>> onCreateLoader(int arg0, Bundle arg1) {
        return new HomePageLoader(getActivity());
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }
}
