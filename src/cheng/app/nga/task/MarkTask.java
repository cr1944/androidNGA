
package cheng.app.nga.task;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import cheng.app.nga.R;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class MarkTask extends ProgressTask<Integer, String, Activity> {
    static final String MARK = "func=topicfavor&action=add&raw=1&tid=";
    static final String DEL_MARK = "?func=topicfavor&action=del";
    NGAApp mApp;

    public MarkTask(Activity target, int text) {
        super(target, text);
        mApp = (NGAApp) target.getApplication();
    }

    @Override
    protected String doInBackground(Activity target, Integer... params) {
        int ifMark = params[0];
        String url;
        String body;
        if (ifMark == 1) {
            String array = "";
            for (int i = 1; i < params.length; i++) {
                array += "," + params[i];
            }
            url = Configs.NUKE_URL + DEL_MARK;
            body = "tidarray=" + array;
        } else {
            int tid = params[1];
            url = Configs.NUKE_URL;
            body = MARK + tid;
        }
        String cookie = mApp.getCookieString(0);
        Log.d(TAG, "body:" + body);
        InputStream input = null;
        HttpURLConnection conn = HttpUtil.httpPost(url, cookie, null, body);
        try {
            if (conn != null)
                input = conn.getInputStream();
            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                return html;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    @Override
    protected void onPostExecute(Activity target, String result) {
        super.onPostExecute(target, result);
        if (target != null && !target.isFinishing()) {
            if (result != null && !result.equals("fail")) {
                String success = target.getString(R.string.mark_success);
                if (result.contains(success)) {
                    Toast.makeText(target, success, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(target, R.string.mark_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
