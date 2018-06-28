package com.the_canuck.openpodcast.download;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.the_canuck.openpodcast.Episode;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class DownloadHelper {
    private Episode episode;
    private int collectionId;
    private Context context;
    private long enqueue;
    private DownloadManager downloadManager;

    // TODO: Remove collectionid param, its stored in episode now
    public DownloadHelper(Episode episode, int collectionId, Context context) {
        this.episode = episode;
        this.collectionId = collectionId;
        this.context = context;
    }

    /**
     * Downloads the specified podcast episode with download manager.
     */
    public void downloadEpisode() {

        String mimeType = getMimeType(episode.getMediaUrl());
        String path = episode.getTitle().replaceAll("/", " ") + "."
                + FilenameUtils.getExtension(episode.getMediaUrl());

        downloadManager = (DownloadManager) context.getSystemService
                (Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(episode.getMediaUrl()));
        Toast.makeText(context, "Link: " + episode.getMediaUrl(), Toast.LENGTH_LONG).show();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS,
                File.separator + collectionId + File.separator + path);


        request.setMimeType(mimeType);
        request.setTitle("Downloading " + episode.getTitle());
        request.setDescription("Downloading " + episode.getTitle());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        enqueue = downloadManager.enqueue(request);
    }

    /**
     * Checks if the status of the downloaded is complete or in progress.
     *
     * @return if the download is completed (true) or not (false)
     */
    public boolean isDownloadValid() {
        DownloadManager dm = (DownloadManager) context.getSystemService
                (Context.DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(new DownloadManager.Query().setFilterById(enqueue));

        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            return status == DownloadManager.STATUS_SUCCESSFUL;
        }
        return false;
    }

    /**
     * Gets the MIME type from the passed in url.
     *
     * @param url the file url being downloaded from
     * @return the MIME type as a String
     */
    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
