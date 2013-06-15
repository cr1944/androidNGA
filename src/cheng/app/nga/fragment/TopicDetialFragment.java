package cheng.app.nga.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Toast;

import cheng.app.nga.R;
import cheng.app.nga.activity.ReplyActivity;
import cheng.app.nga.activity.TopicDetialActivity;
import cheng.app.nga.activity.ViewImageActivity;
import cheng.app.nga.content.CommentEntry;
import cheng.app.nga.content.ThreadEntry.GroupEntry;
import cheng.app.nga.content.ThreadReplysEntry;
import cheng.app.nga.content.ThreadEntry;
import cheng.app.nga.content.ThreadUsersEntry;
import cheng.app.nga.loader.TopicDetialLoader;
import cheng.app.nga.util.AvatarLoader;
import cheng.app.nga.util.Configs;
import cheng.app.nga.util.JsonUtil;
import cheng.app.nga.util.TextUtil;
import cheng.app.nga.widget.ErrorDisplayer;
import cheng.app.nga.widget.PageChooser;

import java.io.File;
import java.util.List;

public class TopicDetialFragment extends CustomLoaderFragment<ThreadEntry> implements
PageChooser.OnNumberSetListener, Callback {
    public static final String KEY_PAGE = "page";
    public static final String KEY_TID = "tid";
    public static final String KEY_PID = "pid";
    public static final String KEY_TOTAL_PAGE = "total_page";
    static final int MESSAGE_UPDATE_WEBVIEW = 1;
    static final int MESSAGE_UPDATE_FLOOR = 2;
    static final int MESSAGE_DELAY = 200;
    int mPage = 1;
    int mTid;
    int mPid;
    int mTotalPage;
    Handler mHandler;
    WebView mWebView;
    AvatarLoader mAvatarLoader;

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
            Log.d(TAG, message + " -- From "+ sourceID + "(line "
                    + lineNumber + "). ");
        }
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            Builder builder = new Builder(getActivity());
            builder.setMessage(message);
            builder.setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return true;
        }
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return true;
        }
        public void onProgressChanged(WebView view, int newProgress) {
        }
        public void onReceivedTitle(WebView view, String title) {
        }
    };

    private WebViewClient mWebViewClient = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Log.d(TAG, ">>>url: " + url);
            return true;
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        }
        public void onLoadResource(WebView view, String url) {
            Log.d(TAG, "onLoadResource: " + url);
        }
    };

    private OnActionListener mActionListener = new OnActionListener() {

        @Override
        public void onActionLeft() {
            if (mPage > 1) {
                mPage--;
                requestLoad();
            } else {
                Toast.makeText(getActivity(), R.string.first_page_tip, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onActionMiddle() {
            mHandler.post(new PageChooser(getActivity(), TopicDetialFragment.this, mPage, mTotalPage));
        }

        @Override
        public void onActionRight() {
            if (mPage < mTotalPage) {
                mPage++;
                requestLoad();
            } else {
                Toast.makeText(getActivity(), R.string.last_page_tip, Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onNumberSet(int selectedNumber) {
        mPage = selectedNumber;
        requestLoad();
    }

    public static TopicDetialFragment newInstance(int tid, int pid) {
        TopicDetialFragment f = new TopicDetialFragment();
        Bundle arg = new Bundle();
        arg.putInt(KEY_TID, tid);
        arg.putInt(KEY_PID, pid);
        f.setArguments(arg);
        return f;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_UPDATE_WEBVIEW: {
                List<ThreadEntry> arg = getData();
                if (arg != null && arg.size() != 0) {
                    ThreadEntry data = arg.get(0);
                    if(data != null) {
                        mTotalPage = data.__R__ROWS / 20 + 1;
                        int defaultFontSize = mWebView.getSettings().getDefaultFontSize();
                        boolean smallScreen = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).getBoolean("key_small_screen", true);
                        mWebView.loadUrl("javascript:loadTitle("+smallScreen+","+defaultFontSize+")");
                        setEmptyShow(false);
                        if (mTotalPage > 1) {
                            enableActionBar(true);
                            show();
                        } else {
                            enableActionBar(false);
                        }
                        mWebView.scrollTo(0, 0);
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_UPDATE_FLOOR, 0, 0), MESSAGE_DELAY);
                        return true;
                    }
                }
                setEmptyShow(true);
                return true;
            }
            case MESSAGE_UPDATE_FLOOR:
                int lou = msg.arg1;
                List<ThreadEntry> arg = getData();
                if (arg != null && arg.size() != 0) {
                    ThreadEntry data = arg.get(0);
                    if(data != null) {
                        boolean showSign = PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).getBoolean("key_display_sign", true);
                        if(data.__R != null && data.__R.size() > lou) {
                            ThreadReplysEntry item = data.__R.get(lou);
                            ThreadUsersEntry user = data.__U.get(item.authorid);
                            GroupEntry group = data.__GROUPS.get(user.groupid == -1 ? user.memberid : user.groupid);
                            if (item != null && !TextUtils.isEmpty(item.content)) {
                                mWebView.loadUrl("javascript:loadInfo("+lou+","+item.type+","+item.lou+",'"+user.username+"',"+item.authorid+",'"+group.name+"',"+user.rvrc+","+user.postnum+","+item.postdatetimestamp+")");
                                mWebView.loadUrl("javascript:loadContent('"+item.content+"','"+item.alterinfo+"','"+user.signature+"','"+item.attachs+"','"+item.comment+"',"+lou+","+item.content_length+","+user.rvrc+","+item.tid+","+item.pid+","+item.authorid+","+item.type+","+showSign+")");
                                String avatar = TextUtil.parserAvatarUrl(user.avatar);
                                if (!TextUtils.isEmpty(avatar)) {
                                    mAvatarLoader.loadAvatar(avatar, lou);
                                } else {
                                    Log.w(TAG, "no avatar of lou:" + lou);
                                }
                            }
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_UPDATE_FLOOR, lou + 1, 0), MESSAGE_DELAY);
                        }
                    }
                }
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle arg = getArguments();
        mTid = arg.getInt(KEY_TID, 0);
        mPid = arg.getInt(KEY_PID, 0);
        setActionListener(mActionListener);
        if (savedInstanceState != null) {
            Log.d(TAG, "recreate");
            mPage = savedInstanceState.getInt(KEY_PAGE, 1);
            mTid = savedInstanceState.getInt(KEY_TID, 0);
            mPid = savedInstanceState.getInt(KEY_PID, 0);
            mTotalPage = savedInstanceState.getInt(KEY_TOTAL_PAGE, 1);
        }
        mHandler = new Handler(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt(KEY_PAGE, mPage);
            outState.putInt(KEY_TID, mTid);
            outState.putInt(KEY_PID, mPid);
            outState.putInt(KEY_TOTAL_PAGE, mTotalPage);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_custom_detial, container, false);
    }

    @Override
    public Loader<List<ThreadEntry>> onCreateLoader(int arg0, Bundle arg1) {
        return new TopicDetialLoader(getActivity(), arg1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mWebView = (WebView) getContentView();
        mAvatarLoader = new AvatarLoader(getActivity(), mWebView);
        WebSettings webSettings = mWebView.getSettings();
        //webSettings.setPluginsEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        int defaultFontSize = PreferenceManager.getDefaultSharedPreferences(
              getActivity()).getInt(Configs.KEY_TEXT_FONT_SIZE, Configs.FONT_SIZE_DEFAULT);
        webSettings.setDefaultFontSize(defaultFontSize);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        File dir = new File(Configs.ROOT_PATH + "/web_cache");
        if (!dir.exists())
            dir.mkdirs();
        webSettings.setAppCachePath(Configs.ROOT_PATH + "/web_cache");
        mWebView.addJavascriptInterface(new jsInterface(), "jsInterface");
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onFirstLoad() {
        requestLoad();
    }

    public boolean goBack() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    public void requestLoad() {
        if (mTid == 0 && mPid == 0) {
            return;
        }
        mAvatarLoader.clear();
        mWebView.clearView();
        int theme = PreferenceManager.getDefaultSharedPreferences(
                getActivity()).getInt(Configs.KEY_THEME, 0);
        if (theme == 1) {
            mWebView.loadUrl("file:///android_asset/nga_detial_night.html");
        } else {
            mWebView.loadUrl("file:///android_asset/nga_detial.html");
        }
        updateCurrentPage(mPage);
        Bundle arg = new Bundle();
        arg.putInt(KEY_PAGE, mPage);
        arg.putInt(KEY_TID, mTid);
        arg.putInt(KEY_PID, mPid);
        requestLoad(arg);
    }

    @Override
    public void onLoadFinished(Loader<List<ThreadEntry>> arg0, List<ThreadEntry> arg1) {
        super.onLoadFinished(arg0, arg1);
        TopicDetialLoader loader = (TopicDetialLoader) arg0;
        String result = loader.getResultCode();
        if (TextUtils.isEmpty(result) || !result.equals(JsonUtil.RESULT_OK)) {
            mHandler.post(new ErrorDisplayer(getActivity(), result));
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_UPDATE_WEBVIEW));
        }
        //new UpdateTask(arg1).execute();
    }

    class jsInterface {
        public void showImg(String s) {
            Log.d(TAG, "showImg:"+s);
            final Uri data = Uri.parse(s);
            boolean showDlg = PreferenceManager
                    .getDefaultSharedPreferences(getActivity()).getBoolean("key_show_confirm", true);
            if(!showDlg) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(data, "image/*");
                intent.setClass(getActivity(), ViewImageActivity.class);
                startActivity(intent);
                return; 
            }
            Builder builder = new Builder(getActivity());
            builder.setTitle(R.string.open_img_title);
            builder.setMessage(s);
            builder.setNegativeButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(data, "image/*");
                    intent.setClass(getActivity(), ViewImageActivity.class);
                    startActivity(intent);
                }
            });
            builder.setPositiveButton(android.R.string.cancel, null);
            builder.setCancelable(true);
            builder.create();
            builder.show();
        }

        public void showFlash(String s) {
            Log.d(TAG, "showFlash:"+s);
            openUrl(s);
        }

        public void openUrl(String s) {
            Log.d(TAG, "openUrl:"+s);
            final Uri data = Uri.parse(s);
            boolean showDlg = PreferenceManager
                    .getDefaultSharedPreferences(getActivity()).getBoolean("key_show_confirm", true);
            if(!showDlg) {
                Intent intent = new Intent(Intent.ACTION_VIEW, data);
                if (data.getHost().equals("bbs.ngacn.cc") && data.getPath().equals("/read.php")) {
                    intent.setClass(getActivity(), TopicDetialActivity.class);
                }
                startActivity(intent);
                return; 
            }
            Builder builder = new Builder(getActivity());
            builder.setTitle(R.string.open_url_title);
            builder.setMessage(s);
            builder.setNegativeButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, data);
                    if (data.getHost().equals("bbs.ngacn.cc") && data.getPath().equals("/read.php")) {
                        intent.setClass(getActivity(), TopicDetialActivity.class);
                    }
                    startActivity(intent);
                }
            });
            builder.setPositiveButton(android.R.string.cancel, null);
            builder.setCancelable(true);
            builder.create();
            builder.show();
        }

        public void quote(final int lou) {
            List<ThreadEntry> arg = getData();
            ThreadEntry data = arg.get(0);
            ThreadReplysEntry item = data.__R.get(lou);
            ThreadUsersEntry user = data.__U.get(item.authorid);
            Intent intent = new Intent(getActivity(), ReplyActivity.class);
            intent.putExtra("fid", item.fid);
            intent.putExtra("tid", item.tid);
            intent.putExtra("pid", item.pid);
            intent.putExtra("article", lou);
            intent.putExtra("action", "quote");
            //intent.putExtra("mention", item.author);
            String prefix = TextUtil.buildquoteString(user.username, item.content, item.postdate, item.pid, item.tid);
            intent.putExtra("prefix", prefix);
            startActivity(intent);
        }

        public void reply(final int lou) {
            List<ThreadEntry> arg = getData();
            ThreadEntry data = arg.get(0);
            ThreadReplysEntry item = data.__R.get(lou);
            ThreadUsersEntry user = data.__U.get(item.authorid);
            Intent intent = new Intent(getActivity(), ReplyActivity.class);
            intent.putExtra("fid", item.fid);
            intent.putExtra("tid", item.tid);
            intent.putExtra("pid", item.pid);
            intent.putExtra("article", lou);
            intent.putExtra("action", "reply");
            //intent.putExtra("mention", item.author);
            String prefix = TextUtil.buildReplyString(user.username, item.postdate, item.pid, item.tid);
            intent.putExtra("prefix", prefix);
            startActivity(intent);
        }

        public void comment(final int lou) {
            List<ThreadEntry> arg = getData();
            ThreadEntry data = arg.get(0);
            ThreadReplysEntry item = data.__R.get(lou);
            CommentEntry entry = new CommentEntry();
            entry.tid = item.tid;
            entry.pid = item.pid;
            new CommentFragment(entry).show(getActivity().getSupportFragmentManager(), "addcomment");
        }

        public void message(final int lou) {
            
        }

        public void showonlythis(final int lou) {
            
        }

        public void userinfo(final int lou) {
            List<ThreadEntry> arg = getData();
            ThreadEntry data = arg.get(0);
            ThreadReplysEntry item = data.__R.get(lou);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(Configs.NUKE_URL + Configs.USER_INFO + item.authorid));
            startActivity(intent);
        }

        public void searchtopic(final int lou) {
            
        }

        public void searchreply(final int lou) {
                    
        }

        public void share(final int lou) {
            
        }
    }
}
