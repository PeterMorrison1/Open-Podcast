package com.the_canuck.openpodcast.download;

import android.app.DownloadManager;
import android.content.Context;
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
        long enqueue;
        String extension = getMimeType(episode.getMediaUrl());
//        String pathName = null;
//        try {
//            pathName = URLEncoder.encode(episode.getTitle().replaceAll(" ", "")
//                    + "." + FilenameUtils.getExtension(episode.getMediaUrl()), "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String encodedTitle = null;
//        try {
//            encodedTitle = URLEncoder.encode(episode.getTitle(), "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String path = episode.getTitle().replaceAll("/", " ") + "." + FilenameUtils.getExtension(episode.getMediaUrl());

        DownloadManager downloadManager = (DownloadManager) context.getSystemService
                (Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(episode.getMediaUrl()));
        Toast.makeText(context, "Link: " + episode.getMediaUrl(), Toast.LENGTH_LONG).show();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS,
                File.separator + collectionId + File.separator + path);


        request.setMimeType(extension);
        request.setTitle("Downloading " + "test" + ".mp3");
        request.setDescription("Downloading " + "test" + ".mp3");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        enqueue = downloadManager.enqueue(request);

    }

    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
