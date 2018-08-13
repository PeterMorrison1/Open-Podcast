package com.the_canuck.openpodcast.download;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.fragments.settings.PreferenceKeys;
import com.the_canuck.openpodcast.media_store.MediaStoreHelper;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.List;

public class DownloadCompleteService extends IntentService {

    public DownloadCompleteService() {
        super("DownloadCompleteService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DownloadHelper downloadHelper = new DownloadHelper();
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences(PreferenceKeys.PREF_DOWNLOADS, MODE_PRIVATE);

        List<Episode> episodeList = sqLiteHelper.getNewDownloadEpisodes();

        // Uses each episode's downloadId as a key in SharedPreferences to get download status
        for (Episode episode : episodeList) {
            String id = String.valueOf(episode.getDownloadId());
            String status = prefs.getString(id, "null");

            if (!status.equalsIgnoreCase("null")) {
                updateDownloadedEpisodes(status, id, episode, sqLiteHelper, downloadHelper);

                // remove the just completed id from shared preferences
                prefs.edit().remove(id).commit();
            }
        }

        /* Once all files are done being added to sqlite the finished downloads is set to false
        so it won't keep trying to update on start up.
         */
        prefs.edit().putBoolean(PreferenceKeys.IS_FINISHED_DOWNLOADS, false).apply();
    }

    /**
     * Updates the sqlite database to show the episode was downloaded and revert it's downloadId
     * back to -1, and run an ACTION_MEDIA_SCANNER_SCAN_FILE.
     * <P>
     *     If download was not STATUS_SUCCESSFUL it will be deleted from the SQLite database.
     * </P>
     *
     * @param status the status returned from DownloadManager (getStatus in DownloadHelper)
     * @param id The id/enqueue from DownloadManager (DownloadHelper)
     * @param episode the episode object being updated
     * @param sqLiteHelper the sqlite object for MySqliteHelper
     * @param downloadHelper the DownloadHelper object
     */
    private void updateDownloadedEpisodes(String status, String id, Episode episode,
                                          MySQLiteHelper sqLiteHelper,
                                          DownloadHelper downloadHelper) {
        try {
            if (status != null && episode != null) {
                if (status.equalsIgnoreCase(DownloadHelper.STATUS_SUCCESSFUL)) {
                    // Update download status and update the episode in sqlite
                    episode.setDownloadStatus(Episode.IS_DOWNLOADED);

                    // Reset download id to -1 (aka no download id)
                    episode.setDownloadId(-1);

                    // Update podcast info if it doesn't have duration or size yet
                    sqLiteHelper.updateEpisode(MediaStoreHelper
                            .updateEpisodeMetaData(getApplicationContext(),
                                    episode));

                    getApplicationContext().sendBroadcast(new Intent
                            (Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.parse(Environment.DIRECTORY_PODCASTS
                                            + downloadHelper.getPath())));
                } else {
                    // Handles canceled and failed downloads
                    Uri uri;
                    long downloadId = Long.valueOf(id);

                    uri = downloadHelper.getDownloadUri(downloadId);

                    /* uri is null when the download is canceled, which allows
                    us to check if the action is for successful download or a
                    canceled download
                     */
                    if (uri == null) {
                        sqLiteHelper.deleteEpisode(episode);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
