package com.the_canuck.openpodcast.fragments.bottom_sheet;

import android.net.Uri;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.data.episode.EpisodeRepository;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;
import com.the_canuck.openpodcast.download.DownloadHelperApi;

import java.util.List;

public class BottomSheetPresenter implements BottomSheetContract.BottomSheetPresenter {

    public EpisodeRepository episodeRepository;

    public PodcastRepository podcastRepository;

    private DownloadHelperApi downloadHelperApi;

    private BottomSheetContract.BottomSheetView bottomSheetView;

    public BottomSheetPresenter(BottomSheetContract.BottomSheetView bottomSheetView,
                                EpisodeRepository episodeRepository, PodcastRepository podcastRepository) {
        this.bottomSheetView = bottomSheetView;
        this.episodeRepository = episodeRepository;
        this.podcastRepository = podcastRepository;
    }

    // -------------------------------- Download Related --------------------------------

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

    // -------------------------------- Episode Related --------------------------------

    /**
     * Creates an arraylist with downloaded episodes from database at the lowest # index,
     * and sets the non-downloaded episodes from rssReader at higher # index than downloaded,
     * sets the {@link PodcastListDialogFragment#episodes} to the combined list of downloaded and
     * non-downloaded episodes.
     */
    @Override
    public void episodeListInstantiator(String feed, int collectionId, String artist) {
        bottomSheetView.showLoadingIndicator(true);

        episodeRepository.getAllEpisodesSorted(feed, collectionId, artist, new EpisodeRepository.LoadEpisodesCallback() {
            @Override
            public void onEpisodesLoaded(List<Episode> episodes) {
                bottomSheetView.setEpisodeList(episodes);
                bottomSheetView.showLoadingIndicator(false);
                bottomSheetView.populateBottomSheetViews();
                getDescription();
            }
        });
    }
//    @Override
//    public void stop() {
//        if (episodeListTask != null && episodeListTask.isRunning()) {
//            episodeListTask.cancel(true);
//        }
//    }

    @Override
    public void getDescription() {
        episodeRepository.getDescription(new EpisodeRepository.GetStringCallback() {
            @Override
            public void onStringReturned(String string) {
                if (string != null) {
                    bottomSheetView.setPodcastDescription(string);
                } else {
                    bottomSheetView.setPodcastDescription("Podcast Description Failed to load");
                }
            }
        });
    }

    @Override
    public void deleteEpisode(Episode episode) {
        episodeRepository.deleteEpisode(episode);
    }

    // -------------------------------- Podcast related --------------------------------

    @Override
    public void subscribe(Podcast podcast, int autoUpdate) {
        podcastRepository.subscribe(podcast, autoUpdate);
        bottomSheetView.hideSubscribeButton();
    }

    @Override
    public void unsubscribe(Podcast podcast) {
        podcastRepository.unsubscribe(podcast);
        bottomSheetView.showSubscribeButton();
    }

    @Override
    public void updatePodcast(Podcast podcast, int autoUpdate) {
        podcastRepository.updatePodcast(podcast, autoUpdate);
    }

    @Override
    public void doesPodcastExist(Podcast podcast) {
        podcastRepository.doesPodcastExist(podcast, new PodcastRepository.GetPodcastExistCallback() {
            @Override
            public void onExistLoaded(boolean exists) {
                if (exists) {
                    bottomSheetView.hideSubscribeButton();
                } else {
                    bottomSheetView.showSubscribeButton();
                }
            }
        });
    }
}
