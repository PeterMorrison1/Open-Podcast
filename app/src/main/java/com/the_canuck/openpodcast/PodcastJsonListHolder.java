package com.the_canuck.openpodcast;

import java.util.List;

/**
 * This class is used to hold parsed data from json files using gson. Using Podcast.java would work
 * but would require lots of workarounds and more confusing code. So this class is used to parse
 * json for podcast objects *ONLY*. Then the PodcastJsonHolder or list must be converted to a
 * Podcast.java object or list using createPodcast() in this class.
 */
public class PodcastJsonListHolder {

    private List<PodcastJsonHolder> podcastJsonHolderList;

    public List<PodcastJsonHolder> getPodcastJsonHolderList() {
        return podcastJsonHolderList;
    }

    public PodcastJsonListHolder setPodcastJsonHolderList(List<PodcastJsonHolder> podcastJsonHolderList) {
        this.podcastJsonHolderList = podcastJsonHolderList;
        return this;
    }

    public class PodcastJsonHolder {
        private int collectionId;
        private String collectionName;
        private String artistName;
        private int trackCount;
        private String artworkUrl30;
        private String artworkUrl60;
        private String artworkUrl100;
        private String artworkUrl600;
        private String censoredName;
        private String feedUrl;

        /**
         * Creates a Podcast.java object out of the parameters held by this holder object.
         *
         * @return a Podcast object with the same parameters as the PodcastJsonHolder object
         */
        public Podcast createPodcast() {
            return new Podcast()
                    .setCollectionId(collectionId)
                    .setCollectionName(collectionName)
                    .setArtistName(artistName)
                    .setTrackCount(trackCount)
                    .setArtworkUrl30(artworkUrl30)
                    .setArtworkUrl60(artworkUrl60)
                    .setArtworkUrl100(artworkUrl100)
                    .setArtworkUrl600(artworkUrl600)
                    .setCensoredName(censoredName)
                    .setFeedUrl(feedUrl);
        }

        public PodcastJsonHolder() {
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
    }
}


