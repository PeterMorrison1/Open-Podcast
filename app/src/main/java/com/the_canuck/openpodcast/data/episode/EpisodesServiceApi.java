package com.the_canuck.openpodcast.data.episode;

import android.content.Context;

import com.the_canuck.openpodcast.Episode;

import java.util.List;

public interface EpisodesServiceApi {

    interface EpisodesServiceCallback<T> {

        void onLoaded(T episodes);
    }

    void getAllEpisodesForCollection(Context context, int collectionId,
                                     EpisodesServiceCallback<List<Episode>> callback);

    void getEpisode(Episode episode, EpisodesServiceCallback<Episode> callback);

    void addEpisode(Episode episode);

    void updateEpisode(Episode episode);

    void deleteEpisode(Episode episode);

    void getLastPlayed(EpisodesServiceCallback<Episode> callback);

    void getNewDownloads(EpisodesServiceCallback<List<Episode>> callback);

}
