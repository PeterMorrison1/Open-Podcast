package com.the_canuck.openpodcast.search;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpHandler {
    // TODO: Delete this class when I'm sure my other code in searchhelper works
    private OkHttpClient client = new OkHttpClient();
    private String result;

    public void doStuff(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Log.v("OkHttpHandler.class", "Response.body.string: YAAAAAAAY");
                    result = response.body().string();
                } catch (Exception e) {
                    Log.v("onResponse", "OnResponse error: " + e);
                }
            }
        });
    }

    public String getResult() {
        Log.v("Result in OKHTTP", "OKHTTPHANDLER RESULT: " + result);
        return result;
    }

    //    public String doGetRequest(String url) {
//        try {
//            Log.v("OkHttpHandler.class", url);
//            Log.v("OkHttpHandler.class", "Start of doGetRequest");
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//            Log.v("OkHttpHandler.class", "Before Response after .build()");
//            Response response = client.newCall(request).execute();
//            Log.v("OkHttpHandler.class", response.body().string());
//            return response.body().string();
//        } catch (IOException e) {
//            Log.e("OkHttpHandler.class", "IOException: " + e);
//        }
//        return null;
//    }
//
//    @Override
//    protected String doInBackground(String...url) {
//        Log.v("OkHttpHandler.class", url[0]);
//        Log.v("OkHttpHandler.class", "Start of doInBackground");
//
//        Request.Builder builder = new Request.Builder();
//        builder.url(url[0]);
//        Request request = builder.build();
//
//        Log.v("OkHttpHandler.class", "Before Response after .build()");
//
//        try {
//            Response response = client.newCall(request).execute();
//            Log.v("OkHttpHandler.class", response.body().string());
//            String results = response.body().string();
//            return results;
//        } catch (IOException e) {
//            Log.e("OkHttpHandler.class", "IOException: " + e);
//        }
//        return null;
//    }

//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//
//    }
}
