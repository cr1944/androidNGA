package cheng.app.nga.task;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import cheng.app.nga.R;
import cheng.app.nga.activity.MainActivity;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.HttpUtil;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class NewAccountTask extends ProgressTask<String, Boolean, MainActivity> {
    NGAApp mApp;

    public NewAccountTask(MainActivity target, int text) {
        super(target, text);
        mApp = (NGAApp) target.getApplication();
    }

    @Override
    protected Boolean doInBackground(MainActivity target, String... params) {
        try {
            StringBuilder sb = new StringBuilder("type=username&email=");
            String username = URLEncoder.encode(params[0],"utf-8");
            String password = URLEncoder.encode(params[1],"utf-8");
            sb.append(username);
            sb.append("&password=");
            sb.append(password);
            HttpURLConnection conn = HttpUtil.httpPost(Configs.LOGIN_URL, null, null, sb.toString());
            if (conn == null) {
                return false;
            }
            Map<String, List<String>> result = conn.getHeaderFields();
            conn.disconnect();
            if (result == null)
                return false;
            String uid = "";
            String email = "";
            String name = "";
            String sid = "";
            String location = "";
            for (String key : result.keySet()) {
                Log.i(TAG, "key:"+key);
                if (TextUtils.isEmpty(key))
                    continue;
                if (key.equalsIgnoreCase("set-cookie")) {
                    List<String> list = result.get(key);
                    for(String s : list) {
                        if (s.startsWith("_sid=")) {
                            sid = s.substring(5, s.indexOf(';'));
                        } else if (s.startsWith("_178c=")) {
                            String[] info = s.substring(6, s.indexOf(';')).split("%23");
                            uid = info[0];
                            if (info.length > 1)
                                email = info[1];
                            if (info.length > 2)
                                name = info[2];
                        }
                    }
                } else if (key.equalsIgnoreCase("Location")) {
                    List<String> list = result.get(key);
                    location = list.get(0);
                }
            }
            if (!TextUtils.isEmpty(sid) && !TextUtils.isEmpty(uid)
                    && location.indexOf("login_success&error=0") != -1) {
                Log.i(TAG, "uid =" + uid + ", sid=" + sid);
                mApp.addAccount(Integer.parseInt(uid), sid, name, email);
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(MainActivity target, Boolean result) {
        Toast.makeText(target, result ? R.string.login_success : R.string.login_fail,
                Toast.LENGTH_SHORT).show();
        if (!target.isFinishing()) {
            target.onAccountAdded();
        }
        super.onPostExecute(target, result);
    }
}
