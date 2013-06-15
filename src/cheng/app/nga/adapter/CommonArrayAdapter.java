package cheng.app.nga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

import java.util.List;

public abstract class CommonArrayAdapter<T> extends ArrayAdapter<T> {
    protected final LayoutInflater mInflater;

    public CommonArrayAdapter(Context context, List<T> list) {
        super(context, 0, list);
        mInflater = LayoutInflater.from(context);
    }

    public CommonArrayAdapter(Context context, T[] list) {
        super(context, 0, list);
        mInflater = LayoutInflater.from(context);
    }
}
