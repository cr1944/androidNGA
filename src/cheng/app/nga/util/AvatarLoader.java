package cheng.app.nga.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Handler.Callback;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import org.apache.commons.io.FilenameUtils;

public class AvatarLoader implements Callback {
    private static final String TAG = "ImageLoader";
    private  Context mContext;
    private WebView mWebView;

    private static final int MESSAGE_REQUEST_LOADING = 1;
    private static final int MESSAGE_PHOTOS_LOADED = 3;

    private static class AvatarHolder {
        private static final int NEEDED = 0;
        private static final int LOADING = 1;
        private static final int LOADED = 2;

        int state = NEEDED;
        String url;
        AvatarHolder(String u) {
            url = u;
        }
    }
    private final ConcurrentHashMap<Integer, AvatarHolder> mPendingRequests =
        new ConcurrentHashMap<Integer, AvatarHolder>();
    private final Handler mMainThreadHandler = new Handler(this);
    private LoaderThread mLoaderThread;
    private boolean mLoadingRequested;
    private boolean mPaused = false;

    public interface OnLoadedListener {
        void onLoaded();
    }

    public AvatarLoader(Context context, WebView webView) {
        mContext = context;
        mWebView = webView;
    }

    public static String getFileName(String url) {
        String extension = FilenameUtils.getExtension(url);
        if (TextUtils.isEmpty(extension)) {
            extension = "jpg";
        }
        int end = extension.indexOf("?");
        if (end != -1) {
            extension = extension.substring(0, end);
        }
        String path = url.toLowerCase();
        StringBuilder sb = new StringBuilder();
        path = path.replaceAll("[^\\w]+", "_");
        sb.append(path);
        sb.append('.');
        sb.append(extension);
        return sb.toString();
    }

    private void doLoadAvatar(String avatar, int lou) {
        if (mWebView != null) {
            Log.d(TAG, "doLoadAvatar: "+avatar + " of lou:" + lou);
            mWebView.loadUrl("javascript:loadAvatar('"+avatar+"',"+lou+")");
        }
    }

    public void loadAvatar(String imageUrl, int lou) {
        if (TextUtils.isEmpty(imageUrl)) {
            Log.e(TAG, "loadAvatar: imageUrl is empty!");
            return;
        }
        if (!imageUrl.toLowerCase().startsWith("http")) {
            Log.e(TAG, "loadAvatar: invalidate imageUrl:"+imageUrl);
            return;
        }
        ConnectivityManager conn =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        boolean loadIn3G = PreferenceManager
                .getDefaultSharedPreferences(mContext).getBoolean("key_display_head", false);
        boolean isWifi = net != null && net.isConnected() && net.getType() == ConnectivityManager.TYPE_WIFI;
        if (Configs.hasSdcard()) {
            if (loadCachedAvatar(imageUrl, lou)) {
            } else if (isWifi || loadIn3G) {
                Log.d(TAG, "download avatar (" + imageUrl + ") of lou:"+ lou);
                mPendingRequests.remove(lou);
                if (!mPaused) {
                    AvatarHolder a = new AvatarHolder(imageUrl);
                    mPendingRequests.put(lou, a);
                    requestLoading();
                }
            } else {
                Log.d(TAG, "not display avatar of lou:"+ lou);
            }
        } else {
            if (isWifi || loadIn3G) {
                Log.d(TAG, "no sdcard, download avatar of lou:"+ lou);
                doLoadAvatar(imageUrl, lou);
            } else {
                Log.d(TAG, "no sdcard, not display avatar of lou:"+ lou);
            }
        }
    }

    private boolean loadCachedAvatar(String imageUrl, int lou) {
        String avatarname = getFileName(imageUrl);
        final String avatarPath = Configs.ROOT_PATH + "/avatar_cache/" + avatarname;
        File f = new File(avatarPath);
        if (f.exists()) {
            doLoadAvatar(avatarPath, lou);
            Log.d(TAG, "cache loaded:" + avatarPath);
            return true;
        }
        return false;
    }

