package com.personalcapital.pcchallenge.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.personalcapital.pcchallenge.Constants;
import com.personalcapital.pcchallenge.R;
import com.personalcapital.pcchallenge.Utility;
import com.personalcapital.pcchallenge.network.model.RSSItem;
import com.personalcapital.pcchallenge.network.task.FeedTask;
import com.personalcapital.pcchallenge.ui.viewholder.ArticleAdapter;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final int MENU_ITEM_ID = 0;
    // SCROLL_FLAG = 0 for ActionBar without scroll behavior
    //    private static final int SCROLL_FLAG = 0;
    private static final int SCROLL_FLAG = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS |
        AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP;

    private static final int CACHE_DENOMINATOR = 8;
    private static final int PHONE_SPAN = 2;
    private static final int TABLET_SPAN = 3;
    private static final double TABLET_THRESHOLD = 6.5;

    // View
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    // Member Variable
    private ArticleAdapter mAdapter;
    private LruCache<String, Bitmap> mMemoryCache;
    private int mSpanSize = PHONE_SPAN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSpanSize();
        setupLRUImageCache();
        setupUI();
        loadArticles();
    }

    // Create Refresh button on ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add(0, MENU_ITEM_ID, Menu.NONE, getString(R.string.refresh));
        menuItem.setIcon(R.drawable.ic_sync_white_24dp);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    // Setup Refresh button behavior
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_ID:
                loadArticles();
            default:
        }
        return true;
    }

    // Prevent leaking of unfinished progressDialog
    @Override
    protected void onPause() {
        super.onPause();
        Utility.dismissDialog(progressDialog);
    }

    // SetUI for:
    // rootView CoordinatorLayout,
    // ActionBar
    // RecyclerView
    // ProgressBar
    private void setupUI() {
        CoordinatorLayout rootView = createRootView();
        setupActionBar(rootView, R.string.company_name, SCROLL_FLAG);
        setupRecyclerView();
        rootView.addView(recyclerView);
        setContentView(rootView);
        progressDialog = new ProgressDialog(this);
    }

    // Create CoordinatorLayout layout
    private CoordinatorLayout createRootView() {
        CoordinatorLayout coordinatorLayout = new CoordinatorLayout(this);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT);
        coordinatorLayout.setLayoutParams(params);
        return coordinatorLayout;
    }

    // Setup CoordinatorLayout layout
    private void setupRecyclerView() {
        recyclerView = new RecyclerView(this);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT);
        // Set Scrolling Behavior to interact with Toolbar
        params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        recyclerView.setLayoutParams(params);
        // Set GridLayoutManager with different span size depending on different screen size
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, mSpanSize);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Only top article and header have entire span size
                return position == 0 || position == 1 ? mSpanSize : 1;
            }
        });
        recyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new ArticleAdapter(mMemoryCache);
        recyclerView.setAdapter(mAdapter);
    }

    // Send Async Task to fetch RSS feeds
    private void loadArticles() {
        Utility.showDialog(progressDialog, getString(R.string.loading));
        new FeedTask(new FeedTask.FeedTaskCallback() {
            @Override
            public void success(List<RSSItem> list) {
                if (mAdapter != null) {
                    mAdapter.updateList(list);
                }
                Utility.dismissDialog(progressDialog);
            }

            @Override
            public void failed() {
                Utility.dismissDialog(progressDialog);
                // Display Snackbar message and provide retry option
                Snackbar.make(recyclerView, getString(R.string.loading_failed), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadArticles();
                        }
                    }).show();
            }
        }).execute();
    }

    // Get span size base on user agent and screen size
    private void setSpanSize() {
        String ua = new WebView(this).getSettings().getUserAgentString();
        if (ua.contains(Constants.USER_AGENT_MOBILE)) {
            mSpanSize = isTablet() ? TABLET_SPAN : PHONE_SPAN;
        } else {
            mSpanSize = TABLET_SPAN;
        }
    }

    // Check screen size >= 6.5 inch, if true, then is tablet, else phone
    private boolean isTablet() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        return diagonalInches >= TABLET_THRESHOLD;
    }

    // Setup LRU ImageCache
    // https://developer.android.com/topic/performance/graphics/cache-bitmap.html
    private void setupLRUImageCache() {
        // Use RetainFragment to keep LRU cache when screen rotated
        // https://developer.android.com/guide/topics/resources/runtime-changes.html
        RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        mMemoryCache = retainFragment.mRetainedCache;
        if (mMemoryCache == null) {
            // Get max available VM memory
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / Constants.KILO);
            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / CACHE_DENOMINATOR;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / Constants.KILO;
                }
            };
            retainFragment.mRetainedCache = mMemoryCache;
        }
    }

    // RetainFragment inner class
    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragment";
        public LruCache<String, Bitmap> mRetainedCache;

        public RetainFragment() {
        }

        // User FragmentManager as carrier
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }
}
