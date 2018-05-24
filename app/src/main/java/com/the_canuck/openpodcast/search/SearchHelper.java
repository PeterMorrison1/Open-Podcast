package com.the_canuck.openpodcast.search;

import android.os.AsyncTask;
import android.util.Log;

import com.the_canuck.openpodcast.SearchResponseHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchHelper {
    private String query;
    private SearchResponseHolder holder = new SearchResponseHolder();
    private String genreId;
    private boolean isGenre = false;

    public SearchHelper(String query) {
        this.query = query;
    }

    public SearchHelper(String genreId, boolean isGenre) {
        this.genreId = genreId;
        this.isGenre = isGenre;
    }

    /**
     * Builds url based on user input and runs the url in HttpTask to return json results.
     */
    public void runSearch() {
        Log.d("Skipping", "runSearch start");
        UrlBuilder urlBuilder = new UrlBuilder();
        String url = "";
        if (!isGenre) {
            url = urlBuilder.createQueryUrl(urlBuilder.encodeQueryTerms(query), isGenre);
        } else {
            url = urlBuilder.createQueryUrl(urlBuilder.encodeQueryTerms(genreId), isGenre);
        }
        Log.d("Skipping", "runSearch after urlbuilder.createqueryurl");

        new HttpTask().execute(url);
        try {
            while (holder.getResults() == null) {
                Log.d("Skipping", "runSearch thread.sleep");
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return holder which has the json results from HttpTask
     */
    public SearchResponseHolder getHolder() {
        return holder;
    }

    /**
     * Connects to the itunes search api and sets the holder result value to the returned json info.
     */
    private class HttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("Skipping", "Do in background start");
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            try {
                Log.d("Test", "Test url: " + strings[0]);
                URL url = new URL(strings[0].replace("%2B", "+"));
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream in = connection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(in));

                StringBuilder buffer = new StringBuilder();
                String line = "";

                holder.setResults("\n");
                while((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }
                holder.setResults(buffer.toString());
                Log.d("Skipping", "Do in background end");
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Skipping", "Do in background PostExecute");
            super.onPostExecute(s);
        }
    }
}
