package cheng.app.nga.content;

import org.json.JSONObject;

import android.util.SparseArray;

import java.util.List;

public class ThreadEntry {
    public SparseArray<ThreadUsersEntry> __U;
    public SparseArray<GroupEntry> __GROUPS;
    public JSONObject __MEDALS;
    public JSONObject __REPUTATIONS;
    public List<ThreadReplysEntry> __R;
    public ThreadInfoEntry __T;
    public JSONObject __F;
    public int __R__ROWS;
    public String __MESSAGE;

    public static class GroupEntry {
        public String name;
        public int bit;
    }
}
