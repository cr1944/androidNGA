
package cheng.app.nga.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.widget.CheckableFrameLayout;

public class SlidingMenuFragment extends ListFragment {
    static final String TAG = "SlidingMenuFragment";
    int mCurCheckPosition = 0;
    static final String KEY_POSITION = "key_position";
    MenuAdapter mAdapter;
    Callbacks mCallbacks;

    public interface Callbacks {
        public void onCallback(int position, String tag);
    }

    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    public static final String[] TABS = {
            "nga_recent",//0
            "nga_home",//1
            "nga_accounts",//2
            //"nga_message",//3
            "nga_favor",//4
            "nga_location",//5
            "nga_settings",//6
            "nga_about"//7
    };

    static final int[] TITLES = {
            R.string.recent,
            R.string.home,
            R.string.accounts,
            //R.string.message,
            R.string.favorite,
            R.string.location,
            R.string.settings,
            R.string.about
    };

    static final int[] ICONS = {
            R.drawable.ic_menu_recent_history,
            R.drawable.ic_menu_home,
            R.drawable.ic_menu_login,
            //R.drawable.ic_folder_inbox_holo_light,
            R.drawable.ic_menu_move_to_holo_light,
            R.drawable.ic_menu_myplaces,
            R.drawable.ic_menu_manage,
            R.drawable.ic_menu_info_details
    };

    public static SlidingMenuFragment newInstance() {
        SlidingMenuFragment f = new SlidingMenuFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt(KEY_POSITION, 0);
        }
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.menu_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mAdapter = new MenuAdapter(getActivity(), TABS);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(mAdapter);
        getListView().setItemChecked(mCurCheckPosition, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_POSITION, mCurCheckPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        if (position != 3 && position != 4 && position != 5) {
            mCurCheckPosition = position;
            //getListView().setItemChecked(position, true);
        }
        mAdapter.notifyDataSetChanged();
        if (mCallbacks != null)
            mCallbacks.onCallback(position, mAdapter.getItem(position));
    }

    private class MenuAdapter extends ArrayAdapter<String> {
        final LayoutInflater mInflater;

        public MenuAdapter(Context context, String[] items) {
            super(context, 0, items);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.menu_list_item, null);
            }
            CheckableFrameLayout layout = (CheckableFrameLayout) convertView
                    .findViewById(android.R.id.checkbox);
            layout.setChecked(mCurCheckPosition == position);
            //layout.setChecked(getListView().isItemChecked(position));
            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            text.setText(TITLES[position]);
            text.setCompoundDrawablesWithIntrinsicBounds(ICONS[position], 0, 0, 0);
            return convertView;
        }
    }

}
