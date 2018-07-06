package com.the_canuck.openpodcast.media_store;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.the_canuck.openpodcast.Episode;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.HashMap;

public class MediaStoreHelper {
    public static final String DURATION = "duration";
    public static final String SIZE = "size";
    public static final String BOOKMARK = "bookmark";

    /**
     * Creates a cursor for the mediastore that points to the Podcasts folder and holds all
     * media file titles within it.
     *
     * @param context current application context
     * @return cursor containing all media inside Podcasts (in external directory)
     */
    private static Cursor getCursor(Context context) {
        // Creates a cursory to query the mediastore for episode metadata
        return context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[] {"%Podcasts%"},
                MediaStore.Audio.Media.TITLE + " ASC");
    }

    /**
     * Gets the metadata of an episode from the mediastore. Returns hashmap with duration, size,
     * and bookmark.
     *
     * @param context current application context
     * @param episode the current episode being queried
     * @return the hashmap containing episode's duration, size, and bookmark. Null if ep not found
     */
    public static HashMap<String, String> getEpisodeMetaData(Context context, Episode episode) {
        Cursor cursor = getCursor(context);
        HashMap<String, String> podcastMetaData = null;

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String titleNoExtension = FilenameUtils.getBaseName(title);

            // If the title found in mediastore equals the passed in title
            if (titleNoExtension.equalsIgnoreCase(episode.getTitle().replaceAll("/", " "))) {

                String duration = cursor.getString(cursor.getColumnIndexOrThrow
                       (MediaStore.Audio.Media.DURATION));
                String size = cursor.getString(cursor.getColumnIndexOrThrow
                       (MediaStore.Audio.Media.SIZE));
                String bookmark = cursor.getString(cursor.getColumnIndexOrThrow
                       (MediaStore.Audio.Media.BOOKMARK));
                podcastMetaData = new HashMap<String, String>();
                podcastMetaData.put(DURATION, duration);
                podcastMetaData.put(SIZE, size);
                podcastMetaData.put(BOOKMARK, bookmark);
            }
        }
        return podcastMetaData;
    }

    /**
     * Deletes the specified episode from the mediastore database and the media file its associated
     * with.
     *
     * @param context the current application context
     * @param episode the episode to be deleted
     */
    public static void deleteEpisode(Context context, Episode episode) {

        Cursor cursor = getCursor(context);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String titleNoExtension = FilenameUtils.getBaseName(title);
//            String titleNoExtension = title.substring(0, title.lastIndexOf('.'));

            // If the title found in mediastore equals the passed in title
            if (titleNoExtension.equalsIgnoreCase(episode.getTitle().replaceAll("/", " "))) {
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                // Creates the file path to the episode to delete
                String filePath = Environment.getExternalStorageDirectory().getPath()
                        + File.separator + "Podcasts" + File.separator + episode.getCollectionId()
                        + File.separator + title;
                File file = new File(filePath);

                if (file.exists()) {
                    if (file.delete()) {
                        Toast.makeText(context, "File deleted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
                }

                // Deletes the file data from the mediastore database
                context.getContentResolver().delete(uri, null, null);
            }
        }
        cursor.close();
    }
}
