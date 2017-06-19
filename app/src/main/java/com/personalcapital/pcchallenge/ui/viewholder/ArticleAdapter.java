package com.personalcapital.pcchallenge.ui.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.personalcapital.pcchallenge.Constants;
import com.personalcapital.pcchallenge.R;
import com.personalcapital.pcchallenge.Utility;
import com.personalcapital.pcchallenge.network.model.RSSItem;
import com.personalcapital.pcchallenge.network.task.ImageTask;
import com.personalcapital.pcchallenge.ui.ArticleDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Article Adapter for displaying RSS feeds
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TOP_ARTICLE = 0;
    private static final int HEADER = 1;
    private static final int ARTICLE = 2;

    private Context mContext;
    private List<RSSItem> mRssItemList = new ArrayList<>();
    private LruCache<String, Bitmap> mMemoryCache;

    public ArticleAdapter(LruCache<String, Bitmap> memoryCache) {
        mMemoryCache = memoryCache;
    }

    // Update RecyclerView
    public void updateList(List<RSSItem> rssItemList) {
        mRssItemList = rssItemList;
        notifyDataSetChanged();
    }

    // Create ViewHolder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        if (viewType == TOP_ARTICLE) {
            return createTopArticleViewHolder();
        } else if (viewType == ARTICLE) {
            return createArticleViewHolder();
        } else {
            return createHeaderViewHolder();
        }
    }

    // Bind ViewHolder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RSSItem item = getRSSItem(position);
        String url = item.getMediaContent();
        Bitmap cached = getBitmapFromMemCache(url);
        // If bitmap existed in cache, use it
        // Otherwise send image loading async task to fetch image bitmap
        if (holder.getItemViewType() == TOP_ARTICLE) {
            TopArticleViewHolder newHolder = (TopArticleViewHolder) holder;
            if (cached != null) {
                loadCacheMediaContent(newHolder.imageView, url, cached);
            } else {
                loadMediaContent(newHolder.imageView, newHolder.progressBar, url);
            }
            // Set title and detail text
            newHolder.titleTextView.setText(Utility.fromHtml(item.getTitle()));
            newHolder.detailView.setText(Utility.getTitleWithPubData(item.getPubDate(), item.getDescription()));
        } else if (holder.getItemViewType() == ARTICLE) {
            ArticleViewHolder newHolder = (ArticleViewHolder) holder;
            if (cached != null) {
                loadCacheMediaContent(newHolder.imageView, url, cached);
            } else {
                loadMediaContent(newHolder.imageView, newHolder.progressBar, url);
            }
            // Set title
            newHolder.titleTextView.setText(Utility.fromHtml(item.getTitle()));
        }
    }

    // Position 0 is top article
    // Position 1 is article header
    // Rest are article
    @Override
    public int getItemViewType(int position) {
        if (position == TOP_ARTICLE) {
            return TOP_ARTICLE;
        } else if (position == HEADER) {
            return HEADER;
        }
        return ARTICLE;
    }

    // If list is empty, count is 0
    // Otherwise, add one for header into count
    @Override
    public int getItemCount() {
        int size = mRssItemList.size();
        if (size == 0) {
            return 0;
        } else {
            return size + 1;
        }
    }

    // Get Correct RSSItem by excluding header
    private RSSItem getRSSItem(int position) {
        int pos = 0;
        if (position >= 2) {
            pos = position - 1;
        }
        return mRssItemList.get(pos);
    }

    // Load image bitmap from Cache
    private void loadCacheMediaContent(ImageView imageView, String url, Bitmap bitmap) {
        //setTag for tracking the correct viewHolder to load image
        imageView.setTag(url);
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }

    // Send image loading async task to fetch image bitmap
    private void loadMediaContent(final ImageView imageView, ProgressBar progressBar, final String url) {
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        // setTag for tracking the correct viewHolder to load image
        imageView.setTag(url);
        new ImageTask(new ImageTask.ImageTaskCallback() {
            @Override
            public void success(Bitmap bitmap) {
                // Check tag to make sure the viewHolder request current image is not been recycled
                String tagURL = (String) imageView.getTag();
                if (url.equals(tagURL)) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }
                // Add bitmap into Cache
                addBitmapToMemoryCache(url, bitmap);
            }

            @Override
            public void failed() {
                // If failed, load default image place holder
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pc_default_image);
                imageView.setImageBitmap(bm);
                imageView.setVisibility(View.VISIBLE);
            }
        }).execute(url);
    }

    // Open Article Detail Activity
    private void startArticleActivity(int position) {
        String link = getRSSItem(position).getLink();
        Intent intent = new Intent(mContext, ArticleDetailActivity.class);
        intent.putExtra(Constants.INTENT_KEY, link);
        mContext.startActivity(intent);
    }

    // Create TopArticleViewHolder
    private TopArticleViewHolder createTopArticleViewHolder() {
        LinearLayout ll = new LinearLayout(mContext);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(llParam);
        final TopArticleViewHolder holder = new TopArticleViewHolder(ll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startArticleActivity(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    // Create ArticleViewHolder
    private ArticleViewHolder createArticleViewHolder() {
        CardView cv = new CardView(mContext);
        CardView.LayoutParams cvParam = new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT,
            CardView.LayoutParams.WRAP_CONTENT);
        int margin = Utility.getDimension(mContext, R.dimen.article_cardview_margin);
        cvParam.setMargins(margin, margin, margin, Constants.ZERO_PIXEL);
        cv.setLayoutParams(cvParam);
        cv.setUseCompatPadding(true);
        final ArticleViewHolder holder = new ArticleViewHolder(cv);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startArticleActivity(holder.getAdapterPosition());
            }
        });
        return holder;
    }

    // Create HeaderViewHolder
    private HeaderViewHolder createHeaderViewHolder() {
        TextView tv = new TextView(mContext);
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(param);
        tv.setText(R.string.article_header);
        int padding = Utility.getDimension(mContext, R.dimen.text_padding);
        tv.setPadding(padding, Constants.ZERO_PIXEL, Constants.ZERO_PIXEL, Constants.ZERO_PIXEL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getDimension(mContext, R.dimen.article_header_textsize));
        tv.setTextColor(ContextCompat.getColor(mContext, R.color.Black));
        return new HeaderViewHolder(tv);
    }

    // Add bitmap into Cache
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    // Get bitmap from Cache
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
