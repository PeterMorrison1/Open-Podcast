package com.the_canuck.openpodcast.update_pods;

import android.content.Context;
import android.util.Log;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.download.DownloadHelper;
import com.the_canuck.openpodcast.misc_helpers.ListHelper;
import com.the_canuck.openpodcast.search.RssReader;
import com.the_canuck.openpodcast.search.RssReaderApi;
import com.the_canuck.openpodcast.search.RssReaderApiImpl;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class UpdateHelper {
    private MySQLiteHelper sqLiteHelper;
    private List<Episode> newEpisodeList = new ArrayList<>();
    private Context context;

    private int podcastsParsed = 0;

//    private RssReaderApi readerApi;

    public UpdateHelper(MySQLiteHelper sqLiteHelper, Context context) {
        this.sqLiteHelper = sqLiteHelper;
        this.context = context;
    }

    /**
     * Parse all autoUpdate podcasts for new episodes, then download the episodes.
     */
    public void downloadNewEpisodes() {
        // TODO: Remove all Logs before final release
        populateNewEpList();
        startDownloadHelpers();
    }

    /**
     * Creates and starts DownloadHelpers for each episode in the episode list created by
     * populateNewEpList().
     */
    private void startDownloadHelpers() {
        Log.d("test", "Enter downlodaHelpers");

        try {
            /* Starts a downloadHelper for each new episode then updates database that this ep is
            downloading. The DownloadReceiver broadcastreceiver should catch and handle the
            onComplete part, but need to still call the DownloadCompleteService.
             */
            for (Episode episode : newEpisodeList) {
                Log.d("test", "Enter downlodaHelpers for loop");
                Log.d("test", "Enter downlodaHelpers for loop array size: " + newEpisodeList.size());


                DownloadHelper downloadHelper = new DownloadHelper(episode,
                        episode.getCollectionId(), context);
                long enqueue = downloadHelper.downloadEpisode();
                Log.d("test", "Enter downlodaHelpers start download");


                episode.setDownloadId(enqueue);
                episode.setDownloadStatus(Episode.CURRENTLY_DOWNLOADING);
                sqLiteHelper.addEpisode(episode);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.d("test", "Exit downlodaHelpers");

    }

    /**
     * Creates an arrayList of episodes that are new from podcasts marked as AutoUpdate == 1.
     */
    private void populateNewEpList() {
        Log.d("test", "Enter worker populateneweplist");

        RssReader reader = new RssReader();
        RssReaderApi readerApi = new RssReaderApiImpl(reader);

        final List<Podcast> podcastList = sqLiteHelper.getAutoUpdatePods();
        Log.d("test", "pass updatePods call");

//        podcastList.get(0).setNewestDownloadDate("Mon, 07 May 2018 11:10:28 +0000");
//        sqLiteHelper.updatePodcast(podcastList.get(0), 1);

        // Checks all autoupdate podcasts episode list for new episodes
        for (final Podcast podcast: podcastList) {
            Log.d("test", "Title: " + podcast.getCollectionName());

            readerApi.getEpisodes(podcast.getFeedUrl(), podcast.getCollectionId(),
                    podcast.getArtistName(), new RssReaderApi.RssServiceCallback<List<Episode>>() {
                @Override
                public void onLoaded(List<Episode> episodes) {

                    Log.d("test", "newest date for podcast: " + podcast.getNewestDownloadDate());

                    // checks episode list of current podcast for new episodes
                    for (int i = 0; i < episodes.size(); i++) {
                        Episode episode = episodes.get(i);
                        Log.d("test", "newArray size: " + newEpisodeList.size());
                        Log.d("test", "In for loop after on loaded, title: " + episode.getTitle());
                        Log.d("test", "Episode Pub date: " + episode.getPubDate());

                        String downloadDate;
                        if (podcast.getNewestDownloadDate() == null) {
                            downloadDate = null;
                        } else {
                            downloadDate = podcast.getNewestDownloadDate();
                        }

                        int position = ListHelper.determineNewerDate(downloadDate,
                                episode.getPubDate());
                        Log.d("test", "Position: " + position);


                        // if a new episode, add to newEpisodeList else go to next podcast
                        // Yes, in this instance new episodes would be -1
                        if (position < ListHelper.A_SAMEAS_B) {
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
                    podcastsParsed++;
                    if (podcastsParsed >= podcastList.size() && !podcastList.isEmpty()) {
                        Log.d("test", "---------- DONE ALL PODCAST PARSING ----------");
                        startDownloadHelpers();
                    }
                }
            });
        }
    }
}
