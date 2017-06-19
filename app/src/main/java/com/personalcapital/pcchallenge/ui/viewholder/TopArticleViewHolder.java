package com.personalcapital.pcchallenge.ui.viewholder;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.personalcapital.pcchallenge.Constants;
import com.personalcapital.pcchallenge.R;
import com.personalcapital.pcchallenge.Utility;

/**
 * Top Article ViewHolder
 */

class TopArticleViewHolder extends BaseViewHolder {

    private static final int MAX_TITLE_LINE = 1;
    private static final int MAX_DETAIL_LINE = 2;

    TextView detailView;

    TopArticleViewHolder(ViewGroup rootView) {
        super(rootView);
        mContext = rootView.getContext();
        setupLayout(rootView);
    }

    // Setup
    // rootView
    // Activity header
    // CardView
    private void setupLayout(ViewGroup rootView) {
        TextView textView = setupHeaderTextView();
        rootView.addView(textView);

        CardView cardView = createCardView();
        setupCardView(cardView);
        rootView.addView(cardView);
    }

    // Setup
    // Image section : ProgressBar / ImageView
    // Article title TextView
    // Article detail TextView
    private void setupCardView(CardView cardView) {
        LinearLayout ll = createLinearLayout();
        RelativeLayout imageSection = setupImageSection(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            Utility.getDimension(mContext, R.dimen.top_article_image_height));
        titleTextView = setupTitleTextView(R.dimen.top_article_title_textsize, MAX_TITLE_LINE);
        detailView = createDetailTextView();
        ll.addView(imageSection);
        ll.addView(titleTextView);
        ll.addView(detailView);
        cardView.addView(ll);
    }

    // Setup Header TextView
    private TextView setupHeaderTextView() {
        TextView tv = createTextView(Gravity.CENTER, R.dimen.top_article_header_textsize, R.color.Black);
        int padding = Utility.getDimension(mContext, R.dimen.text_padding);
        tv.setPadding(Constants.ZERO_PIXEL, padding, Constants.ZERO_PIXEL, padding);
        tv.setText(R.string.top_article_header);
        return tv;
    }

    // Create CardView
    private CardView createCardView() {
        CardView cv = new CardView(mContext);
        CardView.LayoutParams cvParam = new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT,
            CardView.LayoutParams.WRAP_CONTENT);
        cvParam.setMargins(Constants.ZERO_PIXEL, Constants.ZERO_PIXEL, Constants.ZERO_PIXEL, Utility.getDimension(mContext, R.dimen.top_article_cardview_bottom_margin));
        cv.setLayoutParams(cvParam);
        cv.setUseCompatPadding(true);
        return cv;
    }

    // Setup Detail TextView
    private TextView createDetailTextView() {
        TextView tv = createTextView(Gravity.CENTER_VERTICAL, R.dimen.top_article_detail_textsize, R.color.textColor);
        int padding = Utility.getDimension(mContext, R.dimen.text_padding);
        tv.setPadding(padding, Constants.ZERO_PIXEL, padding, padding);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setLines(MAX_DETAIL_LINE);
        return tv;
    }
}