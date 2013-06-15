
package cheng.app.nga.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;

import cheng.app.nga.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    static final String TAG = "TextUtil";
    public static SpannableStringBuilder buildNgaTitle(Context context, String src,
            String titlefont, int quote_from, int digest, int type) {
         int[] attrs = new int[] {
            R.attr.text_color
        };
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int textColor = ta.getColor(0, context.getResources().getColor(R.color.list_text_color));
        int textStyle = Typeface.NORMAL;
        if (!TextUtils.isEmpty(titlefont)) {
            String[] titlefonts = titlefont.split("~");
            if (titlefonts.length > 0) {
                if ("gray".equals(titlefonts[0])) {
                    textColor = context.getResources().getColor(R.color.gray);
                } else if ("red".equals(titlefonts[0])) {
                    textColor = context.getResources().getColor(R.color.red);
                } else if ("green".equals(titlefonts[0])) {
                    textColor = context.getResources().getColor(R.color.green);
                } else if ("blue".equals(titlefonts[0])) {
                    textColor = context.getResources().getColor(R.color.blue);
                } else if ("orange".equals(titlefonts[0])) {
                    textColor = context.getResources().getColor(R.color.orange);
                } else if ("silver".equals(titlefonts[0])) {
                    textColor = context.getResources().getColor(R.color.silver);
                }
            }
            if (titlefonts.length > 1) {
                if (!TextUtils.isEmpty(titlefonts[1])) {
                    textStyle |= Typeface.BOLD;
                }
            }
            if (titlefonts.length > 2) {
                if (!TextUtils.isEmpty(titlefonts[2])) {
                    textStyle |= Typeface.ITALIC;
                }
            }
            if (titlefonts.length > 3) {
                if (!TextUtils.isEmpty(titlefonts[3])) {
                    // TODO: underline not support yet
                }
            }
        }
        String text = StringEscapeUtils.unescapeHtml3(src);
        int start = 0;
        int end = text.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(new StyleSpan(textStyle), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(textColor), start,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Pattern p = Pattern.compile("\\[([^\\[]+?)\\]");
        Matcher m = p.matcher(text);
        while (m.find()) {
            builder.setSpan(
                    new ForegroundColorSpan(context.getResources().getColor(R.color.silver_hover)),
                    m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (quote_from != 0) {
            String s = context.getString(R.string.quote_from);
            start = end;
            end = end + s.length();
            builder.append(s);
            builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (digest != 0) {
            String s = context.getString(R.string.digest);
            start = end;
            end = end + s.length();
            builder.append(s);
            builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if ((type & 1024) != 0) {
            String s = context.getString(R.string.locked);
            start = end;
            end = end + s.length();
            builder.append(s);
            builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if ((type & 2) != 0) {
            String s = context.getString(R.string.hide);
            start = end;
            end = end + s.length();
            builder.append(s);
            builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    @Deprecated
    public static String filterEntities(String src) {
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile("&[a-zA-Z]*;|&#[0-9]*;");
        Matcher m = p.matcher(src);
        int pos1 = 0;
        while (m.find(pos1)) {
            int pos2 = m.start();
            sb.append(src.substring(pos1, pos2));
            String entity = m.group().toLowerCase();
            if ("&nbsp;".equals(entity) || "&#160;".equals(entity)) {
                sb.append((char) 160);
            } else if ("&lt;".equals(entity) || "&#60;".equals(entity)) {
                sb.append((char) 60);
            } else if ("&gt;".equals(entity) || "&#62;".equals(entity)) {
                sb.append((char) 62);
            } else if ("&amp;".equals(entity) || "&#38;".equals(entity)) {
                sb.append((char) 38);
            } else if ("&quot;".equals(entity) || "&#34;".equals(entity)) {
                sb.append((char) 34);
            } else if ("&apos;".equals(entity) || "&#39;".equals(entity)) {
                sb.append((char) 39);
            } else if ("&cent;".equals(entity) || "&#162;".equals(entity)) {
                sb.append((char) 0xa2);
            } else if ("&pound;".equals(entity) || "&#163;".equals(entity)) {
                sb.append((char) 0xa3);
            } else if ("&yen;".equals(entity) || "&#165;".equals(entity)) {
                sb.append((char) 0xa5);
            } else if ("&sect;".equals(entity) || "&#167;".equals(entity)) {
                sb.append((char) 0xa7);
            } else if ("&copy;".equals(entity) || "&#169;".equals(entity)) {
                sb.append((char) 0xa9);
            } else if ("&reg;".equals(entity) || "&#174;".equals(entity)) {
                sb.append((char) 0xae);
            } else if ("&times;".equals(entity) || "&#215;".equals(entity)) {
                sb.append((char) 215);
            } else if ("&divide;".equals(entity) || "&#247;".equals(entity)) {
                sb.append((char) 247);
            }
            pos1 = m.end();
        }
        sb.append(src.substring(pos1));
        return sb.toString();
    }

    public static String buildquoteString(String name, String content, String postTime, int pid,
            int tid) {
        final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
        content = content.replaceAll(quote_regex, "");
        if (!content.trim().endsWith("[/url]")) {
            if (content.length() > 1000)
                content = content.substring(0, 999) + ".......";
        }
        content = StringEscapeUtils.unescapeHtml4(content);
        content = content.replaceAll("<br/><br/>", "\n");
        content = content.replaceAll("<br/>", "\n");
        StringBuilder postPrefix = new StringBuilder();
        if (pid == 0) {
            postPrefix.append("[quote][tid=");
            postPrefix.append(tid);
            postPrefix.append("]Topic[/tid] [b]Post by[/b] ");
        } else {
            postPrefix.append("[quote][pid=");
            postPrefix.append(pid);
            postPrefix.append("]Reply[/pid] [b]Post by[/b] ");
        }
        postPrefix.append("[@");
        postPrefix.append(name);
        postPrefix.append("]");
        postPrefix.append(" (");
        postPrefix.append(postTime);
        postPrefix.append("):\n");
        postPrefix.append(content);
        postPrefix.append("[/quote]");
        return postPrefix.toString();
    }

    public static String buildReplyString(String name, String postTime, int pid,
            int tid) {
        StringBuilder postPrefix = new StringBuilder();
        if (pid == 0) {
            postPrefix.append("[b]Reply to [tid=");
            postPrefix.append(tid);
            postPrefix.append("]Topic[/tid] Post by[/b] ");
        } else {
            postPrefix.append("[b]Reply to [pid=");
            postPrefix.append(pid);
            postPrefix.append("]Reply[/pid] Post by[/b] ");
        }
        postPrefix.append("[@");
        postPrefix.append(name);
        postPrefix.append("]");
        postPrefix.append(" (");
        postPrefix.append(postTime);
        postPrefix.append("):\n");
        return postPrefix.toString();
    }

    public static String parserAvatarUrl(String avatar) {
        Log.d(TAG, "parserAvatarUrl:" + avatar);
        if (!TextUtils.isEmpty(avatar)) {
            if (avatar.startsWith("{")) {
                int start = avatar.indexOf("http");
                int end = avatar.indexOf("\"",start);
                if(end > start)
                    return avatar.substring(start, end);
            }
        }
        return avatar;
    }

}
