
package cheng.app.nga.content;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CommentEntry implements Parcelable {
    public int account = 0;
    public int pid = 0;
    public int tid = 0;
    public String comment;

    public String buildBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("&pid=");
        if (pid != 0) {
            sb.append(pid);
        }
        sb.append("&tid=");
        if (tid != 0) {
        sb.append(tid);
        }
        sb.append("&info=");
        try {
            sb.append(URLEncoder.encode(comment,"GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(account);
        dest.writeInt(pid);
        dest.writeInt(tid);
        dest.writeString(comment);
    }

    public void readFromParcel(Parcel in) {
        account = in.readInt();
        pid = in.readInt();
        tid = in.readInt();
        comment = in.readString();
    }

}
