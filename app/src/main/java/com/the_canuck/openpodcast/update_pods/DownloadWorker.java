package com.the_canuck.openpodcast.update_pods;

import android.support.annotation.NonNull;
import android.util.Log;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.download.DownloadHelper;
import com.the_canuck.openpodcast.misc_helpers.ListHelper;
import com.the_canuck.openpodcast.search.RssReader;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.work.Worker;

public class DownloadWorker extends Worker {

    private MySQLiteHelper sqLiteHelper;
    private List<Episode> newEpisodeList = new ArrayList<>();

    @NonNull
    @Override
    public Result doWork() {
        Log.d("test", "Enter worker");
        sqLiteHelper = new MySQLiteHelper(getApplicationContext());

        // TODO:                                MUST READ
        // ---------------------------------------------------------------------------------------//
        // Think of ways to split this into concurrent Workers. Maybe update sqlite instead of
        // sending a Data object to the next worker. Since Data object can only hold key pairs.
        // Then one worker updates a bunch of episodes in sqlite as "To be downloaded" then next worker
        // downloads them.
        // ---------------------------------------------------------------------------------------//

        populateNewEpList();
        startDownloadHelpers();

        Log.d("test", "Exit worker");

        // TODO: Maybe set try-catch around method calls to be able to return Result.FAILURE
        return Result.SUCCESS;
    }

    // For testing worker since it's hard to test w/ device probably delete when confirmed no issues
//    private void testNotification() {
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(getApplicationContext(), "56")
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setContentTitle("Worker ran")
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        .setContentText("Update worker");
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "testWorker";
//            String description = "testWorkerNotification";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("56", name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager =
//                    getApplicationContext().getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
//
//        notificationManager.notify(56, builder.build());
//
//    }

    /**
     * Creates and starts DownloadHelpers for each episode in the episode list created by
     * populateNewEpList().
     */
    private void startDownloadHelpers() {
        Log.d("test", "Enter worker downlodaHelpers");

        try {
                    /* Starts a downloadHelper for each new episode then updates database that this ep is
        downloading. The DownloadReceiver broadcastreceiver should catch and handle the
        onComplete part, but need to still call the DownloadCompleteService.
         */
            for (Episode episode : newEpisodeList) {
                DownloadHelper downloadHelper = new DownloadHelper(episode,
                        episode.getCollectionId(), getApplicationContext());
                long enqueue = downloadHelper.downloadEpisode();

                episode.setDownloadId(enqueue);
                episode.setDownloadStatus(Episode.CURRENTLY_DOWNLOADING);
                sqLiteHelper.addEpisode(episode);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates an arrayList of episodes that are new from podcasts marked as AutoUpdate == 1.
     */
    private void populateNewEpList() {
        Log.d("test", "Enter worker populateneweplist");

        List<Podcast> podcastList = sqLiteHelper.getAutoUpdatePods();
        Log.d("test", "pass updatePods call");

        // Checks all autoupdate podcasts episode list for new episodes
        for (Podcast podcast: podcastList) {
            Log.d("test", "Title: " + podcast.getCollectionName());
            RssReader reader = new RssReader(podcast.getFeedUrl());
            reader.setCollectionId(podcast.getCollectionId());
            reader.setCollectionArtist(podcast.getArtistName());

            List<Episode> episodeList = reader.createEpisodeList(podcast.getFeedUrl(), podcast.getCollectionId(), podcast.getArtistName());

            // checks episode list of current podcast for new episodes
            for (int i = 0, episodeListSize = episodeList.size(); i < episodeListSize; i++) {
                Episode episode = episodeList.get(i);

                String downloadDate;
                if (podcast.getNewestDownloadDate() == null) {
                    downloadDate = null;
                } else {
                    downloadDate = podcast.getNewestDownloadDate();
                }

                int position = ListHelper.determineNewerDate(downloadDate,
                        episode.getPubDate());

                // if a new episode, add to newEpisodeList else go to next podcast
                if (position <= ListHelper.A_OLDERTHAN_B) {
                    newEpisodeList.add(episode);

                    // Only sets newest ep as the first episode passed through
                    if (i == 0) {
                        podcast.setNewestDownloadDate(episode.getPubDate());
                        sqLiteHelper.updatePodcast(podcast, Podcast.AUTO_UPDATE_ENABLED);
                    }
                } else {
                    break;
                }
            }
        }

    }
}
