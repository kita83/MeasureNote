package com.app.strkita.measurenote;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * 設定画面アクティビティ
 * Created by kitada on 2017/05/10.
 */

public class SettingActivity extends AppCompatActivity implements SettingFragment.SettingFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

//        getFragmentManager().beginTransaction()
//                .replace(R.id.SettingFragment, new SettingFragment())
//                .addToBackStack(null)
//                .commit();

        setResult(RESULT_CANCELED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSettingChanged() {
        setResult(RESULT_OK);
    }
}
