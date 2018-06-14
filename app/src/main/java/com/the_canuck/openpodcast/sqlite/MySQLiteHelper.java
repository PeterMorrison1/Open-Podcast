package com.the_canuck.openpodcast.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.the_canuck.openpodcast.Podcast;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database name & version
    private static final String DATABASE_NAME = "podcasts.db";
    private static final int DATABASE_VERSION = 1;

    // subscribed podcasts table & columns
    private static final String TABLE_SUBSCRIBED = "subscribed";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CENSORED_TITLE = "censored_title";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_COLLECTION_ID = "collection_id";
    private static final String COLUMN_FEED_URL = "feed_url";
    private static final String COLUMN_ART_100 = "art_100";
    private static final String COLUMN_ART_600 = "art_600";
    private static final String COLUMN_AUTO_UPDATE = "auto_update";

    private static final String CREATE_SUB_TABLE = "CREATE TABLE " + TABLE_SUBSCRIBED + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_CENSORED_TITLE + " TEXT,"
            + COLUMN_ARTIST + " TEXT,"
            + COLUMN_COLLECTION_ID + " INTEGER,"
            + COLUMN_FEED_URL + " TEXT,"
            + COLUMN_ART_100 + " TEXT,"
            + COLUMN_ART_600 + " TEXT,"
            + COLUMN_AUTO_UPDATE + " INTEGER"
            + ")";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SUB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIBED);
        onCreate(db);
    }

    /**
     * Called to add a podcast to the subscription list.
     *
     * @param podcast specific podcast being subscribed to
     * @param autoUpdate if the new episodes will be automatically downloaded (0 or 1)
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
    }

    /**
     * Called to update a podcast in the subscription list if meta data changes or auto download.
     *
     * @param podcast specific podcast being updated
     * @param autoUpdate if the new episodes will be automatically downloaded (0 or 1)
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

        // autoUpdate must be passed in and will be either 0 or 1 (bool)
        contentValues.put(COLUMN_AUTO_UPDATE, autoUpdate);

        db.update(TABLE_SUBSCRIBED, contentValues, COLUMN_ID + "=?",
                new String[]{Integer.toString(podcast.getCollectionId())});
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
            Podcast podcast = new Podcast.PodcastBuilder()
                    .setCollectionName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)))
                    .setCensoredName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CENSORED_TITLE)))
                    .setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ARTIST)))
                    .setCollectionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COLLECTION_ID)))
                    .setFeedUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEED_URL)))
                    .setArtworkUrl100(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ART_100)))
                    .setArtworkUrl600(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ART_600)))
                    .build();

            podcasts.add(podcast);
            cursor.moveToNext();
        }
        cursor.close();
        return podcasts;
    }

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
        return podcastExists;
    }
}
