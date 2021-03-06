package com.app.strkita.measurenote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.RequiresApi;


/**
 * 設定用フラグメント
 */
public class SettingFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingFragment() {}

    // 変更イベントをActivityに通知する
    public interface SettingFragmentListener {
        void onSettingChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ファイル名を指定する
        getPreferenceManager().setSharedPreferencesName(SettingPrefUtil.PREF_FILE_NAME);
        // Preferencesの設定ファイルを指定
        addPreferencesFromResource(R.xml.preferences);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTypefaceSummary(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Activityを取得
        Activity activity = getActivity();

        if (activity instanceof SettingFragmentListener) {
            SettingFragmentListener listener = (SettingFragmentListener)activity;

            // Activityに変更通知
            listener.onSettingChanged();
        }

        // サマリーに反映する
        if (activity.getString(R.string.key_text_size).equals(key)) {
            setTypefaceSummary(sharedPreferences);
        }
    }

    private void setTypefaceSummary(SharedPreferences sharedPreferences) {
        String key = getActivity().getString(R.string.key_text_size);

        Preference preference = findPreference(key);
        String selected = sharedPreferences.getString(key, null);
        preference.setSummary(selected);
    }
}
