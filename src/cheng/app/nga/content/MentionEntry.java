package cheng.app.nga.content;

import android.os.Parcel;
import android.os.Parcelable;

public class MentionEntry implements Parcelable{
    public int _TYPE; //0
    public int _FROM_UID; //1,
    public String _FROM_UNAME; //2
    public int _TO_UID; //3
    public String _TO_UNAME; //4
    public String _TEXT; //5
    public int _ABOUT_ID; //6
    public int _ABOUT_ID_2; //7
    public int _ABOUT_ID_3; //8
    public long _TIME; //9
    public int _ABOUT_ID_4; //10

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_TYPE);
        dest.writeInt(_FROM_UID);
        dest.writeString(_FROM_UNAME);
        dest.writeInt(_TO_UID);
        dest.writeString(_TO_UNAME);
        dest.writeString(_TEXT);
        dest.writeInt(_ABOUT_ID);
        dest.writeInt(_ABOUT_ID_2);
        dest.writeInt(_ABOUT_ID_3);
        dest.writeLong(_TIME);
        dest.writeLong(_ABOUT_ID_4);
    }

    public void readFromParcel(Parcel in) {
        _TYPE = in.readInt();
        _FROM_UID = in.readInt();
        _FROM_UNAME = in.readString();
        _TO_UID = in.readInt();
        _TO_UNAME = in.readString();
        _TEXT = in.readString();
        _ABOUT_ID = in.readInt();
        _ABOUT_ID_2 = in.readInt();
        _ABOUT_ID_3 = in.readInt();
        _TIME = in.readLong();
        _ABOUT_ID_4 = in.readInt();
    }

    public MentionEntry() {
    }

    MentionEntry(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<MentionEntry> CREATOR = new Creator<MentionEntry>() {
        public MentionEntry createFromParcel(Parcel source) {
            return new MentionEntry(source);
        }

        public MentionEntry[] newArray(int size) {
            return new MentionEntry[size];
        }
    };
}
