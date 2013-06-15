package cheng.app.nga.loader;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cheng.app.nga.content.BlockEntry;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.fragment.BlockFragment;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;
import cheng.app.nga.util.JsonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlockLoader extends CustomLoader<BlockEntry> {
    static final String TAG = "BlockLoader";
    static final String URL = "?func=message&act=list_block";
    Bundle mArgs;
    NGAApp mApp;
    String mResult;

    public BlockLoader(Activity context, Bundle arg) {
        super(context);
        mArgs = arg;
        mApp = (NGAApp) context.getApplication();
    }

    @Override
    public List<BlockEntry> loadInBackground() {
        mResult = JsonUtil.RESULT_ERROR_EMPTY;
        int account = mArgs.getInt(BlockFragment.KEY_ACCOUNT, 0);
        String cookie = mApp.getCookieString(account);
        String url = Configs.NUKE_URL + URL + "&rand=" + Math.floor(new Date().getTime() / 3000);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, url);
        String result = HttpUtil.httpGet(url, cookie, null, 5000);
        Log.d(TAG, "result:" + result);
        if (!TextUtils.isEmpty(result) && result.startsWith("window.script_muti_get_var_store=")) {
            if(!result.substring(33).equals("null")) {
                ArrayList<BlockEntry> list = new ArrayList<BlockEntry>();
                mResult = JsonUtil.parseBlock(result.substring(33), list);
                return list;
            }
        }
        return null;
    }

    public String getResult() {
        return mResult;
    }

}

