package com.the_canuck.openpodcast;

public class Episode {
    private String title;
    private String description;
    private String artist;
    private String mediaUrl;
    private int length;
    private String link;
    private String pubDate;

    public Episode() {
        // empty constructor
    }

    public Episode(String title, String description, String mediaUrl) {
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
    }

    public String getTitle() {
        return title;
    }

    public Episode setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Episode setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public Episode setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public Episode setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
        return this;
    }

    public int getLength() {
        return length;
    }

    public Episode setLength(int length) {
        this.length = length;
        return this;
    }

    public String getLink() {
        return link;
    }

    public Episode setLink(String link) {
        this.link = link;
        return this;
    }

    public String getPubDate() {
        return pubDate;
    }

    public Episode setPubDate(String pubDate) {
        this.pubDate = pubDate;
        return this;
    }
}