    private void requestLoading() {
        if (!mLoadingRequested) {
            mLoadingRequested = true;
            mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING);
        }
    }

    public void stop() {
        pause();

        if (mLoaderThread != null) {
            mLoaderThread.quit();
            mLoaderThread = null;
        }

        clear();
    }

    public void clear() {
        mPendingRequests.clear();
    }

    public void pause() {
        mPaused = true;
    }

    public void resume() {
        mPaused = false;
        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_REQUEST_LOADING: {
                mLoadingRequested = false;
                if (!mPaused) {
                    if (mLoaderThread == null) {
                        mLoaderThread = new LoaderThread();
                        mLoaderThread.start();
                    }

                    mLoaderThread.requestLoading();
                }
                return true;
            }
            case MESSAGE_PHOTOS_LOADED: {
                if (!mPaused) {
                    processLoadedImages();
                }
                return true;
            }

        }
        return false;
    }

    private void processLoadedImages() {
        Iterator<Integer> iterator = mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            int lou = iterator.next();
            AvatarHolder a = mPendingRequests.get(lou);
            if (a.state == AvatarHolder.LOADED) {
                loadCachedAvatar(a.url, lou);
                iterator.remove();
            }
        }
        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    private static boolean storeAvatar(byte[] data, String url) {
        Log.d(TAG, "storeAvatar: " + url);
        if (data == null) return false;
        OutputStream outputStream = null;
        String name = getFileName(url);
        File dir = new File(Configs.ROOT_PATH + "/avatar_cache/");
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, name);
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
            return true;
        } catch (FileNotFoundException ex) {
            Log.w(TAG, ex);
        } catch (IOException ex) {
            Log.w(TAG, ex);
        }
        return false;
    }

    private void obtainPhotoIdsToLoad(ArrayList<String> urls) {
        urls.clear();

        Iterator<AvatarHolder> iterator = mPendingRequests.values().iterator();
        while (iterator.hasNext()) {
            AvatarHolder a = iterator.next();
            if (a.state == AvatarHolder.NEEDED) {
                a.state = AvatarHolder.LOADING;
                if (urls.contains(a.url)) {
                    Log.d(TAG ,"ignore the same url!");
                } else {
                    urls.add(a.url);
                }
            }
        }
    }

    private void cache(String url, boolean loaded) {
        Iterator<AvatarHolder> iterator = mPendingRequests.values().iterator();
        while (iterator.hasNext()) {
            AvatarHolder a = iterator.next();
            if (a.url.equals(url)) {
                a.state = AvatarHolder.LOADED;
            }
        }
    }

    private class LoaderThread extends HandlerThread implements Callback {
        private Handler mLoaderThreadHandler;
        private final ArrayList<String> mImageUrls = new ArrayList<String>();

        public LoaderThread() {
            super(TAG);
        }

        public void requestLoading() {
            if (mLoaderThreadHandler == null) {
                mLoaderThreadHandler = new Handler(getLooper(), this);
            }
            mLoaderThreadHandler.sendEmptyMessage(0);
        }

        @Override
        public boolean handleMessage(Message msg) {
            loadImage();
            Message m = mMainThreadHandler.obtainMessage(MESSAGE_PHOTOS_LOADED);
            mMainThreadHandler.sendMessage(m);
            return true;
        }

        private void loadImage() {
            obtainPhotoIdsToLoad(mImageUrls);
            int count = mImageUrls.size();
            if (count == 0) {
                return;
            }
            try {
                ArrayList<String> temp = new ArrayList<String>();
                temp.addAll(mImageUrls);
                for (int i = 0; i < count; i++) {
                    String url = temp.get(i);
                    byte[] bytes = HttpUtil.getBytesFromUrl(url);
                    boolean loaded = storeAvatar(bytes, url);
                    cache(url, loaded);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
