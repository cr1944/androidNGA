
package cheng.app.nga.task;

import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import cheng.app.nga.R;
import cheng.app.nga.activity.ReplyActivity;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.PostEntry;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;

public class PostTask extends ProgressTask<PostEntry, String, ReplyActivity> {
    NGAApp mApp;
    static final String TAG = "PostTask";
    static final String result_start_tag = "<span style='color:#aaa'>&gt;</span>";
    static final String result_end_tag = "<br/>";

    public PostTask(ReplyActivity target, int text) {
        super(target, text);
        mApp = (NGAApp) target.getApplication();
    }

    @Override
    protected String doInBackground(ReplyActivity target, PostEntry... params) {
        PostEntry entry = params[0];
        String body = entry.buildPostBody();
        String cookie = mApp.getCookieString(entry.account);
        InputStream input = null;
        HttpURLConnection conn = HttpUtil.httpPost(Configs.REPLY_URL, cookie, null, body);
        try {
            if (conn != null)
                input = conn.getInputStream();
            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                return getReplyResult(html);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fail";
    }

    private String getReplyResult(String html) {
        int start = html.indexOf(result_start_tag);
        if (start == -1)
            return "fail";
        start += result_start_tag.length();
        int end = html.indexOf(result_end_tag, start);
        if (start == -1)
            return "fail";
        return html.substring(start, end);
    }

    @Override
    protected void onPostExecute(ReplyActivity target, String result) {
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
