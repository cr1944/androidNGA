
package cheng.app.nga.task;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import cheng.app.nga.R;
import cheng.app.nga.content.CommentEntry;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;

public class CommentTask extends ProgressTask<CommentEntry, String, Activity> {
    static final String TAG = "CommentTask";
    NGAApp mApp;

    public CommentTask(Activity target, int text) {
        super(target, text);
        mApp = (NGAApp) target.getApplication();
    }

    @Override
    protected String doInBackground(Activity target, CommentEntry... params) {
        CommentEntry entry = params[0];
        String body = entry.buildBody();
        String cookie = mApp.getCookieString(entry.account);
        InputStream input = null;
        HttpURLConnection conn = HttpUtil.httpPost(Configs.NUKE_URL + Configs.COMMENT, cookie,
                null, body);
        try {
            if (conn != null)
                input = conn.getInputStream();
            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                return getPostResult(html);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    private String getPostResult(String html) {
        Log.d(TAG, "getPostResult:" + html);
        final String startTag = "<script>window.alert(\"";
        final String EndTag = "\")</script>";
        String result = "fail";
        if (TextUtils.isEmpty(html)) {
            return result;
        }
        int start = html.indexOf(startTag);
        if (start != -1) {
            int end = html.indexOf(EndTag, start);
            if (end > start) {
                return html.substring(start + startTag.length(), end);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Activity target, String result) {
        if (target != null && !target.isFinishing()) {
            if (result.equals("fail")) {
                Toast.makeText(target, R.string.post_fail,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(target, result,
                        Toast.LENGTH_SHORT).show();
                target.finish();
            }
        }
        super.onPostExecute(target, result);
    }
}
