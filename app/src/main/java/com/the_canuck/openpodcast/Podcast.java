package com.the_canuck.openpodcast;

import android.support.v7.widget.RecyclerView;


import java.io.Serializable;
import java.util.ArrayList;

public class Podcast implements Serializable {

    // Constant for serializing podcast
    public static final String PODCAST = "com.the_canuck.openpodcast.Podcast.podcastKey";

    public static final int AUTO_UPDATE_ENABLED = 1;
    public static final int AUTO_UPDATE_DISABLED = 0;

    private int collectionId;
    private String collectionName;
    private String artistName;
    private int trackCount;
    private String artworkUrl30;
    private String artworkUrl60;
    private String artworkUrl100;
    private String artworkUrl600;
    private String censoredName;
    private int groupingGenre;
    private String feedUrl;
    private RecyclerView recyclerView;
    private int position;
    private String newestDownloadDate;

    public Podcast() {
    }

//    public Podcast(PodcastBuilder builder) {
//        collectionId = builder.collectionId;
//        collectionName = builder.collectionName;
//        artistName = builder.artistName;
//        trackCount = builder.trackCount;
//        artworkUrl30 = builder.artworkUrl30;
//        artworkUrl60 = builder.artworkUrl60;
//        artworkUrl100 = builder.artworkUrl100;
//        artworkUrl600 = builder.artworkUrl600;
//        censoredName = builder.censoredName;
//        feedUrl = builder.feedUrl;
//        newestDownloadDate = builder.newestDownloadDate;
//    }


    public Podcast setCollectionId(int collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    public Podcast setCollectionName(String collectionName) {
        this.collectionName = collectionName;
        return this;
    }

    public Podcast setArtistName(String artistName) {
        this.artistName = artistName;
        return this;
    }

    public Podcast setTrackCount(int trackCount) {
        this.trackCount = trackCount;
        return this;
    }

    public Podcast setArtworkUrl30(String artworkUrl30) {
        this.artworkUrl30 = artworkUrl30;
        return this;
    }

    public Podcast setArtworkUrl60(String artworkUrl60) {
        this.artworkUrl60 = artworkUrl60;
        return this;
    }

    public Podcast setArtworkUrl100(String artworkUrl100) {
        this.artworkUrl100 = artworkUrl100;
        return this;
    }

    public Podcast setArtworkUrl600(String artworkUrl600) {
        this.artworkUrl600 = artworkUrl600;
        return this;
    }

    public Podcast setCensoredName(String censoredName) {
        this.censoredName = censoredName;
        return this;
    }

    public Podcast setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
        return this;
    }

    /**
     * Returns the newest download date, which is the newest episode for the podcast that has been
     * synced by the DownloadWorker.
     *
     * @return a String of the last date the podcast has been updated
     */
    public String getNewestDownloadDate() {
        return newestDownloadDate;
    }

    public Podcast setNewestDownloadDate(String newestDownloadDate) {
        this.newestDownloadDate = newestDownloadDate;
        return this;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public Podcast setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public Podcast setPosition(int position) {
        this.position = position;
        return this;
    }

    public int getGroupingGenre() {
        return groupingGenre;
    }

    public Podcast setGroupingGenre(int groupingGenre) {
        this.groupingGenre = groupingGenre;
        return this;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getArtistName() {
        return artistName;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public String getArtworkUrl30() {
        return artworkUrl30;
    }

    public String getArtworkUrl60() {
        return artworkUrl60;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public String getArtworkUrl600() {
        return artworkUrl600;
    }

    public String getCensoredName() {
        return censoredName;
    }

    public String getFeedUrl() {
        return feedUrl;
    }
//
//    public static class PodcastBuilder {
//        String collectionName, artistName, artworkUrl30, artworkUrl60, artworkUrl100,
//                artworkUrl600, censoredName, feedUrl, newestDownloadDate;
//        int trackCount, collectionId;
//
//        public String getNewestDownloadDate() {
//            return newestDownloadDate;
//        }
//
//        public PodcastBuilder setNewestDownloadDate(String newestDownloadDate) {
//            this.newestDownloadDate = newestDownloadDate;
//            return this;
//        }
//
//        public PodcastBuilder setCollectionId(int collectionId) {
//            this.collectionId = collectionId;
//            return this;
//        }
//
//        public PodcastBuilder setCollectionName(String collectionName) {
//            this.collectionName = collectionName;
//            return this;
//        }
//
//        public PodcastBuilder setArtistName(String artistName) {
//            this.artistName = artistName;
//            return this;
//        }
//
//        public PodcastBuilder setArtworkUrl30(String artworkUrl30) {
//            this.artworkUrl30 = artworkUrl30;
//            return this;
//        }
//
//        public PodcastBuilder setArtworkUrl60(String artworkUrl60) {
//            this.artworkUrl60 = artworkUrl60;
//            return this;
//        }
//
//        public PodcastBuilder setArtworkUrl100(String artworkUrl100) {
//            this.artworkUrl100 = artworkUrl100;
//            return this;
//        }
//
//        public PodcastBuilder setArtworkUrl600(String artworkUrl600) {
//            this.artworkUrl600 = artworkUrl600;
//            return this;
//        }
//
//        public PodcastBuilder setTrackCount(int trackCount) {
//            this.trackCount = trackCount;
//            return this;
//        }
//
//        public PodcastBuilder setCensoredName(String censoredName) {
//            this.censoredName = censoredName;
//            return this;
//        }
//
//        public PodcastBuilder setFeedUrl(String feedUrl) {
//            this.feedUrl = feedUrl;
//            return this;
//        }
//
//        public Podcast build() {
//            return new Podcast(this);
//        }
//    }
    @Override
    public String toString() {
        return "Podcast{" +
                "collectionId='" + collectionId + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", trackCount=" + trackCount +
                ", artworkUrl30='" + artworkUrl30 + '\'' +
                ", artworkUrl60='" + artworkUrl60 + '\'' +
                ", artworkUrl100='" + artworkUrl100 + '\'' +
                ", artworkUrl600='" + artworkUrl600 + '\'' +
                ", censoredName='" + censoredName + '\'' +
                ", feedUrl='" + feedUrl + '\'' +
                ", newestDate='" + newestDownloadDate + '\'' +
                '}';
    }
}
