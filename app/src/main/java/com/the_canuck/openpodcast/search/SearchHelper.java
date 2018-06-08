package com.the_canuck.openpodcast.search;

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
    private boolean isGenre;

    public SearchHelper() {
    }

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
    public String runSearch() {
        UrlBuilder urlBuilder = new UrlBuilder();
        String url;
        if (!isGenre) {
            url = urlBuilder.createQueryUrl(urlBuilder.encodeQueryTerms(query), isGenre);
        } else {
            url = urlBuilder.createQueryUrl(genreId, isGenre);
        }

        String results = httpTask(url);
        return results;
    }

    /**
     * Runs httpTask for rss feeds (instead of creating a url a url is simply passed in)
     *
     * @param url feed url for the podcast
     * @return result from the feed
     */
    public String parseRss(String url) {
        return httpTask(url);
    }

    /**
     * Connects to the itunes search api and sets the holder result value to the returned json info.
     */
    private String httpTask(final String strings) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(strings.replace("%2B", "+"));
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
        return holder.getResults();
    }
}
