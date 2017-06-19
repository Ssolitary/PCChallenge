package com.personalcapital.pcchallenge.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.personalcapital.pcchallenge.Constants;
import com.personalcapital.pcchallenge.R;

public class ArticleDetailActivity extends BaseActivity {

    private static final int SCROLL_FLAG = 0;
    private static final String URL_APPEND = "?displayMobileNavigation=0";

    // View
    private WebView webView;

    // Member Variable
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        // append url for removing web page navigation bar
        mUrl = String.format(Constants.APPEND_FORMAT_STR, intent.getStringExtra(Constants.INTENT_KEY), URL_APPEND);
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // prevent video/audio continue play when background app
        webView.onPause();
    }

    // Setup Click event for ActionBar Back Arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // If web page can go back, go back to previous page
                // otherwise, close Activity
                if (webView.canGoBack()) {
                    webView.goBack();
                    return true;
                } else {
                    this.finish();
                }
                break;
            default:
        }
        return false;
    }

    // Setup:
    // RooView layout,
    // ActionBar,
    // ActionBar backArrow icon
    // WebView
    private void setupUI() {
        LinearLayout rootView = createVerticalLinearLayout();
        setupActionBar(rootView, R.string.top_article_header, SCROLL_FLAG);
        setupHomeAsUp();
        RelativeLayout relativeLayout = setupWebView();
        rootView.addView(relativeLayout);
        setContentView(rootView);
    }

    // Create Vertical direction LinearLayout
    private LinearLayout createVerticalLinearLayout() {
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(llParams);
        ll.setOrientation(LinearLayout.VERTICAL);
        return ll;
    }

    // Setup ActionBar to display back arrow
    private void setupHomeAsUp() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Setup WebView
    private RelativeLayout setupWebView() {
        RelativeLayout rl = createRelativeLayout();
        ProgressBar pb = createProgressBar();
        rl.addView(pb);

        webView = createWebView();
        ConfigWebViewSetting();
        rl.addView(webView);
        return rl;
    }

    // Create RelativeLayout
    private RelativeLayout createRelativeLayout() {
        RelativeLayout rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT);
        rl.setLayoutParams(rlParam);
        return rl;
    }

    // Create ProgressBar
    private ProgressBar createProgressBar() {
        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams pbParam = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        pbParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        pb.setLayoutParams(pbParam);
        return pb;
    }

    // Create WebView
    private WebView createWebView() {
        WebView wv = new WebView(this);
        LinearLayout.LayoutParams wvParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        wv.setLayoutParams(wvParams);
        wv.setVisibility(View.GONE);
        wv.loadUrl(mUrl);
        return wv;
    }

    // Config WebView
    private void ConfigWebViewSetting() {
        WebSettings webSettings = webView.getSettings();
        // Enable WebView rending / Javascript support
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            // Setup onPageFinished callback
            @Override
            public void onPageFinished(WebView view, String url) {
//                webView.loadUrl(getInjectedJavaScript());
                webView.setVisibility(View.VISIBLE);
            }

            // Hide navigation bar for next web page
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(String.format(Constants.APPEND_FORMAT_STR, url, URL_APPEND));
//                return false;
//            }
        });

        // Setup OnKeyListener for clicking back button behavior
        // If web page can go back, go back to previous page
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                        default:
                    }
                }
                return false;
            }
        });
    }

//    public String getInjectedJavaScript() {
//        return "javascript:(function() { " +
//                "var navbar = document.getElementsByTagName('nav')[0];" +
//                "navbar.style.display = 'none';" +
//                "})()";
//    }
}