package com.the_canuck.openpodcast;

import java.io.Serializable;

public class Episode implements Serializable {

    // Constant for serializing episode
    public static final String EPISODE = "com.the_canuck.openpodcast.Episode.episodeKey";

    // Constants for downloadedStatus
    public static final int NOT_DOWNLOADED = 0;
    public static final int IS_DOWNLOADED = 1;
    public static final int CURRENTLY_DOWNLOADING = 2;

    public static final int NOT_LAST_PLAYED = 0;
    public static final int IS_LAST_PLAYED = 1;

    public static final long NO_DOWNLOAD_ID = -1;

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
    private int isLastPlayed = NOT_LAST_PLAYED;
    private long downloadId = NO_DOWNLOAD_ID;

    public Episode() {
        // empty constructor
    }

    public Episode(String title, String description, String mediaUrl) {
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
    }

    /**
     * The id of the download for this file. Known as enqueue from DownloadManager (DownloadHelper).
     *
     * @return the downloadId for this downloaded/downloading file
     */
    public long getDownloadId() {
        return downloadId;
    }

    public Episode setDownloadId(long downloadId) {
        this.downloadId = downloadId;
        return this;
    }

    /**
     * Gets the last played status of the episode. If this episode was playing when the app was
     * closed then isLastPlayed will == 1, else it will == 0.
     * Use NOT_LAST_PLAYED and IS_LAST_PLAYED.
     *
     * @return 0 or 1, based on if the episode was played last or not
     */
    public int getIsLastPlayed() {
        return isLastPlayed;
    }

    public Episode setIsLastPlayed(int isLastPlayed) {
        this.isLastPlayed = isLastPlayed;
        return this;
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
