package com.app.strkita.measurenote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;

/**
 * SharedPreferences設定用ユーティリティクラス
 * Created by kitada on 2017/05/09.
 */

public class SettingPrefUtil {

    // 保存先ファイル名
    public static final String PREF_FILE_NAME = "settings";

    private static final String KEY_TEXT_SIZE = "text.size";
    public static final String TEXT_SIZE_LARGE = "large";
    public static final String TEXT_SIZE_MEDIUM = "medium";
    public static final String TEXT_SIZE_SMALL = "small";

    private static final String KEY_SCREEN_REVERSE = "screen.reverse";

    // Utilクラスのため、インスタンスを作成させない
    private SettingPrefUtil() {}

    // フォントサイズを取得する
    public static float getFontSize(Context context) {
        // SharedPreferencesを取得
        SharedPreferences sp = context.getSharedPreferences(
                PREF_FILE_NAME, Context.MODE_PRIVATE);
        // 現在の設定値
        String storedSize = sp.getString(KEY_TEXT_SIZE, TEXT_SIZE_MEDIUM);

        // 設定値に応じて、実際のテキストサイズを返す
        switch (storedSize) {
            case TEXT_SIZE_LARGE:
                return context.getResources().getDimension(
                        R.dimen.settings_text_size_large);
            case TEXT_SIZE_MEDIUM:
                return context.getResources().getDimension(
                        R.dimen.settings_text_size_medium);
            case TEXT_SIZE_SMALL:
                return context.getResources().getDimension(
                        R.dimen.settings_text_size_small);
            default:
                return context.getResources().getDimension(
                        R.dimen.settings_text_size_medium);
        }
    }

    // 画面の明暗を反転するかどうか
    public static boolean isScreenReverse(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_SCREEN_REVERSE, false);
    }
}
