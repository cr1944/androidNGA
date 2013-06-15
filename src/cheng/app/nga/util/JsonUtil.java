
package cheng.app.nga.util;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import cheng.app.nga.content.BlockEntry;
import cheng.app.nga.content.MentionEntry;
import cheng.app.nga.content.MessageEntry;
import cheng.app.nga.content.ThreadEntry.GroupEntry;
import cheng.app.nga.content.ThreadInfoEntry;
import cheng.app.nga.content.ThreadReplysEntry;
import cheng.app.nga.content.ThreadEntry;
import cheng.app.nga.content.ThreadUsersEntry;
import cheng.app.nga.content.TopicKeysEntry;
import cheng.app.nga.content.ReadEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {
    static final String TAG = "JsonUtil";
    public static final String RESULT_OK = "ok";
    public static final String RESULT_ERROR_UNKNOWN = "unknown";
    public static final String RESULT_ERROR_EMPTY = "empty";
    public static final String RESULT_ERROR_PARSE = "parse error";

    static final String NUM_REX = "[^0-9]";

    @Deprecated
    private static int getErrorNumber(String text) {
        Pattern p = Pattern.compile(NUM_REX);
        Matcher m = p.matcher(text);
        return Integer.valueOf(m.replaceAll("").trim());
    }

    public static String parseTopicList(String text, int[] page, List<ReadEntry> list) {
        if (TextUtils.isEmpty(text)) {
            return RESULT_ERROR_EMPTY;
        }
        try {
            JSONObject jo = new JSONObject(text);
            JSONObject data = jo.optJSONObject("data");
            String message = data.optString("__MESSAGE");
            if (!TextUtils.isEmpty(message)) {
                int end = message.indexOf("<br/>");
                if (end == -1) {
                    return message;
                }
                String error = message.substring(0, end);
                if (!TextUtils.isEmpty(error)) {
                    return error;
                } else {
                    return message;
                }
            } else {
                int rows = data.optInt("__ROWS");
                int t_rows = data.optInt("__T__ROWS");
                Log.d(TAG, "rows=" + rows + ";t_rows=" + t_rows);
                page[0] = rows;
                JSONObject t = data.optJSONObject("__T");
                if (t != null) {
                    for (int i = 0; i < t.length(); i++) {
                        JSONObject item = t.optJSONObject(String.valueOf(i));
                        if (item != null) {
                            ReadEntry news = new ReadEntry();
                            news.tid = item.optInt("tid");
                            news.fid = item.optInt("fid");
                            news.titlefont = item.optString("titlefont");
                            news.author = item.optString("author");
                            news.authorid = item.optInt("authorid");
                            news.subject = item.optString("subject");
                            news.quote_from = item.optInt("quote_from");
                            news.ifmark = item.optInt("ifmark");
                            news.digest = item.optInt("digest");
                            news.locked = item.optInt("locked");
                            news.ifupload = item.optInt("ifupload");
                            news.type = item.optInt("type");
                            news.type_2 = item.optInt("type_2");
                            news.postdate = item.optLong("postdate");
                            news.lastpost = item.optLong("lastpost");
                            news.lastposter = item.optString("lastposter");
                            news.recommend = item.optInt("recommend");
                            news.replies = item.optInt("replies");
                            list.add(news);
                        }
                    }
                    return RESULT_OK;
                } else {
                    return RESULT_ERROR_PARSE;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return RESULT_ERROR_UNKNOWN;
        }
    }

    public static String parseTopicDetial(String text, List<ThreadEntry> list) {
        if (TextUtils.isEmpty(text)) {
            return RESULT_ERROR_EMPTY;
        }
        try {
            // js = js.replaceAll("\"content\":\\+(\\d+),",
            // "\"content\":\"+$1\",");
            // js = js.replaceAll("\"subject\":\\+(\\d+),",
            // "\"subject\":\"+$1\",");
            // js = js.replaceAll("\"content\":(0\\d+),",
            // "\"content\":\"$1\",");
            // js = js.replaceAll("\"subject\":(0\\d+),",
            // "\"subject\":\"$1\",");
            // js = js.replaceAll("\"author\":(0\\d+),", "\"author\":\"$1\",");
//            final int start = text.indexOf("\"__P\":{\"aid\":");
//            final int end = text.indexOf("\"this_visit_rows\":");
//            if (start != -1 && end != -1) {
//                String validJs = text.substring(0, start);
//                validJs += text.substring(end);
//                text = validJs;
//            }
            JSONObject jo = new JSONObject(text);
            JSONObject data = jo.optJSONObject("data");
            ThreadEntry item = new ThreadEntry();
            item.__MESSAGE = data.optString("__MESSAGE");
            list.add(item);
            if (!TextUtils.isEmpty(item.__MESSAGE)) {
                int e = item.__MESSAGE.indexOf("<br/>");
                if (e == -1) {
                    return item.__MESSAGE;
                }
                String error = item.__MESSAGE.substring(0, e);
                if (!TextUtils.isEmpty(error)) {
                    return error;
                } else {
                    return item.__MESSAGE;
                }
            } else {
                JSONObject __R = data.optJSONObject("__R");
                item.__R__ROWS = data.optInt("__R__ROWS");
                item.__F = data.optJSONObject("__F");
                JSONObject __T = data.optJSONObject("__T");
                JSONObject __U = data.optJSONObject("__U");
                if (__T != null) {
                    item.__T = new ThreadInfoEntry();
                    item.__T.author = __T.optString("author");
                    item.__T.authorid = __T.optInt("authorid");
                    item.__T.fid = __T.optInt("fid");
                    item.__T.icon = __T.optInt("icon");
                    item.__T.ifupload = __T.optInt("ifupload");
                    item.__T.lastmodify = __T.optLong("lastmodify");
                    item.__T.lastpost = __T.optLong("lastpost");
                    item.__T.lastposter = __T.optString("lastposter");
                    item.__T.postdate = __T.optLong("postdate");
                    item.__T.quote_from = __T.optInt("quote_from");
                    item.__T.quote_to = __T.optInt("quote_to");
                    item.__T.recommend = __T.optInt("recommend");
                    //item.__T.this_visit_rows = __T.optInt("ifmark");
                    item.__T.tid = __T.optInt("tid");
                    item.__T.titlefont = __T.optInt("titlefont");
                    item.__T.type = __T.optInt("type");
                    //item.__T.type_2 = __T.optInt("ifmark");
                    item.__T.digest = __T.optInt("digest");
                    item.__T.locked = __T.optInt("locked");
                    item.__T.replies = __T.optInt("replies");
                    item.__T.subject = __T.optString("subject");
                    item.__T.ifmark = __T.optInt("ifmark");
                }
                if (__R != null) {
                    item.__R = new ArrayList<ThreadReplysEntry>();
                    //item.size = __R.length();
                    for (int i = 0; i < __R.length(); i++) {
                        JSONObject j = __R.optJSONObject(String.valueOf(i));
                        if (j != null) {
                            ThreadReplysEntry r = new ThreadReplysEntry();
                            r.pid = j.optInt("pid");
                            r.tid = j.optInt("tid");
                            r.fid = j.optInt("fid");
                            r.content = j.optString("content");
                            r.alterinfo = j.optString("alterinfo");
                            r.type = j.optInt("type");
                            r.authorid = j.optInt("authorid");
                            r.postdate = j.optString("postdate");
                            r.subject = j.optString("subject");
                            r.content_length = j.optInt("content_length");
                            r.lou = j.optInt("lou");
                            r.postdatetimestamp = j.optLong("postdatetimestamp");
                            r.js_escap_org_forum = j.optString("js_escap_org_forum");
                            r.org_fid = j.optInt("org_fid");
                            r.recommend = j.optInt("recommend");
                            r.attachs = j.optString("attachs");
                            JSONObject comment = j.optJSONObject("comment");
                            r.comment = getComment(comment);
                            item.__R.add(r);
                        }
                    }
                }
                if (__U != null) {
                    item.__U = new SparseArray<ThreadUsersEntry>();
                    Iterator it = __U.keys();  
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        JSONObject j = __U.optJSONObject(key);
                        if (j != null) {
                            if (key.equals("__GROUPS")) {
                                item.__GROUPS = new SparseArray<GroupEntry>();
                                Iterator group = j.keys();  
                                while (group.hasNext()) {
                                    String s = (String) group.next();
                                    JSONObject g = j.optJSONObject(s);
                                    if (g != null) {
                                        GroupEntry ge = new GroupEntry();
                                        ge.name = g.optString("0");
                                        ge.bit = g.optInt("1");
                                        item.__GROUPS.put(Integer.parseInt(s), ge);
                                    }
                                }
                            } else if (key.equals("__MEDALS")) {
                            } else if (key.equals("__REPUTATIONS")) {
                            } else {
                                ThreadUsersEntry u = new ThreadUsersEntry();
                                u.uid = j.optInt("uid");
                                u.username = j.optString("username");
                                u.credit = j.optInt("credit");
                                u.medal = j.optInt("medal");
                                u.reputation = j.optString("reputation");
                                u.groupid = j.optInt("groupid");
                                u.memberid = j.optInt("memberid");
                                u.avatar = j.optString("avatar");
                                u.yz = j.optInt("yz");
                                u.site = j.optString("site");
                                u.honor = j.optString("honor");
                                u.regdate = j.optLong("regdate");
                                u.mute_time = j.optLong("mute_time");
                                u.postnum = j.optInt("postnum");
                                u.rvrc = j.optInt("rvrc");
                                u.money = j.optInt("money");
                                u.thisvisit = j.optLong("thisvisit");
                                u.signature = j.optString("signature");
                                u.nickname = j.optString("nickname");
                                u.bit_data = j.optInt("bit_data");
                                item.__U.put(Integer.parseInt(key), u);
                            }
                        }
                    }
                }
                if (item.__R != null && item.__U != null) {
                    return RESULT_OK;
                } else {
                    return RESULT_ERROR_PARSE;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return RESULT_ERROR_UNKNOWN;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return RESULT_ERROR_PARSE;
        }
    }

    private static String getComment(JSONObject comment) throws JSONException {
        if (comment == null)
            return "";
        int size = comment.length();
        if (size == 0)
            return "";
        JSONObject result = new JSONObject();
        for (int i = 0; i < size; i++) {
            JSONObject item = comment.getJSONObject(String.valueOf(i));
            item.remove("js_escap_avatar");
            item.remove("signature");
            item.remove("reputation");
            item.remove("medal");
            result.put(String.valueOf(i), item);
        }
        return result.toString();
    }

    public static ArrayList<TopicKeysEntry> parseTopicKeys(String text) {
        Log.d(TAG, "parseTopicKeys:" + text);
        try {
            JSONObject json = new JSONObject(text);
            JSONObject data = json.getJSONObject("data");
            if (data != null) {
                ArrayList<TopicKeysEntry> result = new ArrayList<TopicKeysEntry>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject j = data.getJSONObject(String.valueOf(i));
                    int top = j.optInt("top");
                    String key = j.optString("key");
                    TopicKeysEntry item = new TopicKeysEntry(top, key);
                    result.add(item);
                }
                return result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseMessage(String text, ArrayList<MessageEntry> list) {
        if (TextUtils.isEmpty(text)) {
            return RESULT_ERROR_EMPTY;
        }
        try {
            JSONObject json = new JSONObject(text);
            String error = json.optString("error");
            if (!TextUtils.isEmpty(error)) {
                return error;
            }
            JSONObject data = json.getJSONObject("data");
            for (int i = 0; i < data.length(); i++) {
                if (!data.has(String.valueOf(i)))
                    continue;
                JSONObject j = data.getJSONObject(String.valueOf(i));
                MessageEntry item = new MessageEntry();
                item.mid = j.optInt("mid");
                item.last_modify = j.optLong("last_modify");
                item.bit = j.optInt("bit");
                item.subject = j.optString("subject");
                item.from = j.optInt("from");
                item.time = j.optInt("time");
                item.last_from = j.optInt("last_from");
                item.posts = j.optInt("posts");
                item.from_username = j.optString("from_username");
                item.last_from_username = j.optString("last_from_username");
                list.add(item);
            }
            return RESULT_OK;
        } catch (JSONException e) {
            e.printStackTrace();
            return RESULT_ERROR_PARSE;
        }
    }

    public static String parseBlock(String text, ArrayList<BlockEntry> list) {
        if (TextUtils.isEmpty(text)) {
            return RESULT_ERROR_EMPTY;
        }
        try {
            JSONObject json = new JSONObject(text);
            JSONObject data = json.getJSONObject("data");
            if (data.length() == 0)
                return RESULT_ERROR_EMPTY;
            for (int i = 0; i < data.length(); i++) {
                JSONObject j = data.getJSONObject(String.valueOf(i));
                BlockEntry item = new BlockEntry();
                item.uid = j.optInt("uid");
                item.username = j.optString("username");
                list.add(item);
            }
            return RESULT_OK;
        } catch (JSONException e) {
            e.printStackTrace();
            return RESULT_ERROR_PARSE;
        }
    }

    public static String parseMention(String text, ArrayList<MentionEntry> list) {
        if (TextUtils.isEmpty(text)) {
            return RESULT_ERROR_EMPTY;
        }
        try {
            JSONObject json = new JSONObject(text);
            for (int i = 0; i < json.length(); i++) {
                JSONArray j = json.getJSONArray(String.valueOf(i));
                JSONObject k = j.getJSONObject(0);
                MentionEntry item = new MentionEntry();
                item._TYPE = k.optInt("0");
                item._FROM_UID = k.optInt("1");
                item._FROM_UNAME = k.optString("2");
                item._TO_UID = k.optInt("3");
                item._TO_UNAME = k.optString("4");
                item._TEXT = k.optString("5");
                item._ABOUT_ID = k.optInt("6");
                item._ABOUT_ID_2 = k.optInt("7");
                item._ABOUT_ID_3 = k.optInt("8");
                item._TIME = k.optLong("9");
                item._ABOUT_ID_4 = k.optInt("10");
                list.add(item);
            }
            return RESULT_OK;
        } catch (JSONException e) {
            e.printStackTrace();
            return RESULT_ERROR_PARSE;
        }
    }
}
