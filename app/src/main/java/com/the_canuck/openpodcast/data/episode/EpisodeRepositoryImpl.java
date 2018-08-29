package com.the_canuck.openpodcast.data.episode;

import android.content.Context;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.fragments.bottom_sheet.EpisodeListSorter;
import com.the_canuck.openpodcast.search.RssReaderApi;

import java.util.List;

public class EpisodeRepositoryImpl implements EpisodeRepository {

    public final EpisodesServiceApi episodesServiceApi;
    public final RssReaderApi readerApi;

    private int collectionId;

    Context context;

    private List<Episode> cachedEpisodeList;
    private List<Episode> cachedDownloadList;
    private List<Episode> cachedNonDownloadedList;

    public EpisodeRepositoryImpl(EpisodesServiceApi episodesServiceApi, RssReaderApi readerApi,
                                 int collectionId, Context context) {
        this.episodesServiceApi = episodesServiceApi;
        this.readerApi = readerApi;
        this.collectionId = collectionId;
        this.context = context;
    }

    @Override
    public void getAllEpisodesSorted(final LoadEpisodesCallback callback) {
        // Check if cachedDownloadList exists, if not get it
        if (cachedDownloadList == null) {
            getDownloadedEpisodes(new LoadEpisodesCallback() {
                @Override
                public void onEpisodesLoaded(List<Episode> episodes) {

                    // after above check if nonDownloadedList exists, if not get it
                    if (cachedNonDownloadedList == null) {
                        getNonDownloadedEpisodes(new LoadEpisodesCallback() {
                            @Override
                            public void onEpisodesLoaded(List<Episode> episodes) {

                                // finally create the final sorted episode list
                                if (cachedDownloadList == null || cachedDownloadList.isEmpty()) {
                                    cachedEpisodeList = cachedNonDownloadedList;
                                } else {
                                    cachedEpisodeList = EpisodeListSorter.sortTwoEpisodeLists(cachedNonDownloadedList,
                                            cachedDownloadList);
                                }

                                callback.onEpisodesLoaded(cachedEpisodeList);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void getDownloadedEpisodes(final LoadEpisodesCallback callback) {

        if (cachedDownloadList == null) {
            episodesServiceApi.getAllEpisodesForCollection(context, collectionId,
                    new EpisodesServiceApi.EpisodesServiceCallback<List<Episode>>() {
                        @Override
                        public void onLoaded(List<Episode> episodes) {
                            cachedDownloadList = episodes;
                            callback.onEpisodesLoaded(cachedDownloadList);
                        }
                    });
        } else {
            callback.onEpisodesLoaded(cachedDownloadList);
        }
    }

    @Override
    public void getNonDownloadedEpisodes(final LoadEpisodesCallback callback) {
        if (cachedNonDownloadedList == null) {

            readerApi.getEpisodes(new RssReaderApi.RssServiceCallback<List<Episode>>() {
                @Override
                public void onLoaded(List<Episode> episodes) {
                    cachedNonDownloadedList = episodes;
                    callback.onEpisodesLoaded(episodes);
                }
            });
        }
    }

    @Override
    public void addEpisode(Episode episode) {
        episodesServiceApi.addEpisode(episode);
    }

    @Override
    public void updateEpisode(Episode episode) {
        episodesServiceApi.updateEpisode(episode);
    }

    @Override
    public void deleteEpisode(Episode episode) {
        episodesServiceApi.deleteEpisode(episode);
    }

    @Override
    public void getLastPlayed(final GetEpisodeCallback callback) {
        episodesServiceApi.getLastPlayed(new EpisodesServiceApi.EpisodesServiceCallback<Episode>() {
            @Override
            public void onLoaded(Episode episodes) {
                callback.onEpisodeLoaded(episodes);
            }
        });
    }

    @Override
    public void getNewDownloads(final LoadEpisodesCallback callback) {
        episodesServiceApi.getNewDownloads(new EpisodesServiceApi.EpisodesServiceCallback<List<Episode>>() {
            @Override
            public void onLoaded(List<Episode> episodes) {
                callback.onEpisodesLoaded(episodes);
            }
        });
    }

    @Override
    public void refreshData() {
        cachedEpisodeList = null;
        cachedDownloadList = null;
        cachedNonDownloadedList = null;
    }

    @Override
    public void getDescription(final GetStringCallback callback) {
        readerApi.getDescription(new RssReaderApi.RssServiceCallback<String>() {
            @Override
            public void onLoaded(String episodes) {
                callback.onStringReturned(episodes);
            }
        });
    }
}
