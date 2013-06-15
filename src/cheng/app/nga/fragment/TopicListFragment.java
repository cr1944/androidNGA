
package cheng.app.nga.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import cheng.app.nga.R;
import cheng.app.nga.activity.TopicListActivity;
import cheng.app.nga.adapter.TopicListAdapter;
import cheng.app.nga.content.ReadEntry;
import cheng.app.nga.loader.TopicListLoader;
import cheng.app.nga.task.MarkTask;
import cheng.app.nga.util.JsonUtil;
import cheng.app.nga.widget.ErrorDisplayer;
import cheng.app.nga.widget.PageChooser;

import java.util.ArrayList;
import java.util.List;

public class TopicListFragment extends CustomLoaderListFragment<ReadEntry> implements
    PageChooser.OnNumberSetListener {
    static final String TAG = "TopicListFragment";
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_SEARCH = 1;
    public static final int MODE_FAVOR = 2;
    public static final String KEY_MODE = "mode";
    public static final String KEY_FID = "fid";
    public static final String KEY_AUTHORID = "authorid";
    public static final String KEY_SEARCHKEY = "key";
    public static final String KEY_PAGE = "page";
    public static final String KEY_NAVIGATION = "recommend";
    int mMode;
    int mFid;
    int mAuthorId;
    int mPage;
    int mNavigationPosition;
    int mTotalPage;
    String mKey;
    Handler mHandler = new Handler();

    public void setItemClickListener(OnItemClickListener l) {
        if (getListView() != null)
            getListView().setOnItemClickListener(l);
    }

    public static TopicListFragment newInstance(int mode, int fid, int recommend, int page,
            int authorid, String key) {
        TopicListFragment f = new TopicListFragment();
        Bundle arg = new Bundle();
        arg.putInt(KEY_MODE, mode);
        arg.putInt(KEY_FID, fid);
        arg.putInt(KEY_NAVIGATION, recommend);
        arg.putInt(KEY_PAGE, page);
        arg.putInt(KEY_AUTHORID, authorid);
        arg.putString(KEY_SEARCHKEY, key);
        f.setArguments(arg);
        return f;
    }

    private OnActionListener mActionListener = new OnActionListener() {

        @Override
        public void onActionLeft() {
            if (mPage > 1) {
                mPage--;
                requestLoad();
            } else {
                Toast.makeText(getActivity(), R.string.first_page_tip, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onActionMiddle() {
            if (mTotalPage > 1000)
                mHandler.post(new PageChooser(getActivity(), TopicListFragment.this, mPage));
            else
                mHandler.post(new PageChooser(getActivity(), TopicListFragment.this, mPage, mTotalPage));
        }

        @Override
        public void onActionRight() {
            mPage++;
            requestLoad();
        }
    };

    public void onNumberSet(int selectedNumber) {
        mPage = selectedNumber;
        Log.d(TAG, "onNumberSet:" + mPage);
        requestLoad();
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        mMode = arg.getInt(KEY_MODE, 0);
        mFid = arg.getInt(KEY_FID, 0);
        mAuthorId = arg.getInt(KEY_AUTHORID, 0);
        mPage = arg.getInt(KEY_PAGE, 1);
        mNavigationPosition = arg.getInt(KEY_NAVIGATION, 0);
        mKey = arg.getString(KEY_SEARCHKEY);
        LOADER_TAG = 2;
        setActionListener(mActionListener);
        if (savedInstanceState != null) {
            Log.d(TAG, "recreate");
            mNavigationPosition = savedInstanceState.getInt(KEY_NAVIGATION, 0);
            mPage = savedInstanceState.getInt(KEY_PAGE, 1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        if (outState != null) {
            outState.putInt(KEY_NAVIGATION, mNavigationPosition);
            outState.putInt(KEY_PAGE, mPage);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(getString(R.string.topic_empty));
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        setItemClickListener((TopicListActivity)getActivity());
    }

    @Override
    protected void onFirstLoad() {
        requestLoad();
    }

    public void requestLoad() {
        updateCurrentPage(mPage);
        Bundle arg = new Bundle();
        arg.putInt(KEY_MODE, mMode);
        arg.putInt(KEY_FID, mFid);
        arg.putInt(KEY_AUTHORID, mAuthorId);
        arg.putString(KEY_SEARCHKEY, mKey);
        arg.putInt(KEY_PAGE, mPage);
        arg.putInt(KEY_NAVIGATION, mNavigationPosition);
        requestLoad(arg);
    }

    public void onNavigationChanged(int position) {
        mNavigationPosition = position;
        Log.d(TAG, "onNavigationChanged");
        mPage = 1;
        requestLoad();
    }

    public void onCleanMarked() {
        ArrayList<ReadEntry> data = getData();
        Integer[] params = new Integer[data.size() + 1];
        params[0] = 1;
        for(int i = 0; i < data.size(); i++) {
            ReadEntry item = data.get(i);
            params[i + 1] = item.tid;
        }
        new MarkTask(getActivity(), R.string.operating).execute(params);
    }

    public ReadEntry getItem(int position) {
        TopicListAdapter adapter = (TopicListAdapter) getAdapter();
        return adapter.getItem(position);
    }

    @Override
    protected BaseAdapter createListAdapter(Context context, List<ReadEntry> list) {
        TopicListAdapter adapter = new TopicListAdapter(context, list);
        getListView().setAdapter(adapter);
        return adapter;
    }

    @Override
    public Loader<List<ReadEntry>> onCreateLoader(int arg0, Bundle arg1) {
        return new TopicListLoader(getActivity(), arg1);
    }

    @Override
    public void onLoadFinished(Loader<List<ReadEntry>> arg0, List<ReadEntry> arg1) {
        super.onLoadFinished(arg0, arg1);
        TopicListLoader loader = (TopicListLoader) arg0;
        String result = loader.getResultCode();
        mTotalPage = loader.getTotalPage();
        if (mTotalPage > 1) {
            if (arg1 != null && !arg1.isEmpty()) {
                show();
                getListView().setSelection(0);
            }
        } else {
            enableActionBar(false);
        }
        if (TextUtils.isEmpty(result) || !result.equals(JsonUtil.RESULT_OK)) {
            mHandler.post(new ErrorDisplayer(getActivity(), result));
        }
    }

}
