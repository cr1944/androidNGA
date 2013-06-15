package cheng.app.nga.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.TextView;

import cheng.app.nga.R;

public abstract class CustomLoaderFragment<T> extends LoaderFragment<T> implements OnTouchListener,
    AnimationListener {
    static final int MIN_MOVE = 20;
    View mContentView;
    View mEmptyView;
    View mListActionView;
    TextView mLeftText;
    TextView mMediumText;
    TextView mRightText;
    private float lastMotionY;
    boolean isAnimting;
    boolean displayActionBar = true;
    Animation inAnimation;
    Animation outAnimation;
    private OnActionListener mListener;

    public abstract interface OnActionListener {
        public void onActionLeft();
        public void onActionMiddle();
        public void onActionRight();
    }

    public void setActionListener(OnActionListener l) {
        mListener = l;
    }

    public void enableActionBar(boolean show) {
        displayActionBar = show;
        mListActionView.setVisibility(View.GONE);
    }

    OnClickListener mClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_left:
                    if (mListener != null)
                        mListener.onActionLeft();
                    break;
                case R.id.action_middle:
                    if (mListener != null)
                        mListener.onActionMiddle();
                    break;
                case R.id.action_right:
                    if (mListener != null)
                        mListener.onActionRight();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_custom, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListActionView = view.findViewById(R.id.listAction);
        mLeftText = (TextView) view.findViewById(R.id.action_left);
        mMediumText = (TextView) view.findViewById(R.id.action_middle);
        mRightText = (TextView) view.findViewById(R.id.action_right);
        mContentView = view.findViewById(android.R.id.list);
        mEmptyView = view.findViewById(android.R.id.empty);
        if (mContentView == null) {
            throw new IllegalStateException("must have a content view with id(android.R.id.list)");
        }
        mContentView.setOnTouchListener(this);
        inAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.tabs_hide);
        outAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.tabs_show);
        inAnimation.setAnimationListener(this);
        outAnimation.setAnimationListener(this);
        mLeftText.setOnClickListener(mClickListener);
        mMediumText.setOnClickListener(mClickListener);
        mRightText.setOnClickListener(mClickListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //hide();
    }

    public void updateCurrentPage(int page) {
        String text = getString(R.string.current_page, page);
        mMediumText.setText(text);
    }

    public View getContentView() {
        return mContentView;
    }

    public void setEmptyShow(boolean show) {
        if (mEmptyView != null) {
            mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEmptyView.setVisibility(!show ? View.GONE : View.VISIBLE);
        } else {
            Log.e(TAG, "no empty view!");
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        isAnimting = true;
        if (animation == outAnimation) {
            mListActionView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        isAnimting = false;
        if (animation == inAnimation) {
            mListActionView.setVisibility(View.GONE);
        }
    }

    protected void hide() {
        if (mListActionView.getVisibility() == View.VISIBLE && !isAnimting)
            mListActionView.startAnimation(inAnimation);
    }

    protected void show() {
        if (mListActionView.getVisibility() == View.GONE && !isAnimting)
            mListActionView.startAnimation(outAnimation);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!displayActionBar)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastMotionY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                float dy = lastMotionY - currentY;
                if (dy > MIN_MOVE) {
                    lastMotionY = currentY;
                    hide();
                } else if (dy < -MIN_MOVE) {
                    lastMotionY = currentY;
                    show();
                }
                break;

            default:
                break;
        }
        return false;
    }

}
