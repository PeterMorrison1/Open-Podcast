package com.the_canuck.openpodcast.search;

import android.os.AsyncTask;
import android.util.Log;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.SearchResponseHolder;
import com.the_canuck.openpodcast.search.enums.ItunesJsonKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchHelper {
    private String query;
    private SearchResponseHolder holder = new SearchResponseHolder();

    public SearchHelper(String query) {
        this.query = query;
    }

    public void runSearch() {
        UrlBuilder urlBuilder = new UrlBuilder();
        String url = urlBuilder.createQueryUrl(urlBuilder.encodeQueryTerms(query));

        Log.d("Test URL", "Test url entered: " + url);
        new HttpTask().execute(url);
        try {
            while (holder.getResults() == null) {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.v("Results", "Test Results: " + holder.getResults());
    }

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
//                    Log.d("Response: ", "> " + line);
                }
                holder.setResults(buffer.toString());
                Log.d("doInBackground", "buffer.toString(): " + buffer.toString());
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



    // TODO: Delete this when I know for sure my other solution works
//    private class OkHttpHandlerTest {
//        private OkHttpClient client = new OkHttpClient();
//
////    public String doGetRequest(String url) {
////        try {
////            Log.v("OkHttpHandler.class", url);
////            Log.v("OkHttpHandler.class", "Start of doGetRequest");
////            Request request = new Request.Builder()
////                    .url(url)
////                    .build();
////            Log.v("OkHttpHandler.class", "Before Response after .build()");
////            Response response = client.newCall(request).execute();
////            Log.v("OkHttpHandler.class", response.body().string());
////            return response.body().string();
////        } catch (IOException e) {
////            Log.e("OkHttpHandler.class", "IOException: " + e);
////        }
////        return null;
////    }
//
//
//        protected String DoStuff(String url) {
//            Log.v("OkHttpHandler.class", url);
//            Log.v("OkHttpHandler.class", "Start of doInBackground");
//
//            Request.Builder builder = new Request.Builder();
//            builder.url("https://itunes.apple.com/search?");
//            Request request = builder.build();
//
//            Log.v("OkHttpHandler.class", "Before Response after .build()");
//
//            try {
//                Response response = client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if(!response.isSuccessful()) {
//                            throw new IOException("Unexpected code " + response);
//                        }
//                            results = response.body().string();
//
//                    }
//                });
//                Log.v("OkHttpHandler.class", response.body().string());
//                String results = response.body().string();
//                return results;
//            } catch (Exception e) {
//                Log.e("OkHttpHandler.class", "IOException: " + e);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            results = s;
//        }
//    }

}
