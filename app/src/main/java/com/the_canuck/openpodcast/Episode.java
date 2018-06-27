package com.the_canuck.openpodcast;

public class Episode {
    private String title;
    private String description;
    private String artist;
    private String mediaUrl;
    private String length;
    private String link;
    private String pubDate;
    private int collectionId;
    private String titleKey;
    private boolean downloaded = false;

    public Episode() {
        // empty constructor
    }

    public Episode(String title, String description, String mediaUrl) {
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public Episode setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
        return this;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public Episode setTitleKey(String titleKey) {
        this.titleKey = titleKey;
        return this;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public Episode setCollectionId(int collectionId) {
        this.collectionId = collectionId;
        return this;
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

    public String getLength() {
        return length;
    }

    public Episode setLength(String length) {
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
