
package cheng.app.nga.util;

import org.apache.commons.io.IOUtils;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class HttpUtil {
    static final String TAG = "HttpUtil";
    static final String USER_AGENT = "AndroidNga/1.0";

    public synchronized static String httpGet(String urlString, String cookie, String host, int timeout) {
        InputStream is = null;
        HostnameVerifier allHostsValid = new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                // TODO Auto-generated method stub
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            if (!TextUtils.isEmpty(cookie))
                conn.setRequestProperty("Cookie", cookie);
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept-Charset", "GBK");
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            if (!TextUtils.isEmpty(host)) {
                conn.setRequestProperty("Host", host);
            }
            if (timeout > 0) {
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout * 2);
            } else {
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
            }

            conn.connect();
            is = conn.getInputStream();
            if ("gzip".equals(conn.getHeaderField("Content-Encoding")))
                is = new GZIPInputStream(is);
            String encoding = "GBK";
            return IOUtils.toString(is, encoding);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public static HttpURLConnection httpPost(String urlString, String cookie, String host, String body) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            if (cookie != null)
                conn.setRequestProperty("Cookie", cookie);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(body.length()));
            conn.setRequestProperty("Accept-Charset", "GBK");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.connect();

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(body);
            out.flush();
            out.close();
            Log.e(TAG, "getResponseMessage:"+conn.getResponseMessage());
            return conn;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }

    public static byte[] getBytesFromUrl(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        if (conn.getResponseCode() == 200) {
            InputStream inStream = conn.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            return outSteam.toByteArray();
        }
        return null;
    }
}
