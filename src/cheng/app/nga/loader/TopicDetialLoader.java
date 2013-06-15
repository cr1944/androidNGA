package cheng.app.nga.loader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.ThreadEntry;
import cheng.app.nga.fragment.TopicDetialFragment;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;
import cheng.app.nga.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class TopicDetialLoader extends CustomLoader<ThreadEntry> {
    static final String TAG = "TopicDetialLoader";
    Bundle mArgs;
    NGAApp mApp;
    String mResultCode;

    public TopicDetialLoader(Activity context, Bundle arg) {
        super(context);
        mArgs = arg;
        mApp = (NGAApp) context.getApplication();
    }

    public String getResultCode() {
        return mResultCode;
    }

    @Override
    public List<ThreadEntry> loadInBackground() {
        final long start = System.currentTimeMillis();
        mResultCode = JsonUtil.RESULT_ERROR_UNKNOWN;
        int tid = mArgs.getInt(TopicDetialFragment.KEY_TID, 0);
        int pid = mArgs.getInt(TopicDetialFragment.KEY_PID, 0);
        int page = mArgs.getInt(TopicDetialFragment.KEY_PAGE, 1);
        StringBuilder sb = new StringBuilder(Configs.READ_URL);
        sb.append('?');
        if (tid != 0) {
            sb.append("tid=");
            sb.append(tid);
        } else if (pid != 0) {
            sb.append("pid=");
            sb.append(pid);
        }
        sb.append("&page=");
        sb.append(page);
        sb.append(Configs.LITE);
        String cookie = mApp.getCookieString(0);
        Log.d(TAG,"url:"+sb.toString());
        Log.d(TAG,"cookie:"+cookie);
        String result = HttpUtil.httpGet(sb.toString(), cookie, null, 5000);
        List<ThreadEntry> list = new ArrayList<ThreadEntry>();
        mResultCode = JsonUtil.parseTopicDetial(result, list);
        final long time = System.currentTimeMillis() - start;
        if (time < 2000) {
            try {
                Thread.sleep(2000 - time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    
}

