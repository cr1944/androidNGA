package cheng.app.nga.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.content.NgaBoard;
import cheng.app.nga.content.NgaCategory;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

import java.util.List;

public class SectionedAdapter extends SectionedBaseAdapter {
    final LayoutInflater mInflater;
    List<NgaCategory> mSections;

    public SectionedAdapter(Context context, List<NgaCategory> list) {
        mSections = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public NgaBoard getItem(int section, int position) {
        return mSections.get(section).boards.get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSectionCount() {
        return mSections.size();
    }

    @Override
    public int getCountForSection(int section) {
        return mSections.get(section).boards.size();
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.homepage_list_item, null);
        }
        ImageView icon = (ImageView) convertView.findViewById(android.R.id.icon);
        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
        NgaBoard board = mSections.get(section).boards.get(position);
        if (board.icon != 0)
            icon.setImageResource(board.icon);
        else
            icon.setImageResource(R.drawable.pdefault);
        text1.setText(board.title);
        if (!TextUtils.isEmpty(board.summary)) {
            text2.setVisibility(View.VISIBLE);
            text2.setText(board.summary);
        } else {
            text2.setVisibility(View.GONE);
            text2.setText("");
        }
        return convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.homepage_header_item, null);
        }
        TextView title = (TextView) convertView.findViewById(android.R.id.title);
        title.setText(mSections.get(section).title);
        return convertView;
    }
}
