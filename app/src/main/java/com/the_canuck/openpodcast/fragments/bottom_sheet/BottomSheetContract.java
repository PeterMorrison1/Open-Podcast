package com.the_canuck.openpodcast.fragments.bottom_sheet;

import android.net.Uri;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.download.DownloadHelperApi;

import java.util.List;

public interface BottomSheetContract {

    interface BottomSheetView {

        // General
        void showLoadingIndicator(boolean active);

        // Data
        void setEpisodeList(List<Episode> episodeList);

        void setPodcastDescription(String description);

        void setNewestDownloadDate();

        // Views
        void populateBottomSheetViews();
        void showSubscribeButton();
        void hideSubscribeButton();

        // Download Manager
        void setDownloadEnqueue(long enqueue);
        void setDownloadStatus(String status);
        void setDownloadUri(Uri uri);

    }

    interface BottomSheetPresenter {

        // BottomSheet view
        void episodeListInstantiator(String feed, int collectionId, String artist);

//        void stop(); // Will be removed when for sure not using asynctask

        void getDescription();


        // Episode specific
        void deleteEpisode(Episode episode);

        // Podcast specific
        void subscribe(Podcast podcast, int autoUpdate);

        void unsubscribe(Podcast podcast);

        void updatePodcast(Podcast podcast, int autoUpdate);


        // Download manager
        void setDownloadHelperApi(DownloadHelperApi downloadHelperApi);

        void startDownload(Episode episode);

        void getDownloadStatus(long enqueue);

        void getDownloadUri(long enqueue);

        void doesPodcastExist(Podcast podcast);

    }
}
