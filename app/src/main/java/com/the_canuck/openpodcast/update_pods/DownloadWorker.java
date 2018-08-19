package com.the_canuck.openpodcast.update_pods;

import android.support.annotation.NonNull;

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
    @NonNull
    @Override
    public Result doWork() {
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(getApplicationContext());
        List<Podcast> podcastList;
        List<Episode> newEpisodeList = new ArrayList<>();

        // TODO:                                MUST READ
        // ---------------------------------------------------------------------------------------//
        // Think of ways to split this into concurrent Workers. Maybe update sqlite instead of
        // sending a Data object to the next worker. Since Data object can only hold key pairs.
        // Then one worker updates a bunch of episodes in sqlite as "To be downloaded" then next worker
        // downloads them
        // ---------------------------------------------------------------------------------------//


        try {
            podcastList = sqLiteHelper.getAutoUpdatePods();

            // Checks all autoupdate podcasts episode list for new episodes
            for (Podcast podcast: podcastList) {
                RssReader reader = new RssReader(podcast.getFeedUrl());
                reader.setCollectionId(podcast.getCollectionId());
                reader.setCollectionArtist(podcast.getArtistName());

                List<Episode> episodeList = reader.createEpisodeList();

                // checks episode list of current podcast for new episodes
                for (int i = 0, episodeListSize = episodeList.size(); i < episodeListSize; i++) {
                    Episode episode = episodeList.get(i);

                    int position = ListHelper.determineNewerDate(podcast.getNewestDownloadDate(),
                            episode.getPubDate());

                    // if a new episode, add to newEpisodeList else go to next podcast
                    if (position <= ListHelper.A_OLDERTHAN_B) {
                        newEpisodeList.add(episode);
                    } else {
                        break;
                    }
                }
            }

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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
