package com.frida.chinese.jchess.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AppUtils;
import com.frida.chinese.jchess.R;
import com.frida.chinese.jchess.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding binding; // 取代 ButterKnife

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化 ViewBinding
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupVersionInfo();
        setupClickListeners();
    }

    private void setupVersionInfo() {
        String versionName = AppUtils.getAppVersionName();
        binding.versionName.setText(versionName); // 直接用 binding
    }

    private void setupClickListeners() {
        binding.versionInfoItem.setOnClickListener(v ->
                WebViewActivity.startUrl(this, getString(R.string.github_release_page)));

        binding.sourceCodeItem.setOnClickListener(v ->
                WebViewActivity.startUrl(this, getString(R.string.github_project_page)));

        binding.aboutMeItem.setOnClickListener(v ->
                WebViewActivity.startUrl(this, getString(R.string.github_user_page)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
