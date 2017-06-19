package com.personalcapital.pcchallenge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DimenRes;
import android.text.Html;
import android.text.Spanned;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility Class.
 */

public class Utility {

    // Convert @dimens resource in to pixel value
    public static int getDimension(Context context, @DimenRes int dimenRes) {
        Resources resources = context.getResources();
        float px = resources.getDimension(dimenRes);
        return (int) px;
    }

    // Convert html encoded content string to text with markup for displaying.
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String inputStr) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(inputStr, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(inputStr);
        }
        return result;
    }

    // Prepend publish date to Top article's summery
    public static Spanned getTitleWithPubData(String pubDate, String str) {
        String s = str;
        String tag1 = str.substring(0, 3);
        String tag2 = str.substring(str.length() - 4, str.length());
        // remove <p> , </p> tag
        if (Constants.OPEN_P_TAG.equals(tag1) &&
            Constants.CLOSE_P_TAG.equals(tag2)) {
            s = str.substring(3, str.length() - 4);
        }
        String formattedDate;
        SimpleDateFormat originalFormat = new SimpleDateFormat(Constants.SERVER_TIME_FORMAT, Locale.ENGLISH);
        try {
            // parse server response time
            Date date = originalFormat.parse(pubDate);
            // convert time to required format
            SimpleDateFormat targetFormat = new SimpleDateFormat(Constants.TARGET_TIME_FORMAT, Locale.ENGLISH);
            formattedDate = String.format(Constants.TIME_FORMAT_STR, targetFormat.format(date));
        } catch (Exception e) {
            return fromHtml(s);
        }
        return fromHtml(formattedDate + s);
    }

    // Show ProgressDialog
    public static void showDialog(ProgressDialog progressDialog, String str) {
        progressDialog.setMessage(str);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    // Hide ProgressDialog
    public static void dismissDialog(ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
