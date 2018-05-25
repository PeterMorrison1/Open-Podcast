package com.the_canuck.openpodcast;

public class Podcast {
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


    public Podcast(PodcastBuilder builder) {
        collectionId = builder.collectionId;
        collectionName = builder.collectionName;
        artistName = builder.artistName;
        trackCount = builder.trackCount;
        artworkUrl30 = builder.artworkUrl30;
        artworkUrl60 = builder.artworkUrl60;
        artworkUrl100 = builder.artworkUrl100;
        artworkUrl600 = builder.artworkUrl600;
        censoredName = builder.censoredName;
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

    public static class PodcastBuilder {
        String collectionName, artistName, artworkUrl30, artworkUrl60, artworkUrl100,
                artworkUrl600, censoredName;
        int trackCount, collectionId;

//        public PodcastBuilder(int collectionId, String collectionName, String artistName, String artworkUrl30, String artworkUrl60, String artworkUrl100, String artworkUrl600, int trackCount) {
//            this.collectionId = collectionId;
//            this.collectionName = collectionName;
//            this.artistName = artistName;
//            this.artworkUrl30 = artworkUrl30;
//            this.artworkUrl60 = artworkUrl60;
//            this.artworkUrl100 = artworkUrl100;
//            this.artworkUrl600 = artworkUrl600;
//            this.trackCount = trackCount;
//        }

        public PodcastBuilder setCollectionId(int collectionId) {
            this.collectionId = collectionId;
            return this;
        }

        public PodcastBuilder setCollectionName(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }

        public PodcastBuilder setArtistName(String artistName) {
            this.artistName = artistName;
            return this;
        }

        public PodcastBuilder setArtworkUrl30(String artworkUrl30) {
            this.artworkUrl30 = artworkUrl30;
            return this;
        }

        public PodcastBuilder setArtworkUrl60(String artworkUrl60) {
            this.artworkUrl60 = artworkUrl60;
            return this;
        }

        public PodcastBuilder setArtworkUrl100(String artworkUrl100) {
            this.artworkUrl100 = artworkUrl100;
            return this;
        }

        public PodcastBuilder setArtworkUrl600(String artworkUrl600) {
            this.artworkUrl600 = artworkUrl600;
            return this;
        }

        public PodcastBuilder setTrackCount(int trackCount) {
            this.trackCount = trackCount;
            return this;
        }

        public PodcastBuilder setCensoredName(String censoredName) {
            this.censoredName = censoredName;
            return this;
        }

        public Podcast build() {
            return new Podcast(this);
        }
    }
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
                '}';
    }
}
