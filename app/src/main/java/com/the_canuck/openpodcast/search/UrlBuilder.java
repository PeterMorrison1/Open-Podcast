package com.the_canuck.openpodcast.search;


import android.util.Log;

import com.the_canuck.openpodcast.search.enums.Queryable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.HttpUrl;

public class UrlBuilder {

    public UrlBuilder() {
    }

    /**
     * Encodes special characters not supported as query terms.
     *
     * @return encoded query terms
     */
    public String encodeQueryTerms(String query) {
        // Not 100% if this is useful. TODO: Test if this is needed.
        String encodedTerms = "";
        Log.d("Skipping", "urlbuilder before encoding");
        try {
            encodedTerms = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("Skipping", "urlbuilder after encoding");
        return encodedTerms;
    }

    /**
     * Creates a url using the itunes/apple search api, query terms, and other required params.
     *
     * @param query terms entered by user to search for
     * @return complete url for search terms on itunes
     */
    public String createQueryUrl(String query, boolean isGenre) {
        Log.d("Skipping", "urlbuilder before creating query url");

        final String PODCAST = "podcast";
        String APPLE_API_ENDPOINT = "https://itunes.apple.com/search?";
        HttpUrl.Builder builder = HttpUrl.parse(APPLE_API_ENDPOINT).newBuilder();

        if (!isGenre) {
            builder.addEncodedQueryParameter(Queryable.TERM.getValue(), query);
        } else {
            builder.addQueryParameter(Queryable.GENREID.getValue(), query);
            builder.addQueryParameter(Queryable.TERM.getValue(), "podcast");
        }

        builder.addEncodedQueryParameter(Queryable.MEDIA.getValue(), PODCAST);

        Log.d("Skipping", "urlbuilder after creating query url");

        return builder.build().toString();
    }
}
