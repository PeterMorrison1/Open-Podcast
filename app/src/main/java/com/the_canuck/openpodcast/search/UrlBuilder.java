package com.the_canuck.openpodcast.search;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.HttpUrl;

public class UrlBuilder {
    private final String APPLE_API_ENDPOINT = "https://itunes.apple.com/search?";
    private String query;

    public UrlBuilder(String query) {
        this.query = query;
    }

    private String encodeQueryTerms() {
        String encodedTerms = "";
        try {
            encodedTerms = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("UrlBuilder.class", "UnsupportedEncodingException: " + e);
        }
        return encodedTerms;
    }

    private String createQueryUrl(String queryTerm) {
        final String PODCAST = "podcast";
        HttpUrl.Builder builder = HttpUrl.parse(APPLE_API_ENDPOINT).newBuilder();

        builder.addQueryParameter(Queryable.TERM.getValue(), queryTerm);
        builder.addQueryParameter(Queryable.MEDIA.getValue(), PODCAST);

        return builder.build().toString();
    }

    public String getSearchUrl() {
        return createQueryUrl(encodeQueryTerms());
    }
}
