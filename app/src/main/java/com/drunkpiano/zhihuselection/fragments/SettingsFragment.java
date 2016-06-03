package com.drunkpiano.zhihuselection.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.view.MenuItem;

import com.drunkpiano.zhihuselection.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public final static String No_ZhIHU_KEY = "doNotUseClient";
    public final static String NO_JS_KEY = "disableJavascript";
    public final static String IMAGE_LOAD = "loadImagePreference";
    public final static String GRADE_ME = "gradeMe";
    public final static String MAIL_ME = "mailMe";

    SwitchPreference doNotUseClient;
    SwitchPreference disableJavascript;
    ListPreference loadImagePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        doNotUseClient = (SwitchPreference) findPreference(No_ZhIHU_KEY);
        disableJavascript = (SwitchPreference) findPreference(NO_JS_KEY);
        loadImagePreference = (ListPreference) findPreference(IMAGE_LOAD);
        findPreference(GRADE_ME).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //                if (Utilities.isAppInstalled(getActivity(), "com.android.vending"))
                launchAppDetail("com.drunkpiano.zhihuselection", getActivity());
                //                else
                //                    Toast.makeText(getActivity(), "您没有安装Play商店,仍然谢谢您的支持!", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        findPreference(MAIL_ME).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mailMe(getActivity());
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        doNotUseClient.setSummary(sharedPreferences.getBoolean(No_ZhIHU_KEY, true) ? "开启以使浏览更平滑" : "关闭以使用知乎客户端查看所有答案");
        disableJavascript.setSummary(sharedPreferences.getBoolean(NO_JS_KEY, false) ? "未启用Javascript(页面中的链接失效)" : "已启用Javascript(页面中的链接激活)");

        String i = sharedPreferences.getString(IMAGE_LOAD, "always");
        switch (i) {
            case "always":
                loadImagePreference.setSummary("始终载入图片");
                break;
            case "ifWifi":
                loadImagePreference.setSummary("仅在WiFi环境下载入图片");
                break;
            case "never":
                loadImagePreference.setSummary("从不加载图片");
                break;
        }
        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case No_ZhIHU_KEY:
                doNotUseClient.setSummary(sharedPreferences.getBoolean(No_ZhIHU_KEY, true)
                        ? "开启以使浏览更平滑" : "关闭以使用知乎客户端查看所有答案");
                break;
            case NO_JS_KEY:
                disableJavascript.setSummary(sharedPreferences.getBoolean(NO_JS_KEY, false)
                        ? "未启用Javascript(页面中的链接失效)"
                        : "已启用Javascript(页面中的链接激活)");
                break;
            case IMAGE_LOAD:
                String i = sharedPreferences.getString(IMAGE_LOAD, "always");
                switch (i) {
                    case "always":
                        loadImagePreference.setSummary("始终载入图片");
                        break;
                    case "ifWifi":
                        loadImagePreference.setSummary("仅在WiFi环境下载入图片");
                        break;
                    case "never":
                        loadImagePreference.setSummary("从不加载图片");
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == android.R.id.home || super.onOptionsItemSelected(item);
    }

    public static void mailMe(Context context) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:lcconan@gmail.com"));
        data.putExtra(Intent.EXTRA_SUBJECT, "Hello");
        data.putExtra(Intent.EXTRA_TEXT, "");
        data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(data);
    }

    public static void launchAppDetail(String appPkg, Context context) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //            if (!TextUtils.isEmpty(marketPkg))
            //                intent.setPackage(marketPkg);//注释掉这一句,也就不指定应用商店名称,变成implicit intent
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //            AppUtils.getAppContext().startActivity(intent);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}