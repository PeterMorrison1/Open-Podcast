package com.the_canuck.openpodcast.fragments.bottom_sheet;

import android.content.Context;
import android.net.Uri;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.data.episode.EpisodeRepository;
import com.the_canuck.openpodcast.data.episode.EpisodesServiceApi;
import com.the_canuck.openpodcast.download.DownloadHelperApi;
import com.the_canuck.openpodcast.search.RssReader;
import com.the_canuck.openpodcast.search.RssReaderApi;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.List;

public class BottomSheetPresenter implements BottomSheetContract.BottomSheetPresenter {

    private EpisodeRepository episodeRepository;
    private EpisodesServiceApi episodesServiceApi;
    private RssReaderApi rssReaderApi;
    private DownloadHelperApi downloadHelperApi;

    private BottomSheetContract.BottomSheetView bottomSheetView;

    private EpisodeListTask episodeListTask;

    MySQLiteHelper sqLiteHelper;
    Context context; // TODO: Will be removed and injected w/ dagger where sql is needed
    RssReader reader;

    public BottomSheetPresenter(BottomSheetContract.BottomSheetView bottomSheetView,
                                EpisodeRepository episodeRepository,
                                EpisodesServiceApi episodesServiceApi, RssReaderApi rssReaderApi,
                                Context context) {
        this.bottomSheetView = bottomSheetView;
        this.episodeRepository = episodeRepository;
        this.episodesServiceApi = episodesServiceApi;
        this.rssReaderApi = rssReaderApi;
        this.context = context;
    }

    /**
     * Creates an arraylist with downloaded episodes from database at the lowest # index,
     * and sets the non-downloaded episodes from rssReader at higher # index than downloaded,
     * sets the {@link PodcastListDialogFragment#episodes} to the combined list of downloaded and
     * non-downloaded episodes.
     */
    @Override
    public void episodeListInstantiator(String feed, int collectionId, String artist) {
        bottomSheetView.showLoadingIndicator(true);

        episodeRepository.getAllEpisodesSorted(new EpisodeRepository.LoadEpisodesCallback() {
            @Override
            public void onEpisodesLoaded(List<Episode> episodes) {
                bottomSheetView.setEpisodeList(episodes);
                bottomSheetView.showLoadingIndicator(false);
                bottomSheetView.populateBottomSheetViews();
                getDescription();
            }
        });
//        sqLiteHelper = new MySQLiteHelper(context);
//        reader = new RssReader(feed);
//
//        bottomSheetView.showLoadingIndicator(true);
//
//        if (episodeListTask == null || !episodeListTask.isRunning()) {
//            episodeListTask = new EpisodeListTask(bottomSheetView, collectionId, artist,
//                    sqLiteHelper, reader);
//
//            episodeListTask.execute();
//        }
    }

    @Override
    public void stop() {
        if (episodeListTask != null && episodeListTask.isRunning()) {
            episodeListTask.cancel(true);
        }
    }

    @Override
    public void getDescription() {
        episodeRepository.getDescription(new EpisodeRepository.GetStringCallback() {
            @Override
            public void onStringReturned(String string) {
                if (string != null) {
                    bottomSheetView.setPodcastDescription(string);
                } else {
                    // TODO: Change to a string in Strings.xml later
                    bottomSheetView.setPodcastDescription("Podcast Description Failed to load");
                }
            }
        });
    }

    @Override
    public void setDownloadHelperApi(DownloadHelperApi downloadHelperApi) {
        this.downloadHelperApi = downloadHelperApi;
    }

    @Override
    public void startDownload(final Episode episode) {
        downloadHelperApi.startDownload(new DownloadHelperApi.GetEnqueueCallback() {
            @Override
            public void onEnqueueLoaded(long enqueue) {
                bottomSheetView.setDownloadEnqueue(enqueue);

                episode.setDownloadId(enqueue);
                episode.setDownloadStatus(Episode.CURRENTLY_DOWNLOADING);
                episodeRepository.addEpisode(episode);

            }
        });
    }

    @Override
    public void getDownloadStatus(long enqueue) {
        downloadHelperApi.getStatus(enqueue, new DownloadHelperApi.GetStatusCallback() {
            @Override
            public void onStatusReturned(String status) {
                bottomSheetView.setDownloadStatus(status);
            }
        });
    }

    @Override
    public void getDownloadUri(long enqueue) {
        downloadHelperApi.getURI(enqueue, new DownloadHelperApi.GetURICallback() {
            @Override
            public void onURIReturned(Uri uri) {
                bottomSheetView.setDownloadUri(uri);
            }
        });
    }
}
