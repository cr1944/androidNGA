
package cheng.app.nga.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import cheng.app.nga.R;
import cheng.app.nga.adapter.ActionAccountsAdapter;
import cheng.app.nga.content.NGAApp;
import cheng.app.nga.content.PostEntry;
import cheng.app.nga.content.TopicKeysEntry;
import cheng.app.nga.fragment.SmileyFragment;
import cheng.app.nga.fragment.TopicIconFragment;
import cheng.app.nga.task.FileUploadTask;
import cheng.app.nga.task.LoadKeyTask;
import cheng.app.nga.task.PostTask;
import cheng.app.nga.widget.BbsCodeDialog;
import cheng.app.nga.widget.TopicTypeDialog;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ReplyActivity extends AbsThemeActivity implements OnCheckedChangeListener,
        View.OnClickListener, TextWatcher, OnNavigationListener {
    static final String TAG = "ReplyActivity";
    static final String TAG_ICON_FRAGMENT = "tag_topic_icon";
    static final String TAG_SMILEY_FRAGMENT = "tag_nga_smiley";
    static final String KEY_TOPIC_KEYS = "key_topic_keys";
    static final String KEY_TOPIC_ICON = "key_topic_icon";
    static final String KEY_TOPIC_HIDE = "key_topic_hide";
    static final String KEY_TOPIC_LIMIT = "key_topic_limit";
    static final String KEY_ATTACH = "key_attach";
    static final String KEY_ATTACH_CHECK = "key_attach_check";
    static final String KEY_ATTACH_ARRAY = "key_attach_array";
    static final int REQUEST_CODE_ATTACH = 1024;
    EditText mTitleField;
    EditText mContentField;
    Button mIconBtn;
    Button mTypeBtn;
    CheckBox mHideCheck;
    CheckBox mLimitCheck;
    String mSendText;
    LinearLayout mAttachLayout;
    PostEntry mPostEntry;
    ArrayList<TopicKeysEntry> mTopicKeys;
    ArrayList<String> mAttachs = new ArrayList<String>();
    TopicIconFragment mTopicIconFragment;
    TopicTypeDialog mTopicTypeDialog;
    SmileyFragment mSmileyFragment;
    NGAApp mApp;

    SmileyFragment.Callback mSmileyCallback = new SmileyFragment.Callback() {
        
        @Override
        public void done() {
            toggleSmileyView();
        }

        @Override
        public void pick(Object s) {
            if (s == null) return;
            addToContent((String) s);
        }
    };

    TopicIconFragment.Callback mIconCallback = new TopicIconFragment.Callback() {

        @Override
        public void onCallback(int value) {
            mPostEntry.post_icon = value;
        }
    };

    TopicTypeDialog.Callback mTypeListener = new TopicTypeDialog.Callback() {

        @Override
        public void onCallback(String value) {
            mTitleField.append(value);
        }

    };

    BbsCodeDialog.Callback mBbsCodeLintener = new BbsCodeDialog.Callback() {

        @Override
        public void onCallback(String value) {
            addToContent(value);
        }
    };

    private void addToContent(String s) {
        if (TextUtils.isEmpty(s)) return;
        if (mContentField == null) return;
        int index = mContentField.getSelectionStart();
        Editable edit = mContentField.getEditableText();
        if (index < 0 || index >= edit.length()) {
            edit.append(s);
        } else {
            edit.insert(index, s);
        }
        mContentField.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (NGAApp) getApplication();
        Cursor c = mApp.loadAccounts();
        if (c == null || c.getCount() == 0) {
            Toast.makeText(this, R.string.login_tips, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.layout_post);
        mPostEntry = new PostEntry();
        if (savedInstanceState != null) {
            mTopicKeys = savedInstanceState.getParcelableArrayList(KEY_TOPIC_KEYS);
            mPostEntry.post_icon = savedInstanceState.getInt(KEY_TOPIC_ICON, 0);
            mPostEntry.hidden = savedInstanceState.getBoolean(KEY_TOPIC_HIDE, false);
            mPostEntry.self_reply = savedInstanceState.getBoolean(KEY_TOPIC_LIMIT, false);
            mPostEntry.attachments = savedInstanceState.getString(KEY_ATTACH);
            mPostEntry.attachments_check = savedInstanceState.getString(KEY_ATTACH_CHECK);
            mAttachs = savedInstanceState.getStringArrayList(KEY_ATTACH_ARRAY);
        }
        Intent intent = getIntent();
        if (intent != null) {
            Log.d(TAG, "get args from intent");
            mPostEntry.fid = intent.getIntExtra("fid", 414);
            mPostEntry.tid = intent.getIntExtra("tid", 0);
            mPostEntry.pid = intent.getIntExtra("pid", 0);
            // mPostEntry.article = intent.getIntExtra("article", 0);
            String action = intent.getStringExtra("action");
            if (!TextUtils.isEmpty(action)) {
                mPostEntry.action = action;
            }
            //mPostEntry.mention = intent.getStringExtra("mention");
            mPostEntry.post_content = intent.getStringExtra("prefix");
        }
        mTitleField = (EditText) findViewById(R.id.post_title);
        mContentField = (EditText) findViewById(R.id.post_content);
        mIconBtn = (Button) findViewById(R.id.post_icon);
        mTypeBtn = (Button) findViewById(R.id.post_type);
        mAttachLayout = (LinearLayout) findViewById(R.id.post_attach);
        mHideCheck = (CheckBox) findViewById(R.id.post_content_hide);
        mLimitCheck = (CheckBox) findViewById(R.id.post_content_limit);
        if (mPostEntry.action.equals("new")) {
            mLimitCheck.setVisibility(View.VISIBLE);
        }
        mIconBtn.setOnClickListener(this);
        mTypeBtn.setOnClickListener(this);
        mHideCheck.setOnCheckedChangeListener(this);
        mLimitCheck.setOnCheckedChangeListener(this);
        mContentField.addTextChangedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        //LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View customActionBarView = inflater.inflate(R.layout.editor_custom_action_bar, null);
        //mSendBtn = (Button) customActionBarView.findViewById(R.id.post_send);
        //mSendBtn.setOnClickListener(this);
        //getSupportActionBar().setCustomView(customActionBarView,
        //        new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
        //                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
        //                        | Gravity.RIGHT));
        Context context = getSupportActionBar().getThemedContext();
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ActionAccountsAdapter list = new ActionAccountsAdapter(context, c);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setSelectedNavigationItem(0);
        FragmentManager fm = getSupportFragmentManager();
        mSmileyFragment = (SmileyFragment) fm.findFragmentByTag(TAG_SMILEY_FRAGMENT);
        mTopicIconFragment = (TopicIconFragment) fm.findFragmentByTag(TAG_ICON_FRAGMENT);
        if (mTopicIconFragment != null) {
            Log.d(TAG, "mTopicIconFragment != null");
            mTopicIconFragment.setCallback(mIconCallback);
        }
        if (mSmileyFragment != null) {
            mSmileyFragment.setCallback(mSmileyCallback);
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(mSmileyFragment);
            ft.commit();
        }
        if (mAttachs != null && !mAttachs.isEmpty()) {
            for (String a : mAttachs) {
                addAttach(a);
            }
        }
        if (savedInstanceState == null && !TextUtils.isEmpty(mPostEntry.post_content)) {
            mContentField.setText(mPostEntry.post_content);
            mContentField.setSelection(mPostEntry.post_content.length());
        }
    }

    @Override
    protected void onPause() {
        if (mTopicTypeDialog != null && mTopicTypeDialog.isShowing()) {
            mTopicTypeDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt(KEY_TOPIC_ICON, mPostEntry.post_icon);
            outState.putBoolean(KEY_TOPIC_HIDE, mPostEntry.hidden);
            outState.putBoolean(KEY_TOPIC_LIMIT, mPostEntry.self_reply);
            outState.putString(KEY_ATTACH, mPostEntry.attachments);
            outState.putString(KEY_ATTACH_CHECK, mPostEntry.attachments_check);
            outState.putStringArrayList(KEY_ATTACH_ARRAY, mAttachs);
            if (mTopicKeys != null)
                outState.putParcelableArrayList(KEY_TOPIC_KEYS, mTopicKeys);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.post_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_send);
        if (TextUtils.isEmpty(mSendText)) {
            item.setTitle(R.string.send);
            item.setEnabled(false);
        } else {
            item.setTitle(getString(R.string.send) + mSendText);
            item.setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_emo:
                toggleSmileyView();
                return true;
            case R.id.menu_bbscode: {
                final Context dialogContext = new ContextThemeWrapper(this, R.style.AppDarkTheme);
                new AlertDialog.Builder(dialogContext)
                    .setTitle(R.string.menu_bbscode)
                    .setItems(R.array.bbscodes, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 12) {
                                addToContent(getString(R.string.bbscode_list));
                            } else if (which == 14) {
                                addToContent(getString(R.string.bbscode_album));
                            } else if (which == 19) {
                                addToContent(getString(R.string.bbscode_collapse));
                            } else if (which == 20) {
                                addToContent(getString(R.string.bbscode_customachieve));
                            } else {
                                new BbsCodeDialog(dialogContext, which, mBbsCodeLintener).show();
                            }
                        }
                    })
                    .create().show();
            }
                return true;
            case R.id.menu_send:
                CharSequence content = mContentField.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(this, R.string.content_empty, Toast.LENGTH_SHORT).show();
                    return true;
                }
                mPostEntry.post_subject = mTitleField.getText().toString();
                mPostEntry.post_content = mContentField.getText().toString();
                new PostTask(ReplyActivity.this, R.string.posting).execute(mPostEntry);
                break;
            case R.id.menu_attach:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_ATTACH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        switch (arg0) {
            case REQUEST_CODE_ATTACH:
                if (arg1 != RESULT_CANCELED && arg2.getData() != null) {
                    Cursor c = getContentResolver().query(arg2.getData(), null, null, null, null);
                    if (c != null) {
                        if (c.moveToFirst()) {
                            final String f = c.getString(c.getColumnIndex("_data"));
                            int s = c.getInt(c.getColumnIndex("_size")) / 1024;
                            final String m = c.getString(c.getColumnIndex("mime_type"));
                            final String n = c.getString(c.getColumnIndex("_display_name"));
                            final String msg = getString(R.string.upload_tips, f, s + "K");
                            final Context dialogContext = new ContextThemeWrapper(
                                    ReplyActivity.this, R.style.AppDarkTheme);
                            new AlertDialog.Builder(dialogContext)
                            .setMessage(msg)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new FileUploadTask(ReplyActivity.this, R.string.uploading, mPostEntry.account)
                                        .execute(f, n, m);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .create().show();
                        }
                        c.close();
                    }
                }
                break;
        }
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onBackPressed() {
        if (mSmileyFragment != null && !mSmileyFragment.isHidden()) {
            toggleSmileyView();
            return;
        }
        super.onBackPressed();
    }

    private void toggleSmileyView() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_bottom_enter,
                R.anim.fragment_slide_bottom_exit);
        if (mSmileyFragment == null) {
            mSmileyFragment = new SmileyFragment();
            ft.add(R.id.emo_frame, mSmileyFragment, TAG_SMILEY_FRAGMENT);
            mSmileyFragment.setCallback(mSmileyCallback);
            hideSoftKeyboard();
        } else {
            if (mSmileyFragment.isHidden()) {
                //mSmileyFragment.reset();
                ft.show(mSmileyFragment);
                hideSoftKeyboard();
            } else {
                ft.hide(mSmileyFragment);
                //String text = mContentField.getText().toString() + mSmileyFragment.getSmiles();
                //mContentField.setText(text);
                mContentField.requestFocus();
            }
        }
        ft.commit();
    }

    private void hideSoftKeyboard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mPostEntry.account = itemPosition;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_icon:
                if (mTopicIconFragment == null) {
                    mTopicIconFragment = new TopicIconFragment(mIconCallback);
                }
                mTopicIconFragment.show(getSupportFragmentManager(), TAG_ICON_FRAGMENT);
                break;
            case R.id.post_type:
                if (mTopicKeys == null)
                    new LoadKeyTask(ReplyActivity.this, R.string.loading).execute(mPostEntry.fid);
                else {
                    if (mTopicTypeDialog == null) {
                        final Context dialogContext = new ContextThemeWrapper(this, R.style.AppDarkTheme);
                        mTopicTypeDialog = new TopicTypeDialog(dialogContext, mTypeListener);
                    }
                    mTopicTypeDialog.show();
                    mTopicTypeDialog.setContent(mTopicKeys);
                }

                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.post_content_hide:
                mPostEntry.hidden = isChecked;
                break;
            case R.id.post_content_limit:
                mPostEntry.self_reply = isChecked;
                break;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int bytes = 0;
        if (s != null) {
            try {
                bytes = s.toString().getBytes("GBK").length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        int left = 40000 - bytes;

        if (left == 40000) {
            mSendText = "";
        } else if (left < 0) {
            mSendText = "";
        } else {
            mSendText = "(" + bytes + "/40000)";
        }
        invalidateOptionsMenu();
    }

    private void addAttach(String picUrl) {
        mAttachLayout.setVisibility(View.VISIBLE);
        TextView t = new TextView(this);
        t.setSingleLine(true);
        t.setEllipsize(TruncateAt.MARQUEE);
        t.setText("[img]" + picUrl + "[/img]");
        t.setTextAppearance(this, android.R.style.TextAppearance_Small);
        mAttachLayout.addView(t);
    }

    public void onUploaded(String attachments, String attachmentsCheck, String picUrl) {
        mPostEntry.attachments += attachments;
        mPostEntry.attachments_check += attachmentsCheck;
        mAttachs.add(picUrl);
        addAttach(picUrl);
    }

    public void onKeysLoaded(ArrayList<TopicKeysEntry> result) {
        mTopicKeys = result;
        if (mTopicTypeDialog == null) {
            final Context dialogContext = new ContextThemeWrapper(
                    this, R.style.AppDarkTheme);
            mTopicTypeDialog = new TopicTypeDialog(dialogContext, mTypeListener);
        }
        mTopicTypeDialog.show();
        mTopicTypeDialog.setContent(mTopicKeys);
    }

}
