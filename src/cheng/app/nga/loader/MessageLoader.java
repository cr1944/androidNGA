package cheng.app.nga.loader;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cheng.app.nga.content.MessageEntry;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.fragment.BlockFragment;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;
import cheng.app.nga.util.JsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageLoader extends CustomLoader<MessageEntry> {
    static final String TAG = "MessageLoader";
    static final String URL = "?func=message&act=list&page=";
    //static final String URL = "http://interface.i.178.com/?_app=sms&_controller=index&_action=check_new&rtype=2&uid=";
    Bundle mArgs;
    NGAApp mApp;
    String mResult;

    public MessageLoader(Activity context, Bundle arg) {
        super(context);
        mArgs = arg;
        mApp = (NGAApp) context.getApplication();
    }

    @Override
    public List<MessageEntry> loadInBackground() {
        mResult = JsonUtil.RESULT_ERROR_EMPTY;
        int account = mArgs.getInt(BlockFragment.KEY_ACCOUNT, 0);
        String cookie = mApp.getCookieString(account);
        int page = 1;
        String url = Configs.NUKE_URL + URL + page + "&rand=" + Math.floor(new Date().getTime() / 3);
        Log.d(TAG, url);
        String result = HttpUtil.httpGet(url, cookie, null, 5000);
        Log.d(TAG, "result:" + result);
        if (!TextUtils.isEmpty(result) && result.startsWith("window.script_muti_get_var_store=")) {
            if(!result.substring(33).equals("null")) {
                ArrayList<MessageEntry> list = new ArrayList<MessageEntry>();
                mResult = JsonUtil.parseMessage(result.substring(33), list);
                return list;
            }
        }
        return null;
    }

    public String getResult() {
        return mResult;
    }

}

