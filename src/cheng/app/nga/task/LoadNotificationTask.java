package cheng.app.nga.task;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import cheng.app.nga.R;
import cheng.app.nga.activity.NotificationActivity;
import cheng.app.nga.content.MentionEntry;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;
import cheng.app.nga.util.JsonUtil;

import java.util.ArrayList;

public class LoadNotificationTask extends WeakAsyncTask<Integer, Void, ArrayList<MentionEntry>, Activity>{
    static final String TAG = "LoadNotificationTask";
    NGAApp mApp;
    String mResult;

    public LoadNotificationTask(Activity target) {
        super(target);
        mApp = (NGAApp) target.getApplication();
    }

    @Override
    protected ArrayList<MentionEntry> doInBackground(Activity target, Integer... params) {
        int account = params[0];
        mResult = JsonUtil.RESULT_ERROR_EMPTY;
        String cookie = mApp.getCookieString(account);
        String url = Configs.NUKE_URL + Configs.NOTIFY;
        Log.d(TAG, "url:" + url);
        String result = HttpUtil.httpGet(url, cookie, null, 5000);
        //String result = "window.script_muti_get_var_store={0:[{0:8,1:1831521,2:\"片总片总片总片总片总片总片总片总片总片总片总片总\",3:\"\",4:\"\",5:\"打自己黑枪专用楼层\",9:1339680840,6:5271811,7:91025529},{0:8,1:1831521,2:\"片总\",3:\"\",4:\"\",5:\"打自己黑枪专用楼层\",9:1339680888,6:5271811,7:91025564}]}";
        Log.d(TAG, "result:" + result);
        ArrayList<MentionEntry> list = new ArrayList<MentionEntry>();
        if (!TextUtils.isEmpty(result) && result.startsWith("window.script_muti_get_var_store=")) {
            if(!result.substring(33).equals("null")) {
                mResult = JsonUtil.parseMention(result.substring(33).replaceAll("(\\d+):", "\"$1\":"), list);
                return list;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Activity target, ArrayList<MentionEntry> result) {
        if (result != null && !result.isEmpty()) {
            NotificationManager nm = 
                    (NotificationManager)target.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification();
            CharSequence contentTitle = target.getString(R.string.app_name);
            CharSequence contentText;
            if (result.size() == 1)
                contentText = target.getString(R.string.noti_1, result.get(0)._FROM_UNAME);
            else
                contentText = target.getString(R.string.noti_2, result.get(0)._FROM_UNAME, result.size());
            notification.icon = R.drawable.ic_launcher;
            //notification.ledARGB = 0xffffeebb;
            notification.defaults = Notification.DEFAULT_ALL;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.tickerText = contentText;
            Intent intent = new Intent(target, NotificationActivity.class);
            intent.putParcelableArrayListExtra("notification", result);
            PendingIntent contentIntent = PendingIntent.getActivity(target, 0, intent, 0);
            notification.setLatestEventInfo(target, contentTitle, contentText, contentIntent);
            nm.notify(116381917, notification);
        }
    }
}
