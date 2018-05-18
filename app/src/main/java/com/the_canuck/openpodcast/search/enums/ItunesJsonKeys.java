package com.the_canuck.openpodcast.search.enums;

public enum ItunesJsonKeys {
    KIND("kind"),
    ARTISTID("artistId"),
    COLLECTIONID("collectionId"),
    TRACKID("tackId"),
    ARTISTNAME("artistName"),
    COLLECTIONNAME("collectionName"),
    TRACKNAME("trackName"),
    COLLECTIONCENSOREDNAME("collectionCensoredName"),
    TRACKCENSOREDNAME("trackCensoredName"),
    ARTISTVIEWURL("artistViewUrl"),
    COLLECTIONVIEWURL("collectionViewUrl"),
    FEEDURL("feedUrl"),
    TRACKVIEWURL("trackViewUrl"),
    ARTWORKURL30("artworkUrl30"),
    ARTWORKURL60("artworkUrl60"),
    ARTWORKURL100("artworkUrl100"),
    ARTWORKURL600("artworkUrl600"),
    COLLECTIONPRICE("collectionPrice"),
    TRACKPRICE("trackPrice"),
    TRACKRENTALPRICE("trackRetnalPrice"),
    COLLECTIONHDPRICE("collectionHdPrice"),
    RELEASEDATE("releaseDate"),
    COLLECTIONEXPLICITNESS("collectionExplicitness"),
    TRACKCOUNT("trackCount"),
    COUNTRY("country"),
    CURRENCY("currency"),
    PRIMARYGENRE("primaryGenreName"),
    ADVISORYRATING("contentAdvisoryRating"),
    GENREIDS("genreIds"),
    GENRES("genres");

    private final String value;
    ItunesJsonKeys(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
