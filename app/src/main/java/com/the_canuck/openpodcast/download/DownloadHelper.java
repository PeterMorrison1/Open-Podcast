package com.the_canuck.openpodcast.download;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.the_canuck.openpodcast.Episode;


public class DownloadHelper {
    private Episode episode;
    private Context context;

    public DownloadHelper(Episode episode, Context context) {
        this.episode = episode;
        this.context = context;
    }

    /**
     * Downloads the specified podcast episode with download manager.
     */
    public void downloadEpisode() {
        long enqueue;

        DownloadManager downloadManager = (DownloadManager) context.getSystemService
                (Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(episode.getMediaUrl()));
        Toast.makeText(context, "Link: " + episode.getMediaUrl(), Toast.LENGTH_LONG).show();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS,
                episode.getTitle().replaceAll(" ", "") + ".mp3");
        request.setTitle("Downloading " + "test" + ".mp3");
        request.setDescription("Downloading " + "test" + ".mp3");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        enqueue = downloadManager.enqueue(request);

    }
}
