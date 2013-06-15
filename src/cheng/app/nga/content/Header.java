
package cheng.app.nga.content;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public final class Header implements Parcelable {
    public static final int HEADER_TYPE_NORMAL = 0;
    public static final int HEADER_TYPE_CATEGORY = 1;
    public static final int HEADER_TYPE_SWITCH = 2;
    public static final int HEADER_TYPE_COUNT = 3;
    public static final long HEADER_ID_UNDEFINED = -1;

    public long id = HEADER_ID_UNDEFINED;
    public int type;

    public int titleRes;

    public CharSequence title;

    public int summaryRes;

    public CharSequence summary;

    public int iconRes;

    public String fragment;

    public Bundle fragmentArguments;

    public Intent intent;

    public Bundle extras;

    public String preference;

    public boolean isChecked;

    public Header() {
        // Empty
    }

    public int getType() {
        return type;
    }

    public CharSequence getTitle(Resources res) {
        if (titleRes != 0) {
            return res.getText(titleRes);
        }
        return title;
    }

    public CharSequence getSummary(Resources res) {
        if (summaryRes != 0) {
            return res.getText(summaryRes);
        }
        return summary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(type);
        dest.writeInt(titleRes);
        TextUtils.writeToParcel(title, dest, flags);
        dest.writeInt(summaryRes);
        TextUtils.writeToParcel(summary, dest, flags);
        dest.writeInt(iconRes);
        dest.writeString(fragment);
        dest.writeBundle(fragmentArguments);
        if (intent != null) {
            dest.writeInt(1);
            intent.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
        dest.writeBundle(extras);
        dest.writeString(preference);
        dest.writeInt(isChecked ? 1 : 0);
    }

    public void readFromParcel(Parcel in) {
        id = in.readLong();
        type = in.readInt();
        titleRes = in.readInt();
        title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        summaryRes = in.readInt();
        summary = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        iconRes = in.readInt();
        fragment = in.readString();
        fragmentArguments = in.readBundle();
        if (in.readInt() != 0) {
            intent = Intent.CREATOR.createFromParcel(in);
        }
        extras = in.readBundle();
        preference = in.readString();
        isChecked = in.readInt() == 1 ? true : false;
    }

    Header(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<Header> CREATOR = new Creator<Header>() {
        public Header createFromParcel(Parcel source) {
            return new Header(source);
        }

        public Header[] newArray(int size) {
            return new Header[size];
        }
    };
}
