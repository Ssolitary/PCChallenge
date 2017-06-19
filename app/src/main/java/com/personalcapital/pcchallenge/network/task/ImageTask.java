package com.personalcapital.pcchallenge.network.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.personalcapital.pcchallenge.Constants;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * AsyncTask for image fetching
 */

public class ImageTask extends AsyncTask<String, Void, Bitmap> {

    private static final int DOWN_SAMPLE_SIZE = 2;

    private ImageTaskCallback mImageTaskCallback = null;

    // Interface for success and failed conditions
    public interface ImageTaskCallback {
        void success(Bitmap bitmap);

        void failed();
    }

    public ImageTask(ImageTaskCallback callback) {
        mImageTaskCallback = callback;
    }

    // AsyncTask
    @Override
    protected Bitmap doInBackground(String... params) {
        String URL;
        if (params.length != 0) {
            URL = params[0];
        } else {
            return null;
        }

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
            // down-sample the image to save cache memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = DOWN_SAMPLE_SIZE;
            return BitmapFactory.decodeStream(in, null, options);
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
    protected void onPostExecute(Bitmap bitmap) {
        if (mImageTaskCallback == null) {
            return;
        }
        if (bitmap != null) {
            mImageTaskCallback.success(bitmap);
        } else {
            mImageTaskCallback.failed();
        }
        mImageTaskCallback = null;
    }
}
