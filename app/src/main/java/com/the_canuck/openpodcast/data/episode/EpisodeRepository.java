package com.the_canuck.openpodcast.data.episode;

import android.support.annotation.NonNull;

import com.the_canuck.openpodcast.Episode;

import java.util.List;

public interface EpisodeRepository {

    interface LoadEpisodesCallback {

        void onEpisodesLoaded(List<Episode> episodes);

    }

    interface GetEpisodeCallback {

        void onEpisodeLoaded(Episode episode);
    }

    interface GetStringCallback {

        void onStringReturned(String string);
    }

    void getAllEpisodesSorted(LoadEpisodesCallback callback);

    void getDownloadedEpisodes(LoadEpisodesCallback callback);

    void getNonDownloadedEpisodes(LoadEpisodesCallback callback);

    void addEpisode(Episode episode);

    void updateEpisode(Episode episode);

    void deleteEpisode(Episode episode);

    void getLastPlayed(GetEpisodeCallback callback);

    void getNewDownloads(LoadEpisodesCallback callback);

    void refreshData();

    void getDescription(GetStringCallback callback);
}
