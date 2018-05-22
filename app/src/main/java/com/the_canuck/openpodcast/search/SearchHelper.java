package com.the_canuck.openpodcast.search;

import android.os.AsyncTask;

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

    public SearchHelper(String query) {
        this.query = query;
    }

    /**
     * Builds url based on user input and runs the url in HttpTask to return json results.
     */
    public void runSearch() {
        UrlBuilder urlBuilder = new UrlBuilder();
        String url = urlBuilder.createQueryUrl(urlBuilder.encodeQueryTerms(query));

        new HttpTask().execute(url);
        try {
            while (holder.getResults() == null) {
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
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            try {
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
            super.onPostExecute(s);
        }
    }
}
