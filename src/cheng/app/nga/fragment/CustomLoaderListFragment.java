package cheng.app.nga.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public abstract class CustomLoaderListFragment<T> extends CustomLoaderFragment<T> {
    static final String TAG = "LoaderListFragment";
    private BaseAdapter mAdapter;
    private ListView mListView;
    private TextView mEmptyView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        if (mListView == null) {
            throw new IllegalStateException("mast have a ListView with id(android.R.id.list)");
        }
        mListView.setEmptyView(mEmptyView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = createListAdapter(getActivity(), getData());
    }

    protected abstract void onFirstLoad();

    protected abstract BaseAdapter createListAdapter(Context context, List<T> list);

    @Override
    public void onLoadFinished(Loader<List<T>> arg0, List<T> arg1) {
        super.onLoadFinished(arg0, arg1);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<T>> arg0) {
        super.onLoaderReset(arg0);
        mAdapter.notifyDataSetChanged();
    }

    public ListView getListView() {
        return mListView;
    }

    public BaseAdapter getAdapter() {
        return mAdapter;
    }

    public void setEmptyText(CharSequence text) {
        if (mEmptyView == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        mEmptyView.setText(text);
    }

}
