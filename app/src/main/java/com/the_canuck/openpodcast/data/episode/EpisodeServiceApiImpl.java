package com.the_canuck.openpodcast.data.episode;

import android.content.Context;
import android.os.Handler;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class EpisodeServiceApiImpl implements EpisodesServiceApi {

    private List<Episode> episodeList;
    MySQLiteHelper sqLiteHelper; // TODO: Must inject with Dagger!!!!!
    Context context;

    @Override
    public void getAllEpisodesForCollection(Context context, final int collectionId,
                                            final EpisodesServiceCallback<List<Episode>> callback) {
        final Handler handler = new Handler();
        sqLiteHelper = new MySQLiteHelper(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                episodeList = sqLiteHelper.getEpisodes(collectionId);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLoaded(episodeList);

                    }
                });
            }
        }).start();
    }

    @Override
    public void getEpisode(Episode episode, EpisodesServiceCallback<Episode> callback) {

    }

    @Override
    public void addEpisode(final Episode episode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sqLiteHelper.addEpisode(episode);
            }
        }).start();
    }

    @Override
    public void updateEpisode(final Episode episode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sqLiteHelper.updateEpisode(episode);
            }
        }).start();
    }

    @Override
    public void deleteEpisode(final Episode episode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sqLiteHelper.deleteEpisode(episode);
            }
        }).start();
    }

    @Override
    public void getLastPlayed(final EpisodesServiceCallback<Episode> callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (episodeList != null) {
                    for (final Episode e : episodeList) {
                        if (e.getIsLastPlayed() == Episode.IS_LAST_PLAYED) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onLoaded(e);

                                }
                            });
                            break;
                        }
                    }
                } else {
                    sqLiteHelper = new MySQLiteHelper(context);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoaded(sqLiteHelper.getLastPlayedEpisode());

                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void getNewDownloads(final EpisodesServiceCallback<List<Episode>> callback) {

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (episodeList != null) {
                    List<Episode> newDownloads = new ArrayList<>();

                    for (Episode e : episodeList) {
                        if (e.getDownloadId() != Episode.NO_DOWNLOAD_ID) {
                            newDownloads.add(e);
                        }
                    }

                    final List<Episode> finalNewDownloads = newDownloads;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoaded(finalNewDownloads);
                        }
                    });

                } else {
                    sqLiteHelper = new MySQLiteHelper(context);
                    final List<Episode> newDownloads = sqLiteHelper.getNewDownloadEpisodes();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoaded(newDownloads);
                        }
                    });
                }
            }
        }).start();

    }
}
