package cheng.app.nga.loader;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.ReadEntry;
import cheng.app.nga.fragment.TopicListFragment;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;
import cheng.app.nga.util.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TopicListLoader extends CustomLoader<ReadEntry> {
    static final String TAG = "TopicListLoader";
    Bundle mArgs;
    NGAApp mApp;
    String mResultCode;
    int mTotalPage;

    public TopicListLoader(Activity context, Bundle arg) {
        super(context);
        mArgs = arg;
        mApp = (NGAApp) context.getApplication();
    }

    @Override
    public List<ReadEntry> loadInBackground() {
        mTotalPage = 1;
        mResultCode = JsonUtil.RESULT_ERROR_UNKNOWN;
        StringBuilder sb = new StringBuilder(Configs.THREAD_URL);
        sb.append('?');
        int mode = mArgs.getInt(TopicListFragment.KEY_MODE, 0);
        int page = mArgs.getInt(TopicListFragment.KEY_PAGE, 1);
        int recommend = 0;
        int account = 0;
        if (mode == TopicListFragment.MODE_SEARCH) {
            int fid = mArgs.getInt(TopicListFragment.KEY_FID, 0);
            sb.append("fid=");
            sb.append(fid);
            String key = mArgs.getString(TopicListFragment.KEY_SEARCHKEY);
            if(!TextUtils.isEmpty(key)){
                try {
                    sb.append("&key=");
                    sb.append(URLEncoder.encode(key, "GBK"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else if (mode == TopicListFragment.MODE_FAVOR) {
            sb.append("favor=1");
            account = mArgs.getInt(TopicListFragment.KEY_NAVIGATION, 0);
        } else {
            recommend = mArgs.getInt(TopicListFragment.KEY_NAVIGATION, 0);
            int fid = mArgs.getInt(TopicListFragment.KEY_FID, 0);
            sb.append("fid=");
            sb.append(fid);
        }
        sb.append("&page=");
        sb.append(page);
        sb.append(Configs.LITE);
        if (recommend == 1) {
            sb.append(Configs.RECOMMEND1);
        } else if (recommend == 2) {
            sb.append(Configs.RECOMMEND2);
        }
        String cookie = mApp.getCookieString(account);
        Log.d(TAG,"url:"+sb.toString());
        Log.d(TAG,"cookie:"+cookie);
        String result = HttpUtil.httpGet(sb.toString(), cookie, null, 5000);
        List<ReadEntry> list = new ArrayList<ReadEntry>();
        int[] r = new int[]{1,1}; 
        mResultCode = JsonUtil.parseTopicList(result, r, list);
        mTotalPage = r[0] / 35;
        return list;
    }

    public String getResultCode() {
        return mResultCode;
    }

    public int getTotalPage() {
        return mTotalPage;
    }
}

