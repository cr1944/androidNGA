
package cheng.app.nga.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;

import cheng.app.nga.R;

import com.actionbarsherlock.app.SherlockFragment;

public class SmileyFragment extends SherlockFragment implements Callback{
    static final String TAG = "SmileyFragment";
    static final int MESSAGE_PICK = 1;
    static final int MESSAGE_DONE = 2;
    WebView mWebView;
    Handler mHandler = new Handler(this);
    //private String mSmiles = "";
    private Callback mCallback;
    public interface Callback{
        public void pick(Object s);
        public void done();
    }
    public void setCallback(Callback l) {
        mCallback = l;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_PICK:
                if (mCallback != null) {
                    mCallback.pick(msg.obj);
                }
                break;
            case MESSAGE_DONE:
                if (mCallback != null) {
                    mCallback.done();
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = new ContextThemeWrapper(
                getActivity(), R.style.AppDarkTheme);
        LayoutInflater l = LayoutInflater.from(context);
        return l.inflate(R.layout.smiley_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mWebView = (WebView) view.findViewById(R.id.smiley_grid);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.addJavascriptInterface(new JsInterface(), "JsInterface");
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.loadUrl("file:///android_asset/nga_smiley.html");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl("javascript:loademo(0)");
            }
        }, 1000);
        super.onViewCreated(view, savedInstanceState);
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        };
    };

//    public void reset() {
//        mSmiles = "";
//    }
//
//    public String getSmiles() {
//        return mSmiles;
//    }

    class JsInterface {
        public void addsmile(String s) {
            //mSmiles += s;
            Log.i(TAG, s);
            mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_PICK, s));
        }
        public void done() {
            Log.i(TAG, "done");
            mHandler.sendEmptyMessage(MESSAGE_DONE);
        }
    }
}
