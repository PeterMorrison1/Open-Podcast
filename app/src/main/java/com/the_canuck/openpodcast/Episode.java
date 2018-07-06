package com.the_canuck.openpodcast;

import java.io.Serializable;

public class Episode implements Serializable {

    // Constants for downloadedStatus
    public static final int NOT_DOWNLOADED = 0;
    public static final int IS_DOWNLOADED = 1;
    public static final int CURRENTLY_DOWNLOADING = 2;

    private String title;
    private String description;
    private String artist;
    private String mediaUrl;
    private String length;
    private String link;
    private String pubDate;
    private String duration;
    private int collectionId;
    private int downloadStatus = NOT_DOWNLOADED;
    private String bookmark;

    public Episode() {
        // empty constructor
    }

    public Episode(String title, String description, String mediaUrl) {
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
    }

    /**
     * Gets the bookmarked place (last place media was played) for the episode.
     *
     * @return String of the bookmarked time
     */
    public String getBookmark() {
        return bookmark;
    }

    public Episode setBookmark(String bookmark) {
        this.bookmark = bookmark;
        return this;
    }

    public String getDuration() {
        return duration;
    }

    public Episode setDuration(String duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Download status is if the episode is not downloaded (0), downloaded (1), or downloading (2).
     *
     * @return the int representing the status of the episode
     */
    public int getDownloadStatus() {
        return downloadStatus;
    }

    /**
     * Download status is if the episode is not downloaded (0), downloaded (1), or downloading (2).
     *
     * @param downloadStatus the int representing the status of the episode
     */
    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
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

    public boolean titleEquals(Episode episode) {
        return title.equalsIgnoreCase(episode.getTitle());
    }
}
