package cheng.app.nga.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.content.NGASQLiteHelper.AccountColumns;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ActionAccountsAdapter extends CursorAdapter {
    final LayoutInflater mInflater;

    public ActionAccountsAdapter(Context context, Cursor c) {
        super(context, c, false);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.sherlock_spinner_item, parent, false);
        return v;
    }

    @Override
    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.sherlock_spinner_dropdown_item, parent, false);
        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {
        TextView text1 = (TextView) v.findViewById(android.R.id.text1);
        String name = c.getString(c.getColumnIndex(AccountColumns.NAME));
        try {
            name = URLDecoder.decode(name, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        text1.setText(name);
    }

}

