package com.the_canuck.openpodcast.update_pods;

import android.content.Context;

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
        populateNewEpList();
        startDownloadHelpers();
    }

    /**
     * Creates and starts DownloadHelpers for each episode in the episode list created by
     * populateNewEpList().
     */
    private void startDownloadHelpers() {
        try {
            /* Starts a downloadHelper for each new episode then updates database that this ep is
            downloading. The DownloadReceiver broadcastreceiver should catch and handle the
            onComplete part, but need to still call the DownloadCompleteService.
             */
            for (Episode episode : newEpisodeList) {
                DownloadHelper downloadHelper = new DownloadHelper(episode,
                        context);
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

        RssReader reader = new RssReader();
        RssReaderApi readerApi = new RssReaderApiImpl(reader);

        final List<Podcast> podcastList = sqLiteHelper.getAutoUpdatePods();

        // Checks all autoupdate podcasts episode list for new episodes
        for (final Podcast podcast: podcastList) {

            readerApi.getEpisodes(podcast.getFeedUrl(), podcast.getCollectionId(),
                    podcast.getArtistName(), new RssReaderApi.RssServiceCallback<List<Episode>>() {
                @Override
                public void onLoaded(List<Episode> episodes) {


                    // checks episode list of current podcast for new episodes
                    for (int i = 0; i < episodes.size(); i++) {
                        Episode episode = episodes.get(i);

                        String downloadDate;
                        if (podcast.getNewestDownloadDate() == null) {
                            downloadDate = null;
                        } else {
                            downloadDate = podcast.getNewestDownloadDate();
                        }

                        int position = ListHelper.determineNewerDate(downloadDate,
                                episode.getPubDate());

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
                        startDownloadHelpers();
                    }
                }
            });
        }
    }
}
