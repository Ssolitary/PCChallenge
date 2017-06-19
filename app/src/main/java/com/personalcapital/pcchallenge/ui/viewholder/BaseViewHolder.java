package com.personalcapital.pcchallenge.ui.viewholder;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.personalcapital.pcchallenge.R;
import com.personalcapital.pcchallenge.Utility;

/**
 * Parent ViewHolder for TopArticleViewHolder and ArticleViewHolder
 */

class BaseViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    ProgressBar progressBar;
    TextView titleTextView;

    Context mContext;

    BaseViewHolder(View itemView) {
        super(itemView);
    }

    // Setup Title TextView
    TextView setupTitleTextView(@DimenRes int sizeRes, int maxLine) {
        TextView tv = createTextView(Gravity.CENTER_VERTICAL, sizeRes, R.color.textColor);
        int padding = Utility.getDimension(mContext, R.dimen.text_padding);
        tv.setPadding(padding, padding, padding, padding);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setLines(maxLine);
        // textView.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        return tv;
    }

    // Create TextView
    TextView createTextView(int gravity, @DimenRes int sizeRes, @ColorRes int colorRes) {
        TextView tv = new TextView(mContext);
        ViewGroup.LayoutParams tvParam = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(tvParam);
        tv.setGravity(gravity);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Utility.getDimension(mContext, sizeRes));
        tv.setTextColor(ContextCompat.getColor(mContext, colorRes));
        return tv;
    }

    // Create LinearLayout
    LinearLayout createLinearLayout() {
        LinearLayout ll = new LinearLayout(mContext);
        ViewGroup.LayoutParams llParam = new ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(llParam);
        ll.setOrientation(LinearLayout.VERTICAL);
        return ll;
    }

    // Setup Image / ProgressBar
    RelativeLayout setupImageSection(int width, int right) {
        RelativeLayout rl = createRelativeLayout(width, right);
        progressBar = createProgressBar();
        imageView = createImageView();
        rl.addView(progressBar);
        rl.addView(imageView);
        return rl;
    }

    // Create RelativeLayout
    private RelativeLayout createRelativeLayout(int width, int right) {
        RelativeLayout rl = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(width, right);
        rl.setLayoutParams(rlParam);
        return rl;
    }

    // Create ProgressBar
    private ProgressBar createProgressBar() {
        ProgressBar pb = new ProgressBar(mContext, null, android.R.attr.progressBarStyleSmall);
        int size = Utility.getDimension(mContext, R.dimen.progressbar_size);
        RelativeLayout.LayoutParams pbParam = new RelativeLayout.LayoutParams(size, size);
        pbParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        pb.setLayoutParams(pbParam);
        return pb;
    }

    // Create ImageView
    private ImageView createImageView() {
        ImageView iv = new ImageView(mContext);
        ViewGroup.LayoutParams ivParam = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(ivParam);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return iv;
    }
}
