package cheng.app.nga.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import cheng.app.nga.R;
import java.util.List;

public abstract class LoaderListFragment<T> extends LoaderFragment<T> implements OnItemClickListener {
    static final String TAG = "LoaderListFragment";
    private BaseAdapter mAdapter;
    private AbsListView mListView;
    private TextView mEmptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_content, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mEmptyView = (TextView) view.findViewById(android.R.id.empty);
        if (mListView == null) {
            throw new IllegalStateException("mast have a AbsListView with id(android.R.id.list)");
        }
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = createListAdapter(getActivity(), getData());
    }

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

    public AbsListView getListView() {
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
