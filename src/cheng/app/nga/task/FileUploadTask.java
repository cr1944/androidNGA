
package cheng.app.nga.task;

import cheng.app.nga.activity.ReplyActivity;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.util.UploadCookieCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class FileUploadTask extends ProgressTask<String, String, ReplyActivity> {
    private static final String TAG = FileUploadTask.class.getSimpleName();
    private static final String BOUNDARY = "-----------------------------7db1c5232222b";
    private static final String ATTACHMENT_SERVER = "http://img6.ngacn.cc:8080/attach.php";

    private int filesize;

    private String filename;
    private String utfFilename;
    private String contentType;

    static final private String attachmentsStartFlag = "namedItem('attachments').value+='";
    static final private String attachmentsCheckStartFlag = "namedItem('attachments_check').value+='";
    static final private String attachmentsEndFlag = "\\t";

    static final private String picUrlStartTag = "addUploadedAttach('";
    static final private String picUrlEndTag = "'";
    NGAApp mApp;
    int mAccount;

    public FileUploadTask(ReplyActivity target, int text, int account) {
        super(target, text);
        mAccount = account;
        mApp = (NGAApp) target.getApplication();
    }

    @Override
    protected void onPostExecute(ReplyActivity target, String result) {
        super.onPostExecute(target, result);
        Log.e(TAG, "onPostExecute:"+result);
        if (target == null || target.isFinishing())
            return;
        do {
            if (TextUtils.isEmpty(result))
                break;
            int start = result.indexOf(attachmentsStartFlag);
            if (start == -1)
                break;
            start = start + attachmentsStartFlag.length();
            int end = result.indexOf(attachmentsEndFlag, start);
            if (end == -1)
                break;
            String attachments = result.substring(start, end);
            try {
                attachments = URLEncoder.encode(attachments + "\t", "utf-8");
            } catch (UnsupportedEncodingException e1) {
                Log.e(TAG, "invalid attachments string" + attachments);
            }

            start = result.indexOf(attachmentsCheckStartFlag);
            if (start == -1)
                break;
            start = start + attachmentsCheckStartFlag.length();
            end = result.indexOf(attachmentsEndFlag, start);
            if (end == -1)
                break;
            String attachmentsCheck = result.substring(start, end);
            try {
                attachmentsCheck = URLEncoder.encode(attachmentsCheck + "\t", "utf-8");
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "invalid attachmentsCheck string" + attachmentsCheck);
                break;
            }

            start = result.indexOf(picUrlStartTag);
            if (start == -1)
                break;
            start = start + picUrlStartTag.length();
            end = result.indexOf(picUrlEndTag, start);
            if (end == -1)
                break;
            String picUrl = result.substring(start, end);
            target.onUploaded(attachments, attachmentsCheck, picUrl);
            return;
        } while (false);
        int a = result.indexOf("window.alert(");
        if (a != -1) {
            int b = result.indexOf(")", a);
            Toast.makeText(target, result.substring(a + 13, b), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected String doInBackground(ReplyActivity target, String... params) {
        String result = null;
        String path = params[0];
        this.filename = params[1];
        this.utfFilename = URLEncoder.encode(filename);
        this.contentType = params[2];
        Log.e(TAG, "utfFilename:" + utfFilename);
        Log.e(TAG, "contentType:" + contentType);
        InputStream is = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                Log.e(TAG, "doInBackground: file not exist");
                return null;
            } else {
                is = new FileInputStream(path);
                filesize = is.available();
                if (filesize >= 1024 * 1024) {
//                    byte[] img = ImageUtil.fitImageToUpload(cr.openInputStream(uri),
//                            cr.openInputStream(uri));
//                    contentType = "image/png";
//                    filesize = img.length;
//                    is = new ByteArrayInputStream(img);
                }
            }
        } catch (FileNotFoundException ex) {
            Log.w(TAG, ex);
            return null;
        } catch (IOException ex) {
            Log.w(TAG, ex);
            return null;
        }

        final byte header[] = buildHeader().getBytes();
        final byte tail[] = buildTail().getBytes();

        final String cookie = new UploadCookieCollector(mApp).StartCollect(mAccount).toString();
        URL url;
        try {
            url = new URL(ATTACHMENT_SERVER);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Length",
                    String.valueOf(header.length + filesize + tail.length));
            conn.setRequestProperty("Accept-Charset", "GBK");
            conn.setRequestProperty("Cookie", cookie);
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();

            byte[] buf = new byte[1024];
            int len;
            out.write(header);
            while ((len = is.read(buf)) != -1)
                out.write(buf, 0, len);
            out.write(tail);
            out.close();
            is.close();
            InputStream httpInputStream = conn.getInputStream();
            result = IOUtils.toString(httpInputStream, "gbk");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String buildHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data; name=\"attachment_file1\"; filename=\"");
        sb.append(filename);
        sb.append("\"\r\nContent-Type: ");
        sb.append(contentType);
        sb.append("\r\n\r\n");

        return sb.toString();

    }

    private String buildTail() {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"attachment_file1_watermark\"\r\n\r\n\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"attachment_file1_thumb\"\r\n\r\n1\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"attachment_file1_dscp\"\r\n\r\n");
        sb.append(filename + "\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"attachment_file1_url_utf8_name\"\r\n\r\n");
        sb.append(utfFilename + "\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"func\"\r\n\r\nupload\r\n");

        sb.append("--" + BOUNDARY + "\r\n");
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"fid\"\r\n\r\n-7\r\n");

        sb.append("--" + BOUNDARY + "--\r\n");

        return sb.toString();
    }

}
