package com.frida.chinese.jchess.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.frida.chinese.jchess.R;
import com.frida.chinese.jchess.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity {

    public static final String WEB_VIEW_URL = "WEB_VIEW_URL";

    private ActivityWebViewBinding binding; // ★ ViewBinding 替代 ButterKnife

    public static void startUrl(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WEB_VIEW_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        setupWebView();

        String mInitUrl = getIntent().getStringExtra(WEB_VIEW_URL);
        if (!StringUtils.isEmpty(mInitUrl)) {
            binding.webViewWeb.loadUrl(mInitUrl);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.webViewWeb.canGoBack()) {
            binding.webViewWeb.goBack();
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        binding.webViewProgress.setMax(100);

        WebSettings webSettings = binding.webViewWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        if (NetworkUtils.isConnected()) {
            webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        binding.webViewWeb.setWebViewClient(getWebViewClient());
        binding.webViewWeb.setWebChromeClient(getWebChromeClient());
        binding.webViewWeb.setDownloadListener(getDownloadListener());
    }

    private DownloadListener getDownloadListener() {
        return (url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        };
    }

    private WebViewClient getWebViewClient() {
        return new WebViewClient() {
            // 兼容旧签名（项目是老代码）
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("http")) {
                    view.loadUrl(url);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        };
    }

    private WebChromeClient getWebChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 100) {
                    binding.webViewProgress.setProgress(newProgress);
                    binding.webViewProgress.setVisibility(View.VISIBLE);
                } else {
                    binding.webViewProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle(title);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.menu_refresh) {
            binding.webViewWeb.reload();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.webViewWeb.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.webViewWeb.onResume();
    }

    @Override
    public void onDestroy() {
        if (binding != null && binding.webViewWeb != null) {
            binding.webViewWeb.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            binding.webViewWeb.clearHistory();
            binding.webViewWeb.destroy();
        }
        super.onDestroy();
    }
}
