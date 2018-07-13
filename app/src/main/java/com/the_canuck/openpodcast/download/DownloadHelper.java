package com.the_canuck.openpodcast.download;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.media_store.MediaStoreHelper;

import org.apache.commons.io.FilenameUtils;

import java.io.File;


public class DownloadHelper {

    public final static String STATUS_FAILED = "STATUS_FAILED";
    public final static String STATUS_PAUSED = "STATUS_PAUSED";
    public final static String STATUS_PENDING = "STATUS_PENDING";
    public final static String STATUS_RUNNING = "STATUS_RUNNING";
    public final static String STATUS_SUCCESSFUL = "STATUS_SUCCESSFUL";

    private Episode episode;
    private int collectionId;
    private Context context;
    private long enqueue;
    private DownloadManager downloadManager;
    private String path;

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

        // FIXME: Encode the file name in utf-8!! Must be done before release!
        // Replacing invalid characters was a quick and very dirty hack, will be encoded instead
        String title = episode.getTitle();
        title = title.replaceAll("/", " ");
        title = title.replaceAll("#", " ");

        path = File.separator + collectionId + File.separator
                + title + "."
                + FilenameUtils.getExtension(episode.getMediaUrl());

        downloadManager = (DownloadManager) context.getSystemService
                (Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(episode.getMediaUrl()));
        Toast.makeText(context, "Link: " + episode.getMediaUrl(), Toast.LENGTH_LONG).show();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS,
                path);

        request.setMimeType(mimeType);
        request.setTitle("Downloading " + episode.getTitle());
        request.setDescription("Downloading " + episode.getTitle());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        enqueue = downloadManager.enqueue(request);
    }

    /**
     * Checks the status of the download.
     *
     * @return the status code for the download
     */
    public String getDownloadStatus() {
        DownloadManager dm = (DownloadManager) context.getSystemService
                (Context.DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(new DownloadManager.Query().setFilterById(enqueue));
        String statusText = "";

        if (cursor.moveToFirst()) {
            int mStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (mStatus) {
                case DownloadManager.STATUS_FAILED:
                    statusText = STATUS_FAILED;
                    break;
                case DownloadManager.STATUS_PAUSED:
                    statusText = STATUS_PAUSED;
                    break;
                case DownloadManager.STATUS_PENDING:
                    statusText = STATUS_PENDING;
                    break;
                case DownloadManager.STATUS_RUNNING:
                    statusText = STATUS_RUNNING;
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    statusText = STATUS_SUCCESSFUL;
                    break;
                default:
                    statusText = STATUS_FAILED;
                    break;
            }
        }
        return statusText;
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

    /**
     * Gets the path to the downloaded file (not including the Environment.DIRECTORY_PODCASTS).
     *
     * @return string of the file path
     */
    public String getPath() {
        return path;
    }
}
