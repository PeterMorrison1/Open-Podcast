package com.the_canuck.openpodcast.search;

import com.the_canuck.openpodcast.Episode;

import java.util.List;

public class RssReaderApiImpl implements RssReaderApi {

    private RssReader reader;

    public RssReaderApiImpl(RssReader reader) {
        this.reader = reader;
    }

    @Override
    public void getEpisodes(final RssServiceCallback<List<Episode>> callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(reader.createEpisodeList());
            }
        }).start();
    }

    @Override
    public void getDescription(RssServiceCallback<String> callback) {
        callback.onLoaded(reader.getPodcastDescription());
    }
}
