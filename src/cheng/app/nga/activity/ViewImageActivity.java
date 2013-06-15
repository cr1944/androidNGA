
package cheng.app.nga.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.TextView;

import cheng.app.nga.R;
import cheng.app.nga.task.WeakAsyncTask;
import cheng.app.nga.util.AvatarLoader;
import cheng.app.nga.util.Configs;
import cheng.app.nga.widget.ErrorDisplayer;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

public class ViewImageActivity extends AbsThemeActivity {
    private final static String TAG = "ViewImageActivity";
    private Uri mUri;
    private String mPath;
    private WebView mWebView;
    private View mProgress;
    private NumberFormat mProgressPercentFormat;
    private TextView mDownloadText;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_image);
        Intent intent = getIntent();
        if (intent != null) {
            Log.d(TAG, "get args from intent");
            mUri = intent.getData();
        }
        mWebView = (WebView) findViewById(R.id.image_view);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        Log.d(TAG, "densityDpi = " + mDensity);
        if (mDensity == 240) {
            mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
        } else if (mDensity == 160) {
            mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
        } else if(mDensity == 120) {
            mWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
        }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
            mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
        }else if (mDensity == DisplayMetrics.DENSITY_TV){
            mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
        }
        mDownloadText = (TextView) findViewById(R.id.loading_text);
        mProgress = findViewById(R.id.progressContainer);
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new LoadTask(this).execute(mUri);
    }

    class LoadTask extends WeakAsyncTask<Uri, Integer, String, ViewImageActivity> {

        public LoadTask(ViewImageActivity target) {
            super(target);
        }

        @Override
        protected String doInBackground(ViewImageActivity activity, Uri... params) {
            Uri uri = params[0];
            if (uri != null) {
                final String scheme = uri.getScheme();
                if (scheme.equals("file")) {
                    return uri.toString();
                } else if (scheme.equals("http") || scheme.equals("https")) {
                    final String file = AvatarLoader.getFileName(uri.toString());
                    final String path = Configs.ROOT_PATH + "/image_cache/" + file;
                    File f = new File(path);
                    if (f.exists()) {
                        return "file://" + path;
                    } else {
                        if (!Configs.hasSdcard()) {
                            return uri.toString();
                        }
                        try {
                            URL u = new URL(uri.toString());
                            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                            int size = connection.getContentLength();
                            if (size == -1) {
                                return null;
                            }
                            publishProgress(0, size);
                            InputStream inputStream = connection.getInputStream();
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int hasRead = 0;
                            int len = 0;
                            while ((len = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, len);
                                hasRead += len;
                                publishProgress(hasRead, size);
                            }
                            inputStream.close();
                            outputStream.close();
                            byte[] image = outputStream.toByteArray();
                            if (image != null) {
                                File dir = new File(Configs.ROOT_PATH + "/image_cache/");
                                if (!dir.exists())
                                    dir.mkdirs();
                                OutputStream os = new FileOutputStream(path);
                                os.write(image);
                                os.close();
                                return "file://" + path;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int current = values[0];
            int max = values[1];
            double percent = (double) current / (double) max;
            StringBuilder sb = new StringBuilder();
            sb.append(mProgressPercentFormat.format(percent));
            sb.append('\n');
            sb.append(current / 1024);
            sb.append("KB / ");
            sb.append(max / 1024);
            sb.append("KB");
            mDownloadText.setText(sb);
        }

        @Override
        protected void onPostExecute(ViewImageActivity target, String result) {
            if (target == null || target.isFinishing())
                return;
            mDownloadText.setText("");
            mProgress.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(result)) {
                mHandler.post(new ErrorDisplayer(ViewImageActivity.this, R.string.error_view_image));
                return;
            }
            mPath = result;
            mWebView.loadUrl(mPath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.image_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem item = menu.findItem(R.id.menu_share);
        item.setVisible(Configs.hasSdcard());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_refresh:
                new LoadTask(this).execute(mUri);
                return true;
            case R.id.menu_save:
                return true;
            case R.id.menu_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mPath));
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
