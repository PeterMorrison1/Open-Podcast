package com.the_canuck.openpodcast.search;

import android.util.Log;

import com.the_canuck.openpodcast.search.enums.Queryable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.HttpUrl;

public class UrlBuilder {
    private final String APPLE_API_ENDPOINT = "https://itunes.apple.com/search?";
    private String query;

    public UrlBuilder(String query) {
        this.query = query;
    }

    /**
     * Encodes special characters not supported as query terms.
     *
     * @return encoded query terms
     */
    private String encodeQueryTerms() {
        // Not 100% if this is useful. TODO: Test if this is needed.
        String encodedTerms = "";
        try {
            encodedTerms = URLEncoder.encode(query, "UTF-8");
            encodedTerms = encodedTerms.replace("%2B", "+");
        } catch (UnsupportedEncodingException e) {
            Log.e("UrlBuilder.class", "UnsupportedEncodingException: " + e);
        }
        return encodedTerms;
    }

    /**
     * Creates a url using the itunes/apple search api, query terms, and other required params.
     *
     * @param query terms entered by user to search for
     * @return complete url for search terms on itunes
     */
    private String createQueryUrl(String query) {
        final String PODCAST = "podcast";
        HttpUrl.Builder builder = HttpUrl.parse(APPLE_API_ENDPOINT).newBuilder();

        builder.addQueryParameter(Queryable.TERM.getValue(), query);
        builder.addQueryParameter(Queryable.MEDIA.getValue(), PODCAST);

        return builder.build().toString();
    }

    /**
     * Runs the query term encoder and passes the result to create the query term url.
     *
     * @return complete url for search terms on itunes
     */
    public String getSearchUrl() {
        return createQueryUrl(encodeQueryTerms());
    }
}
