package com.the_canuck.openpodcast.search;

import com.the_canuck.openpodcast.Episode;

import java.util.List;

import javax.inject.Inject;

public class RssReaderApiImpl implements RssReaderApi {

    private RssReader reader;

    @Inject
    public RssReaderApiImpl(RssReader reader) {
        this.reader = reader;
    }

    @Override
    public void getEpisodes(final String feed, final int collectionId, final String artist, final RssServiceCallback<List<Episode>> callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(reader.createEpisodeList(feed, collectionId, artist));
            }
        }).start();
    }

    @Override
    public void getDescription(RssServiceCallback<String> callback) {
        callback.onLoaded(reader.getPodcastDescription());
    }
}
