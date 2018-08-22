package com.the_canuck.openpodcast.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.misc_helpers.ListHelper;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database name & version
    private static final String DATABASE_NAME = "podcasts.db";
    private static final int DATABASE_VERSION = 10;

    // tables
    private static final String TABLE_SUBSCRIBED = "subscribed";
    private static final String TABLE_EPISODES = "episodes";

    // common columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_COLLECTION_ID = "collection_id";
    private static final String COLUMN_ARTIST = "artist";

    // subscribed table columns
    private static final String COLUMN_CENSORED_TITLE = "censored_title";
    private static final String COLUMN_FEED_URL = "feed_url";
    private static final String COLUMN_ART_100 = "art_100";
    private static final String COLUMN_ART_600 = "art_600";
    private static final String COLUMN_AUTO_UPDATE = "auto_update";
    private static final String COLUMN_NEWEST_DOWNLOAD = "newest_download";

    // episodes table columns
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PUB_DATE = "pub_date";
    private static final String COLUMN_DOWNLOADED = "downloaded";
    private static final String COLUMN_FILE_SIZE = "file_size";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_MEDIA_URL = "media_url";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_BOOKMARK = "bookmark";
    private static final String COLUMN_DOWNLOAD_ID = "download_id";
    private static final String COLUMN_LAST_PLAYED = "last_played";

    private static final String CREATE_SUB_TABLE = "CREATE TABLE " + TABLE_SUBSCRIBED + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_CENSORED_TITLE + " TEXT,"
            + COLUMN_ARTIST + " TEXT,"
            + COLUMN_COLLECTION_ID + " INTEGER,"
            + COLUMN_FEED_URL + " TEXT,"
            + COLUMN_ART_100 + " TEXT,"
            + COLUMN_ART_600 + " TEXT,"
            + COLUMN_AUTO_UPDATE + " INTEGER,"
            + COLUMN_NEWEST_DOWNLOAD + " TEXT"
            + ")";

    private static final String CREATE_EPISODE_TABLE = "CREATE TABLE " + TABLE_EPISODES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_COLLECTION_ID + " INTEGER,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_PUB_DATE + " TEXT,"
            + COLUMN_DOWNLOADED + " INTEGER,"
            + COLUMN_FILE_SIZE + " TEXT,"
            + COLUMN_LINK + " TEXT,"
            + COLUMN_MEDIA_URL + " TEXT,"
            + COLUMN_DURATION + " TEXT,"
            + COLUMN_ARTIST + " TEXT,"
            + COLUMN_DOWNLOAD_ID + " INTEGER,"
            + COLUMN_LAST_PLAYED + " INTEGER,"
            + COLUMN_BOOKMARK + " TEXT"
            + ")";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // TODO: Update MySQLiteHelper to be a singleton, just found out this is best practice
    // Alternatively could use content providers, but that isn't as easy to refactor as singleton

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SUB_TABLE);
        db.execSQL(CREATE_EPISODE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIBED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EPISODES);

        onCreate(db);
    }

    // TODO: Think about making separate helper classes to handle the different tables
    // SUBSCRIBED TABLE HELPER METHODS
    /**
     * Called to add a podcast to the subscription list.
     *
     * @param podcast specific podcast being subscribed to
     * @param autoUpdate if the new episodes will be automatically downloaded (see podcast constants)
     */
    public void subscribe(Podcast podcast, int autoUpdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, podcast.getCollectionName());
        contentValues.put(COLUMN_CENSORED_TITLE, podcast.getCensoredName());
        contentValues.put(COLUMN_ARTIST, podcast.getArtistName());
        contentValues.put(COLUMN_COLLECTION_ID, podcast.getCollectionId());
        contentValues.put(COLUMN_FEED_URL, podcast.getFeedUrl());
        contentValues.put(COLUMN_ART_100, podcast.getArtworkUrl100());
        contentValues.put(COLUMN_ART_600, podcast.getArtworkUrl600());
        contentValues.put(COLUMN_NEWEST_DOWNLOAD, podcast.getNewestDownloadDate());

        // autoUpdate must be passed in and will be either 0 or 1 (bool)
        contentValues.put(COLUMN_AUTO_UPDATE, autoUpdate);

        db.insert(TABLE_SUBSCRIBED, null, contentValues);
    }

    /**
     * Called to remove a podcast from the subscription list.
     *
     * @param podcast specific podcast being unsubscribbed from
     */
    public void unsubscribe(Podcast podcast) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBSCRIBED, COLUMN_COLLECTION_ID + "=?",
                new String[]{String.valueOf(podcast.getCollectionId())});
        db.close();
    }


    /**
     * Called to update a podcast in the subscription list if meta data changes or auto download.
     *
     * @param podcast specific podcast being updated
     * @param autoUpdate if the new episodes will be automatically downloaded (see Podcast constants)
     */
    public void updatePodcast(Podcast podcast, int autoUpdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, podcast.getCollectionName());
        contentValues.put(COLUMN_CENSORED_TITLE, podcast.getCensoredName());
        contentValues.put(COLUMN_ARTIST, podcast.getArtistName());
        contentValues.put(COLUMN_COLLECTION_ID, podcast.getCollectionId());
        contentValues.put(COLUMN_FEED_URL, podcast.getFeedUrl());
        contentValues.put(COLUMN_ART_100, podcast.getArtworkUrl100());
        contentValues.put(COLUMN_ART_600, podcast.getArtworkUrl600());
        contentValues.put(COLUMN_NEWEST_DOWNLOAD, podcast.getNewestDownloadDate());

        // autoUpdate must be passed in and will be either 0 or 1 (bool)
        contentValues.put(COLUMN_AUTO_UPDATE, autoUpdate);

        db.update(TABLE_SUBSCRIBED, contentValues, COLUMN_COLLECTION_ID + "=?",
                new String[]{Integer.toString(podcast.getCollectionId())});
        db.close();
    }

    /**
     * Creates and returns a list of all podcasts currently subscribed to for the user.
     *
     * @return list of podcasts currently subscribbed to
     */
    public List<Podcast> getSubscribedPodcasts() {
        List<Podcast> podcasts = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_SUBSCRIBED, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Podcast podcast = buildPodcast(cursor);

            podcasts.add(podcast);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return podcasts;
    }

    /**
     * Checks if a podcast object exists in the database (subscribed to it).
     *
     * @param podcast the podcast object being queried
     * @return a boolean if it is subscribed to or not
     */
    public boolean doesPodcastExist(Podcast podcast) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean podcastExists;

        // check subscribed table for matching podcast collection IDs
        String query = "select " + COLUMN_COLLECTION_ID + " from " +
                TABLE_SUBSCRIBED + " where " + COLUMN_COLLECTION_ID + "= '" +
                podcast.getCollectionId() + "'";
        Cursor cursor = db.rawQuery(query, null);

        // if matching collection id return true
        podcastExists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return podcastExists;
    }

    /**
     * Searches podcast table for the matching podcast and returns the artwork 600 link.
     *
     * @param collectionId the podcast collectionId to search for
     * @return the artwork 600 link for queried podcast
     */
    public String getPodcastArtwork600(int collectionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String artwork = "";
        String query = "select * from " + TABLE_SUBSCRIBED + " where "
                + COLUMN_COLLECTION_ID + "='" + collectionId + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            artwork = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ART_600));
        }
        cursor.close();
        db.close();
        return artwork;
    }

    /**
     * Creates a list of podcasts that have an auto update status to them.
     *
     * @return list of podcasts that are to be auto updated
     */
    public List<Podcast> getAutoUpdatePods() {
        SQLiteDatabase db = getWritableDatabase();
        List<Podcast> podcastList = new ArrayList<>();

        String query = "select * from " + TABLE_SUBSCRIBED + " where "
                + COLUMN_AUTO_UPDATE + "='" + Podcast.AUTO_UPDATE_ENABLED + "'";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            podcastList.add(buildPodcast(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return podcastList;
    }

    /**
     * Builds a podcast object using the provided cursor.
     *
     * @param cursor the cursor holding the podcast data
     * @return the podcast object
     */
    private Podcast buildPodcast(Cursor cursor) {
        Podcast podcast;

        podcast = new Podcast.PodcastBuilder()
                .setCollectionName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)))
                .setCensoredName(cursor.getString
                        (cursor.getColumnIndexOrThrow(COLUMN_CENSORED_TITLE)))
                .setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST)))
                .setCollectionId(cursor.getInt
                        (cursor.getColumnIndexOrThrow(COLUMN_COLLECTION_ID)))
                .setFeedUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEED_URL)))
                .setArtworkUrl100(cursor.getString
                        (cursor.getColumnIndexOrThrow(COLUMN_ART_100)))
                .setArtworkUrl600(cursor.getString
                        (cursor.getColumnIndexOrThrow(COLUMN_ART_600)))
                .setNewestDownloadDate((cursor.getString
                        (cursor.getColumnIndexOrThrow(COLUMN_NEWEST_DOWNLOAD))))
                .build();

        return podcast;
    }


    // EPISODES TABLE HELPER METHODS
    /**
     * Called to add an episode into the episodes list.
     *
     * @param episode the episode to be added into the episodes table
     */
    public void addEpisode(Episode episode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, episode.getTitle());
        contentValues.put(COLUMN_COLLECTION_ID, episode.getCollectionId());
        contentValues.put(COLUMN_DESCRIPTION, episode.getDescription());
        contentValues.put(COLUMN_PUB_DATE, episode.getPubDate());
        contentValues.put(COLUMN_DOWNLOADED, episode.getDownloadStatus());
        contentValues.put(COLUMN_FILE_SIZE, episode.getLength());
        contentValues.put(COLUMN_LINK, episode.getLink());
        contentValues.put(COLUMN_MEDIA_URL, episode.getMediaUrl());
        contentValues.put(COLUMN_DURATION, episode.getDuration());
        contentValues.put(COLUMN_ARTIST, episode.getArtist());
        contentValues.put(COLUMN_DOWNLOAD_ID, episode.getDownloadId());
        contentValues.put(COLUMN_LAST_PLAYED, episode.getIsLastPlayed());
        contentValues.put(COLUMN_BOOKMARK, episode.getBookmark());

        db.insert(TABLE_EPISODES, null, contentValues);
        db.close();
    }

    /**
     * Updates the episode's row in the episodes table.
     *
     * @param episode the episode to be updated
     */
    public void updateEpisode(Episode episode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_TITLE, episode.getTitle());
        contentValues.put(COLUMN_COLLECTION_ID, episode.getCollectionId());
        contentValues.put(COLUMN_DESCRIPTION, episode.getDescription());
        contentValues.put(COLUMN_PUB_DATE, episode.getPubDate());
        contentValues.put(COLUMN_DOWNLOADED, episode.getDownloadStatus());
        contentValues.put(COLUMN_FILE_SIZE, episode.getLength());
        contentValues.put(COLUMN_LINK, episode.getLink());
        contentValues.put(COLUMN_MEDIA_URL, episode.getMediaUrl());
        contentValues.put(COLUMN_DURATION, episode.getDuration());
        contentValues.put(COLUMN_ARTIST, episode.getArtist());
        contentValues.put(COLUMN_DOWNLOAD_ID, episode.getDownloadId());
        contentValues.put(COLUMN_LAST_PLAYED, episode.getIsLastPlayed());
        contentValues.put(COLUMN_BOOKMARK, episode.getBookmark());

        db.update(TABLE_EPISODES, contentValues, COLUMN_TITLE + "=?",
                new String[]{episode.getTitle()});
        db.close();
    }

    /**
     * Removes an episode from the episodes table with the matching title.
     *
     * @param episode the downloaded episode to be deleted
     */
    public void deleteEpisode(Episode episode) {
        // TODO: Try and see if there's better way than matching title. Even matching title & size?
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EPISODES, COLUMN_TITLE + "=?",
                new String[]{episode.getTitle()});
        db.close();
    }

    /**
     * Searches the episodes table for the last played episode.
     *
     * @return the episode that was last played
     */
    public Episode getLastPlayedEpisode() {
        SQLiteDatabase db = this.getWritableDatabase();
        Episode episode = new Episode();

        String query = "select * from " + TABLE_EPISODES + " where " + COLUMN_LAST_PLAYED + "='"
                + Episode.IS_LAST_PLAYED + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            episode = buildEpisode(cursor);
        }
        cursor.close();
        db.close();
        return episode;
    }

    /**
     * Gets an episode from its downloadId/enqueue.
     *
     * @param downloadId the enqueue/downloadId of the episode
     * @return the episode object associated with the downloadId/enqueue
     */
    public Episode getEpisodeByDownloadId(long downloadId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Episode episode = new Episode();

        String query = "select * from " + TABLE_EPISODES + " where " + COLUMN_DOWNLOAD_ID + "='"
                + downloadId + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            episode = buildEpisode(cursor);
        }
        cursor.close();
        db.close();
        return episode;
    }

    /**
     * Searches TABLE_EPISODES for all episodes that have a non "-1" value for downloadId, meaning
     * they are currently being downloaded or finihsed downloading and waiting to be updated in
     * TABLE_EPISODES.
     *
     * @return list of currently or just finished downloading episodes
     */
    public List<Episode> getNewDownloadEpisodes() {
        List<Episode> episodes = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();

        String query = "select * from " + TABLE_EPISODES + " where " + COLUMN_DOWNLOAD_ID + "!='"
                + Episode.NO_DOWNLOAD_ID + "'";

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Episode episode = buildEpisode(cursor);
            episodes.add(episode);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return episodes;
    }

    /**
     * Gets all episodes meta data from episodes table as a list.
     *
     * @param collectionId the podcast id assigned by itunes
     * @return arraylist of episodes
     */
    public List<Episode> getEpisodes(int collectionId) {
        /* TODO: If I add non-itunes support there will need to be an adjustment since collection id
             is an itunes variable. Probably an if statement inside this method
         */
        List<Episode> episodes = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String mCollectionId = Integer.toString(collectionId);

        Cursor cursor = db.rawQuery("select * from " + TABLE_EPISODES + " where "
                + COLUMN_COLLECTION_ID + "='" + mCollectionId + "'", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Episode episode = buildEpisode(cursor);

            // Gets the position the episode belongs in (sorted by pubDate)
            int index = ListHelper.getSortedIndex(episode.getPubDate(), episodes);

            // determines position based on index returned by sorted index
            if (episodes.size() == 0) {
                episodes.add(episode);
            } else if (index == episodes.size()){
                episodes.add(episode);
            } else if (index == -1) {
                episodes.add(episode);
            } else {
                episodes.add(index, episode);
            }
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return episodes;
    }

    /**
     * Builds an Episode object using the passed in cursor data.
     *
     * @param cursor the cursor containing the episode query
     * @return the Episode object being built
     */
    private Episode buildEpisode(Cursor cursor) {
        Episode episode = new Episode();
        if (cursor != null) {
            episode.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            episode.setCollectionId(cursor.getInt
                    (cursor.getColumnIndexOrThrow(COLUMN_COLLECTION_ID)));
            episode.setDescription(cursor.getString
                    (cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            episode.setPubDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUB_DATE)));
            episode.setLength(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_SIZE)));
            episode.setDownloadStatus(cursor.getInt
                    (cursor.getColumnIndexOrThrow(COLUMN_DOWNLOADED)));
            episode.setLink(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LINK)));
            episode.setMediaUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDIA_URL)));
            episode.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
            episode.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST)));
            episode.setDownloadId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DOWNLOAD_ID)));
            episode.setIsLastPlayed(cursor.getInt
                    (cursor.getColumnIndexOrThrow(COLUMN_LAST_PLAYED)));
            episode.setBookmark(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOOKMARK)));
        }
        return episode;
    }
}
