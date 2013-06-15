
package cheng.app.nga.util;

import cheng.app.nga.content.NGAApp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

public class UploadCookieCollector {
    private static final String LOG_TAG = "UploadCookieCollector";
    private Map<String, String> ConcernCookies = new HashMap<String, String>();
    static final String collectURL = "http://bbs.ngacn.cc/nuke.php";
    NGAApp mApp;

    public UploadCookieCollector(NGAApp app) {
        mApp = app;
        ConcernCookies.put("ngacn0comUserInfo=", "");
        ConcernCookies.put("ngacn0comUserInfoCheck=", "");
        ConcernCookies.put("ngacn0comInfoCheckTime=", "");
        ConcernCookies.put("ngaPassportUid=", "");
        ConcernCookies.put("ngaPassportUrlencodedUname=", "");
        ConcernCookies.put("ngaPassportCid=", "");
    }

    public String toString() {
        String ret = "";
        for (Map.Entry<String, String> entry : ConcernCookies.entrySet()) {
            ret = ret + entry.getKey() + entry.getValue() + "; ";
        }
        return ret;
    }

    public UploadCookieCollector StartCollect(int account) {
        String[] cookie = mApp.getCookie(account);
        ConcernCookies.put("ngaPassportUid=", cookie[0]);
        final String urlString = collectURL + "?func=login&uid=" + cookie[0]
                + "&cid=" + cookie[1];

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", HttpUtil.USER_AGENT);
            conn.connect();
            conn.getInputStream();
            if (conn != null) {
                Map<String, List<String>> result = conn.getHeaderFields();
                conn.disconnect();
                if (result != null) {
                    for (String key : result.keySet()) {
                        if (TextUtils.isEmpty(key))
                            continue;
                        if (key.equalsIgnoreCase("set-cookie")) {
                            List<String> list = result.get(key);
                            for(String s : list) {
                                Log.i(LOG_TAG, key + ":" + s);
                                UpdateCookie(s);
                            }
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    private void UpdateCookie(String cookieVal) {
        for (Map.Entry<String, String> entry : ConcernCookies.entrySet()) {
            int posStart = cookieVal.indexOf(entry.getKey());
            if (posStart != -1) {
                int posEnd = cookieVal.indexOf(';', posStart);
                if (posEnd == -1) {
                    posEnd = cookieVal.length();
                }
                final String newValue = cookieVal.substring(posStart + entry.getKey().length(),
                        posEnd);
                ConcernCookies.put(entry.getKey(), newValue);

            }
        }
    }

}
