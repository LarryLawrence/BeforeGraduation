package com.drunkpiano.zhihuselection.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.drunkpiano.zhihuselection.R;
import com.drunkpiano.zhihuselection.utilities.MarketUtils;
import com.drunkpiano.zhihuselection.utilities.Utilities;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public final static String No_ZhIHU_KEY = "doNotUseClient";
    public final static String NO_JS_KEY = "disableJavascript";
    public final static String GRADE_ME = "gradeMe";
    public final static String MAILE_ME = "mailMe";

    SwitchPreference doNotUseClient;
    SwitchPreference disableJavascript;
    Preference gradeMe;
    Preference mailMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.pref_general);
        doNotUseClient = (SwitchPreference) findPreference(No_ZhIHU_KEY);
        disableJavascript = (SwitchPreference) findPreference(NO_JS_KEY);
        findPreference(GRADE_ME).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (Utilities.isAppInstalled(getApplicationContext(), "com.android.vending"))
                    MarketUtils.launchAppDetail("com.drunkpiano.zhihuselection", "com.android.vending", getApplicationContext());
                else
                    Toast.makeText(getApplicationContext(), "您没有安装Play商店,仍然谢谢您的支持!", Toast.LENGTH_LONG).show();

                return true;
            }
        });
        findPreference(MAILE_ME).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mailMe(getApplication());
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        doNotUseClient.setSummary(sharedPreferences.getBoolean(No_ZhIHU_KEY, true) ? "开启以使浏览更平滑" : "关闭以使用知乎客户端打开所有答案");
        disableJavascript.setSummary(sharedPreferences.getBoolean(NO_JS_KEY, true) ? "让网页中的链接等元素失效以获取纯粹的阅读体验" : "开启后页面中的链接会被激活");

        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(No_ZhIHU_KEY)) {
            doNotUseClient.setSummary(sharedPreferences.getBoolean(No_ZhIHU_KEY, true) ? "开启以使浏览更平滑" : "关闭以使用知乎客户端打开所有答案");

        } else if (key.equals(NO_JS_KEY)) {
            disableJavascript.setSummary(sharedPreferences.getBoolean(NO_JS_KEY, true) ? "让网页中的链接等元素失效以获取纯粹的阅读体验" : "开启后页面中的链接会被激活");
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void mailMe(Context context) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:lcconan@gmail.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "Hello");
        data.putExtra(Intent.EXTRA_TEXT, "");
        data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(data);
    }

}
