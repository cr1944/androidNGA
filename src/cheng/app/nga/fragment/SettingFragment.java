
package cheng.app.nga.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.content.Header;
import com.actionbarsherlock.app.SherlockListFragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends SherlockListFragment {
    static final String TAG = "SettingFragment";
    HeaderAdapter mAdapter;
    private final ArrayList<Header> mHeaders = new ArrayList<Header>();

    public void loadHeadersFromResource(int resid, List<Header> target) {
        XmlResourceParser parser = null;
        try {
            parser = getResources().getXml(resid);
            AttributeSet attrs = Xml.asAttributeSet(parser);

            int type;
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && type != XmlPullParser.START_TAG) {
                // Parse next until start tag is found
            }

            String nodeName = parser.getName();
            if (!"preference-headers".equals(nodeName)) {
                throw new RuntimeException(
                        "XML document must start with <preference-headers> tag; found" + nodeName
                                + " at " + parser.getPositionDescription());
            }

            Bundle curBundle = null;

            final int outerDepth = parser.getDepth();
            while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                    && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
                if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                    continue;
                }

                nodeName = parser.getName();
                if ("header".equals(nodeName)) {
                    Header header = new Header();

                    TypedArray sa = getResources().obtainAttributes(attrs,
                            R.styleable.preferenceHeader);
                    header.id = sa.getResourceId(R.styleable.preferenceHeader_android_id,
                            (int) Header.HEADER_ID_UNDEFINED);
                    header.type = sa.getInteger(R.styleable.preferenceHeader_type,
                            Header.HEADER_TYPE_NORMAL);
                    TypedValue tv = sa.peekValue(R.styleable.preferenceHeader_android_title);
                    if (tv != null && tv.type == TypedValue.TYPE_STRING) {
                        if (tv.resourceId != 0) {
                            header.titleRes = tv.resourceId;
                        } else {
                            header.title = tv.string;
                        }
                    }
                    tv = sa.peekValue(R.styleable.preferenceHeader_android_summary);
                    if (tv != null && tv.type == TypedValue.TYPE_STRING) {
                        if (tv.resourceId != 0) {
                            header.summaryRes = tv.resourceId;
                        } else {
                            header.summary = tv.string;
                        }
                    }
                    header.iconRes = sa.getResourceId(R.styleable.preferenceHeader_android_icon, 0);
                    header.fragment = sa.getString(R.styleable.preferenceHeader_fragment);
                    header.preference = sa
                            .getString(R.styleable.preferenceHeader_switcherPreference);
                    header.isChecked = sa.getBoolean(
                            R.styleable.preferenceHeader_switcherValue, false);
                    sa.recycle();

                    if (curBundle == null) {
                        curBundle = new Bundle();
                    }

                    final int innerDepth = parser.getDepth();
                    while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                            && (type != XmlPullParser.END_TAG || parser.getDepth() > innerDepth)) {
                        if (type == XmlPullParser.END_TAG || type == XmlPullParser.TEXT) {
                            continue;
                        }

                        String innerNodeName = parser.getName();
                        if (innerNodeName.equals("extra")) {
                            getResources().parseBundleExtra("extra", attrs, curBundle);
                            skipCurrentTag(parser);

                        } else if (innerNodeName.equals("intent")) {
                            header.intent = Intent.parseIntent(getResources(), parser, attrs);

                        } else {
                            skipCurrentTag(parser);
                        }
                    }

                    if (curBundle.size() > 0) {
                        header.fragmentArguments = curBundle;
                        curBundle = null;
                    }

                    target.add(header);
                } else {
                    skipCurrentTag(parser);
                }
            }

        } catch (XmlPullParserException e) {
            throw new RuntimeException("Error parsing headers", e);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing headers", e);
        } finally {
            if (parser != null)
                parser.close();
        }

    }

    public static void skipCurrentTag(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        int outerDepth = parser.getDepth();
        int type;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
        }
    }

    public static SettingFragment newInstance() {
        SettingFragment f = new SettingFragment();
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHeaders.isEmpty()) {
            loadHeadersFromResource(R.xml.settings_headers, mHeaders);
            mAdapter = new HeaderAdapter(getActivity(), mHeaders);
            setListAdapter(mAdapter);
        }
        getListView().setBackgroundColor(getResources().getColor(R.color.transparent));
        getListView().setCacheColorHint(getResources().getColor(R.color.transparent));
   }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int type = mAdapter.getItemViewType(position);
        Header item = mAdapter.getItem(position);
        switch (type) {
            case Header.HEADER_TYPE_NORMAL:
                if (!TextUtils.isEmpty(item.fragment)) {
                        switchToHeader(item);
                } else if (item.intent != null) {
                    startActivity(item.intent);
                }
                break;
            case Header.HEADER_TYPE_SWITCH:
                mAdapter.toggle(position);
                break;
        }
    }

    private void switchToHeader(Header header) {
        Fragment f = Fragment.instantiate(getActivity(), header.fragment, header.fragmentArguments);
        if (f instanceof DialogFragment) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            ((DialogFragment) f).show(fm, header.fragment);
        } else {
            Log.e(TAG, "the fragment should be a DialogFragment!");
        }
    }

    private static class HeaderAdapter extends ArrayAdapter<Header> {
        private SharedPreferences mPref;

        private static class HeaderViewHolder {
            ImageView icon;
            TextView title;
            TextView summary;
            CheckBox switch_;
        }

        private LayoutInflater mInflater;

        @Override
        public int getItemViewType(int position) {
            Header header = getItem(position);
            return header.type;
        }

        public void toggle(int position) {
            Header header = getItem(position);
            Editor editor = mPref.edit();
            editor.putBoolean(header.preference, !header.isChecked);
            editor.commit();
            notifyDataSetChanged();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false; // because of categories
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) != Header.HEADER_TYPE_CATEGORY;
        }

        @Override
        public int getViewTypeCount() {
            return Header.HEADER_TYPE_COUNT;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public HeaderAdapter(Context context, List<Header> objects) {
            super(context, 0, objects);
            mPref = PreferenceManager.getDefaultSharedPreferences(context);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            Header header = getItem(position);
            int headerType = header.type;
            View view = null;

            if (convertView == null) {
                holder = new HeaderViewHolder();
                switch (headerType) {
                    case Header.HEADER_TYPE_CATEGORY:
                        view = new TextView(getContext(), null,
                                android.R.attr.listSeparatorTextViewStyle);
                        holder.title = (TextView) view;
                        break;

                    case Header.HEADER_TYPE_SWITCH:
                        view = mInflater.inflate(R.layout.preference_header_switch_item, parent,
                                false);
                        holder.icon = (ImageView) view.findViewById(android.R.id.icon);
                        holder.title = (TextView) view.findViewById(android.R.id.title);
                        holder.summary = (TextView) view.findViewById(android.R.id.summary);
                        holder.switch_ = (CheckBox) view.findViewById(android.R.id.checkbox);
                        break;

                    case Header.HEADER_TYPE_NORMAL:
                        view = mInflater.inflate(R.layout.preference_header_item, parent, false);
                        holder.icon = (ImageView) view.findViewById(android.R.id.icon);
                        holder.title = (TextView) view.findViewById(android.R.id.title);
                        holder.summary = (TextView) view.findViewById(android.R.id.summary);
                        break;
                }
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (HeaderViewHolder) view.getTag();
            }

            // All view fields must be updated every time, because the view may
            // be recycled
            switch (headerType) {
                case Header.HEADER_TYPE_CATEGORY:
                    holder.title.setText(header.getTitle(getContext().getResources()));
                    break;

                case Header.HEADER_TYPE_SWITCH:
                    // No break, fall through on purpose to update common fields
                    header.isChecked = mPref.getBoolean(header.preference, header.isChecked);
                    holder.switch_.setChecked(header.isChecked);

                case Header.HEADER_TYPE_NORMAL:
                    holder.icon.setImageResource(header.iconRes);
                    holder.title.setText(header.getTitle(getContext().getResources()));
                    CharSequence summary = header.getSummary(getContext().getResources());
                    if (!TextUtils.isEmpty(summary)) {
                        holder.summary.setVisibility(View.VISIBLE);
                        holder.summary.setText(summary);
                    } else {
                        holder.summary.setVisibility(View.GONE);
                    }
                    break;
            }

            return view;
        }

    }

}
