package cheng.app.nga.util;

import android.os.Environment;

public class Configs {
    public static final String KEY_LIST_FONT_SIZE = "key_list_font_size";
    public static final String KEY_TEXT_FONT_SIZE = "key_text_font_size";
    public static final String KEY_THEME = "key_theme";
    public static final int FONT_SIZE_DEFAULT = 16;
    public static final int MINIMUM_FONTSIZE = 8;
    public static final int MAXIMUM_FONTSIZE = 24;

    public static final String LOGIN_URL = "http://account.178.com/q_account.php?_act=login";
    public static final String ROOT = "http://bbs.ngacn.cc";
    public static final String THREAD_URL = "http://bbs.ngacn.cc/thread.php";
    public static final String READ_URL = "http://bbs.ngacn.cc/read.php";
    public static final String REPLY_URL="http://bbs.ngacn.cc/post.php";
    public static final String NUKE_URL="http://bbs.ngacn.cc/nuke.php";
    public static final String NOTIFY = "?func=noti&__notpl&__nodb&__nolib";
    public static final String TOPIC_KEY = "?func=loadtopickey&fid=";
    public static final String USER_INFO = "?func=ucp&uid=";
    public static final String COMMENT = "?func=comment";
    public static final String LITE = "&lite=js&v2&noprefix";
    public static final String RECOMMEND1 = "&recommend=1&order_by=postdatedesc&admin=1";
    public static final String RECOMMEND2 = "&recommend=1&order_by=postdatedesc&user=1";

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory().toString()
            + "/android_nga_cache";
    public static boolean hasSdcard() {
        try {
            return Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
