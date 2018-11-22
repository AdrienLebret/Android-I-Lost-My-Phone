package com.assignment3.cerbonnelebret.ilostmyphone;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Class created for the API IS GD
 */
public class IsGd extends AsyncTask<String, String, String>{
    private static final String TAG = "com.assignment3.cerbonnelebret.ilostmyphone.IsGd URL shortener";
    private static final String API = "https://is.gd/create.php?format=simple";
    private ShortenerListener listener;

    public IsGd(ShortenerListener listener, String url) {
        this.listener = listener;
        execute(url);
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            // Send the long URL to the server
            String encoded = URLEncoder.encode(params[0], "UTF-8");
            URL req = new URL(API + "&url=" + encoded);
            URLConnection conn = req.openConnection();

            // Read the short URL from the server
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String shortUrl = reader.readLine();
            reader.close();
            return shortUrl;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL", e);
        } catch (IOException e) {
            Log.e(TAG, "IO exception", e);
        }
        // In case of failure, return the long URL
        return params[0];
    }

    @Override
    protected void onPostExecute(String shortUrl) {
        listener.done(shortUrl);
    }

    public interface ShortenerListener {
        public void done(String shortUrl);
    }
}
