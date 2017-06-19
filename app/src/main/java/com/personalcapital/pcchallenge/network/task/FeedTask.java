package com.personalcapital.pcchallenge.network.task;

import android.os.AsyncTask;

import com.personalcapital.pcchallenge.Constants;
import com.personalcapital.pcchallenge.network.XMLParser;
import com.personalcapital.pcchallenge.network.model.RSSItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * AsyncTask for RSS feed
 */

public class FeedTask extends AsyncTask<Void, Void, List<RSSItem>> {

    private static final String URL = "https://blog.personalcapital.com/feed/?cat=3,891,890,68,284";

    private FeedTaskCallback mFeedTaskCallback = null;

    // Interface for success and failed conditions
    public interface FeedTaskCallback {
        void success(List<RSSItem> list);

        void failed();
    }

    public FeedTask(FeedTaskCallback callback) {
        mFeedTaskCallback = callback;
    }

    // AsyncTask
    @Override
    protected List<RSSItem> doInBackground(Void... params) {
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(Constants.HTTP_GET);
            // 8 seconds timeout
            urlConnection.setConnectTimeout(Constants.EIGHT_SECONDS);
            urlConnection.setReadTimeout(Constants.EIGHT_SECONDS);
            InputStream in = urlConnection.getInputStream();
            XMLParser xmlParser = new XMLParser();
            return xmlParser.parse(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    // Post AsyncTask
    @Override
    protected void onPostExecute(List<RSSItem> list) {
        if (mFeedTaskCallback == null) {
            return;
        }
        if (list != null) {
            mFeedTaskCallback.success(list);
        } else {
            mFeedTaskCallback.failed();
        }
        // Release reference
        mFeedTaskCallback = null;
    }
}
