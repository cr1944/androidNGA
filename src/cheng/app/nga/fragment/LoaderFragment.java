package cheng.app.nga.fragment;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.OnItemClickListener;

import cheng.app.nga.R;

import com.actionbarsherlock.app.SherlockFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class LoaderFragment<T> extends SherlockFragment implements
    LoaderManager.LoaderCallbacks<List<T>> {
    static final String TAG = "LoaderFragment";
    int LOADER_TAG = 0;
    private ArrayList<T> mData;
    private View mProgressContainer;
    private View mListContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_content, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListContainer = view.findViewById(R.id.listContainer);
        mProgressContainer = view.findViewById(R.id.progressContainer);
        if (mListContainer == null || mProgressContainer == null) {
            throw new IllegalStateException("not supported custom layout");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mData = new ArrayList<T>();
        onFirstLoad();
    }

    protected abstract void onFirstLoad();

    protected ArrayList<T> getData() {
        return mData;
    }

    @Override
    public void onLoadFinished(Loader<List<T>> arg0, List<T> arg1) {
        mData.clear();
        if (arg1 == null || arg1.isEmpty()) {
            Log.d(TAG, "result is empty!");
        } else {
            mData.addAll(arg1);
        }
        setContentShown(true);
    }

    @Override
    public void onLoaderReset(Loader<List<T>> arg0) {
        mData.clear();
    }

    public void requestLoad(Bundle arg) {
        setContentShown(false);
        Log.d(TAG,"LOADER_TAG:"+LOADER_TAG);
        getLoaderManager().restartLoader(LOADER_TAG, arg, this);
    }

    public void setContentShown(boolean shown) {
        setContentShown(shown, true);
    }
    
    public void setContentShownNoAnimation(boolean shown) {
        setContentShown(shown, false);
    }

    private void setContentShown(boolean show, boolean animate) {
        if (mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (show) {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
        }
    }
}
