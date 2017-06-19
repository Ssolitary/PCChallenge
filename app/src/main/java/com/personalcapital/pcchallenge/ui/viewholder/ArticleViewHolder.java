package com.personalcapital.pcchallenge.ui.viewholder;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.personalcapital.pcchallenge.R;
import com.personalcapital.pcchallenge.Utility;

/**
 * Article ViewHolder
 */

class ArticleViewHolder extends BaseViewHolder {

    private static final int MAX_TITLE_LINE = 2;

    ArticleViewHolder(ViewGroup rootView) {
        super(rootView);
        mContext = rootView.getContext();
        setupLayout(rootView);
    }

    // Setup:
    // rootView
    // image section : ProgressBar and ImageView
    // Title textView
    private void setupLayout(ViewGroup rootView) {
        LinearLayout ll = createLinearLayout();
        RelativeLayout imageSection = setupImageSection(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            Utility.getDimension(mContext, R.dimen.article_image_height));
        titleTextView = setupTitleTextView(R.dimen.article_title_textsize, MAX_TITLE_LINE);
        ll.addView(imageSection);
        ll.addView(titleTextView);
        rootView.addView(ll);
    }
}