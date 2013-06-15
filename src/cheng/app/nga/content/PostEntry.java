
package cheng.app.nga.content;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostEntry {
    public int account = 0;
    public int step = 2;
    public int pid = 0;
    public String action = "new";
    public int fid = 414;
    public int tid = 0;
    public String _ff = "";
    public String attachments = "";
    public String attachments_check = "";
    public String force_topic_key = "";
    public int filter_key = 1;
    public String bit_data = "";
    public int post_icon;
    public String post_subject;
    public String post_content;
    public boolean hidden;
    public boolean self_reply;
    //public String mention;
    public String checkkey = "";
    public String tail;

    public String buildPostBody() {
        StringBuilder sb = new StringBuilder();
        sb.append("step=");
        sb.append(step);
        sb.append("&pid=");
        if (pid != 0) {
            sb.append(pid);
        }
        sb.append("&action=");
        sb.append(action);
        sb.append("&fid=");
        if (!action.equals("modify")) {
            sb.append(fid);
        }
        sb.append("&tid=");
        if (tid != 0) {
        sb.append(tid);
        }
        sb.append("&_ff=");
        sb.append(_ff);
        sb.append("&attachments=");
        sb.append(attachments);
        sb.append("&attachments_check=");
        sb.append(attachments_check);
        sb.append("&force_topic_key=");
        sb.append(force_topic_key);
        sb.append("&filter_key=");
        sb.append(filter_key);
        sb.append("&post_subject=");
        try {
            if (!TextUtils.isEmpty(post_subject))
                sb.append(URLEncoder.encode(post_subject, "GBK"));
            sb.append("&post_content=");
            if (!TextUtils.isEmpty(post_content)) {
                sb.append(URLEncoder.encode(post_content, "GBK"));
                if (!TextUtils.isEmpty(tail))
                    sb.append(URLEncoder.encode(tail, "GBK"));
                Pattern p = Pattern.compile("\\[@.{2,30}?\\]");
                Matcher m = p.matcher(post_content);
                StringBuilder mention = new StringBuilder();
                while (m.find()) {
                    mention.append('\t');
                    mention.append(m.group().substring(2, m.group().length() - 1));
                }
                if (!TextUtils.isEmpty(mention)) {
                    mention.deleteCharAt(0);
                    sb.append("&mention=");
                    sb.append(URLEncoder.encode(mention.toString(), "GBK"));
                }
            }
//            if (!TextUtils.isEmpty(mention)) {
//                sb.append("&mention=");
//                sb.append(URLEncoder.encode(mention, "GBK"));
//            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append("&checkkey=");
        sb.append(checkkey);
        return sb.toString();
    }

}
