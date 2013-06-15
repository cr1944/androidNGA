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

public class AccountsAdapter extends CursorAdapter {
    final LayoutInflater mInflater;

    public AccountsAdapter(Context context, Cursor c) {
        super(context, c, false);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mInflater.inflate(android.R.layout.simple_list_item_1, null);
        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {
        TextView text1 = (TextView) v;
        String name = c.getString(c.getColumnIndex(AccountColumns.NAME));
        try {
            text1.setText(URLDecoder.decode(name, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_contact_picture, 0, 0, 0);
    }

}

