package com.personalcapital.pcchallenge.ui;

import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.personalcapital.pcchallenge.R;

/**
 * Parent Activity for ArticleDetailActivity and MainActivity
 */

public class BaseActivity extends AppCompatActivity {

    // Setup Toolbar/AppBarLayout
    void setupActionBar(ViewGroup rootView, @StringRes int titleRes, int scrollFlags) {
        Toolbar toolbar = createToolbar(titleRes, scrollFlags);
        AppBarLayout appBarLayout = createAppBarLayout();
        appBarLayout.addView(toolbar);
        setSupportActionBar(toolbar);
        rootView.addView(appBarLayout);
    }

    // Create toolbar
    private Toolbar createToolbar(@StringRes int titleRes, int scrollFlags) {
        Toolbar toolbar = new Toolbar(this);
        AppBarLayout.LayoutParams params = new AppBarLayout.LayoutParams(
            AppBarLayout.LayoutParams.MATCH_PARENT,
            AppBarLayout.LayoutParams.WRAP_CONTENT);
        params.setScrollFlags(scrollFlags);
        toolbar.setLayoutParams(params);
        toolbar.setTitle(titleRes);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.White));
        return toolbar;
    }

    // Create AppBarLayout
    private AppBarLayout createAppBarLayout() {
        AppBarLayout appBarLayout = new AppBarLayout(this);
        AppBarLayout.LayoutParams params = new AppBarLayout.LayoutParams(
            AppBarLayout.LayoutParams.MATCH_PARENT,
            AppBarLayout.LayoutParams.WRAP_CONTENT);
        appBarLayout.setLayoutParams(params);
        return appBarLayout;
    }
}
