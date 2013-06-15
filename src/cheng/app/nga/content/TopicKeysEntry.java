
package cheng.app.nga.content;

import android.os.Parcel;
import android.os.Parcelable;

public class TopicKeysEntry implements Parcelable {
    public int top;
    public String key;
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(top);
        dest.writeString(key);
    }
    public void readFromParcel(Parcel in) {
        top = in.readInt();
        key = in.readString();
    }
    public TopicKeysEntry(int top, String key) {
        this.top = top;
        this.key = key;
    }
    public TopicKeysEntry(Parcel in) {
        readFromParcel(in);
    }
    public static final Creator<TopicKeysEntry> CREATOR = new Creator<TopicKeysEntry>() {
        public TopicKeysEntry createFromParcel(Parcel source) {
            return new TopicKeysEntry(source);
        }

        public TopicKeysEntry[] newArray(int size) {
            return new TopicKeysEntry[size];
        }
    };
}
