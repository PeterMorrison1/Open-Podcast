package com.the_canuck.openpodcast.search;

import com.the_canuck.openpodcast.Episode;

import java.util.List;

public interface RssReaderApi {

    interface RssServiceCallback<T> {

        void onLoaded(T episodes);
    }

    void getEpisodes(String feed, int collectionId, String Artist, RssServiceCallback<List<Episode>> callback);

    void getDescription(RssServiceCallback<String> callback);
}
