
package cheng.app.nga.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.adapter.CommonArrayAdapter;

public class BbsCodeDialog extends AlertDialog implements OnClickListener, OnItemSelectedListener {
    static final String TAG = "BbsCodeDialog";
    TextView mInfo;
    Spinner mSpinner;
    EditText mEdit;
    Callback mCallback;
    int mType;
    int mSpinnerIndex;

    public interface Callback {
        public void onCallback(String value);
    }

    public void setCallback(Callback l) {
        mCallback = l;
    }

    public BbsCodeDialog(Context context, int type, Callback l) {
        super(context);
        mType = type;
        setCallback(l);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (which == DialogInterface.BUTTON_POSITIVE && mCallback != null) {
            String result = mEdit.getText().toString();
            switch (mType) {
                case 0:
                    mCallback.onCallback("[@" + result + "]");
                    break;
                case 1:
                    mCallback.onCallback("[quote]" + result + "[/quote]");
                    break;
                case 2:
                    String[] colors = getContext().getResources().getStringArray(R.array.text_color_value);
                    String color = colors[mSpinnerIndex];
                    mCallback.onCallback("[color=" + color +"]" + result + "[/color]");
                    break;
                case 3:
                    String[] sizes = getContext().getResources().getStringArray(R.array.text_size);
                    String size = sizes[mSpinnerIndex];
                    mCallback.onCallback("[size=" + size +"]" + result + "[/size]");
                    break;
                case 4:
                    String[] fonts = getContext().getResources().getStringArray(R.array.text_font);
                    String font = fonts[mSpinnerIndex];
                    mCallback.onCallback("[font=" + font +"]" + result + "[/font]");
                    break;
                case 5:
                    mCallback.onCallback("[b]" + result + "[/b]");
                    break;
                case 6:
                    mCallback.onCallback("[u]" + result + "[/u]");
                    break;
                case 7:
                    mCallback.onCallback("[i]" + result + "[/i]");
                    break;
                case 8:
                    mCallback.onCallback("[del]" + result + "[/del]");
                    break;
                case 9:
                    String[] aligns = getContext().getResources().getStringArray(R.array.text_align);
                    String align = aligns[mSpinnerIndex];
                    mCallback.onCallback("[align=" + align +"]" + result + "[/align]");
                    break;
                case 10:
                    mCallback.onCallback("[h]" + result + "[/h]");
                    break;
                case 11:
                    String[] textFloats = getContext().getResources().getStringArray(R.array.text_float);
                    String textFloat = textFloats[mSpinnerIndex];
                    mCallback.onCallback("[" + textFloat +"]" + result + "[/" + textFloat +"]");
                    break;
                case 12:
                    break;
                case 13:
                    mCallback.onCallback("[img]" + result + "[/img]");
                    break;
                case 14:
                    break;
                case 15:
                    mCallback.onCallback("[url]" + result + "[/url]");
                    break;
                case 16:
                    String[] codes = getContext().getResources().getStringArray(R.array.text_code);
                    String code = codes[mSpinnerIndex];
                    mCallback.onCallback("[code=" + code +"]" + result + "[/code]");
                    break;
                case 17:
                    mCallback.onCallback("[flash]" + result + "[/flash]");
                    break;
                case 18:
                    String[] dices = getContext().getResources().getStringArray(R.array.text_dice);
                    String dice = dices[mSpinnerIndex];
                    mCallback.onCallback("[dice]" + dice + "[/dice]");
                    break;
                case 19:
                    break;
                case 20:
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_bbscode, null);
        mInfo = (TextView) view.findViewById(android.R.id.text1);
        mSpinner = (Spinner) view.findViewById(R.id.bbscode_spinner);
        mEdit = (EditText) view.findViewById(android.R.id.edit);
        setView(view);
        setButton(DialogInterface.BUTTON_POSITIVE, 
                getContext().getText(android.R.string.ok), this);
        setButton(DialogInterface.BUTTON_NEGATIVE,
                getContext().getText(android.R.string.cancel), this);
        String[] titles = getContext().getResources().getStringArray(R.array.bbscodes);
        String[] hints = getContext().getResources().getStringArray(R.array.bbscodes_hint);
        setTitle(titles[mType]);
        mEdit.setHint(hints[mType]);
        initViews();
        super.onCreate(savedInstanceState);
    }

    private void initViews() {
        switch (mType) {
            case 2: {
                mSpinner.setVisibility(View.VISIBLE);
                mEdit.setVisibility(View.VISIBLE);
                TypedArray ar = getContext().getResources().obtainTypedArray(R.array.text_color);
                Integer[] colors = new Integer[ar.length()];
                for (int i = 0; i < ar.length(); i ++) {
                    colors[i] = ar.getColor(i, 0);
                }
                ColorAdapter adapter = new ColorAdapter(getContext(), colors);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(this);
            }
                break;
            case 3: {
                mSpinner.setVisibility(View.VISIBLE);
                mEdit.setVisibility(View.VISIBLE);
                String[] sizes = getContext().getResources().getStringArray(R.array.text_size);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, sizes);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(this);
            }
                break;
            case 4: {
                mSpinner.setVisibility(View.VISIBLE);
                mEdit.setVisibility(View.VISIBLE);
                String[] fonts = getContext().getResources().getStringArray(R.array.text_font);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, fonts);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(this);
            }
                break;
            case 9: {
                mSpinner.setVisibility(View.VISIBLE);
                mEdit.setVisibility(View.VISIBLE);
                String[] aligns = getContext().getResources().getStringArray(R.array.text_align);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, aligns);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(this);
            }
                break;
            case 11: {
                mSpinner.setVisibility(View.VISIBLE);
                mEdit.setVisibility(View.VISIBLE);
                String[] textFloats = getContext().getResources().getStringArray(R.array.text_float);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, textFloats);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(this);
            }
                break;
            case 16: {
                mSpinner.setVisibility(View.VISIBLE);
                mEdit.setVisibility(View.VISIBLE);
                String[] codes = getContext().getResources().getStringArray(R.array.text_code);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, codes);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(this);
            }
                break;
            case 18: {
                mSpinner.setVisibility(View.VISIBLE);
                String[] dices = getContext().getResources().getStringArray(R.array.text_dice);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, dices);
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(this);
            }
                break;
            default:
                mEdit.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mSpinnerIndex = arg2;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    class ColorAdapter extends CommonArrayAdapter<Integer> {

        public ColorAdapter(Context context, Integer[] objects) {
            super(context, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_bbs_text_color, parent, false);
            }
            View v = convertView.findViewById(R.id.text_color);
            int color = getItem(position);
            v.setBackgroundColor(color);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }

}
