package cheng.app.nga.activity;

import cheng.app.nga.R;
import cheng.app.nga.util.Configs;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class AbsThemeActivity extends SherlockFragmentActivity {
    private int theme = 0;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        theme = mPref.getInt(Configs.KEY_THEME, 0);
        if (theme == 1)
            setTheme(R.style.Theme_Black);
        else
            setTheme(R.style.Theme_Styled);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (theme == mPref.getInt(Configs.KEY_THEME, 0)) {

        } else {
            reload();
        }
    }
    private void reload() {

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }


}
