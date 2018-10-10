package com.the_canuck.openpodcast.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.data.episode.EpisodeRepository;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;
import com.the_canuck.openpodcast.media_player.MediaControlApi;

public class MainActivityPresenter implements MainActivityContract.MainActivityPresenter {

    private MainActivityContract.MainActivityView mainActivityView;

    private PodcastRepository podcastRepository;

    private EpisodeRepository episodeRepository;

    private MediaControlApi mediaControlApi;

    public MainActivityPresenter(MainActivityContract.MainActivityView mainActivityView,
                                 PodcastRepository podcastRepository,
                                 EpisodeRepository episodeRepository) {
        this.mainActivityView = mainActivityView;
        this.podcastRepository = podcastRepository;
        this.episodeRepository = episodeRepository;
    }

    // -------------------------------- SQL --------------------------------


    @Override
    public void getArtwork600(int collectionId) {
        podcastRepository.getPodcastArtwork600(collectionId, new PodcastRepository.GetArtworkCallback() {
            @Override
            public void onArtworkLoaded(String artworkUrl) {
                mainActivityView.setArtwork600(artworkUrl);
            }
        });
    }

    @Override
    public void getLastPlayedEp() {
        episodeRepository.getLastPlayed(new EpisodeRepository.GetEpisodeCallback() {
            @Override
            public void onEpisodeLoaded(Episode episode) {
                mainActivityView.setCurrentEpisode(episode);
            }
        });
    }

    // -------------------------------- Media --------------------------------


    @Override
    public void setMediaController(MediaControlApi mediaController) {
        this.mediaControlApi = mediaController;
    }

    @Override
    public void play() {
        mediaControlApi.play();
    }

    @Override
    public void playFromUri(Uri uri, Bundle bundle) {
        mediaControlApi.playFromUri(uri, bundle);
    }

    @Override
    public void pause() {
        mediaControlApi.pause();
    }

    @Override
    public void stop() {
        mediaControlApi.stop();
    }

    @Override
    public void seekTo(int position) {
        mediaControlApi.seekTo(position);
    }

    @Override
    public void registerCallback(MediaControllerCompat.Callback callback) {
        mediaControlApi.registerCallback(callback);
    }

    @Override
    public void getState() {
        mediaControlApi.getState(new MediaControlApi.StateCallback() {
            @Override
            public void onLoaded(int state) {
                mainActivityView.setMediaState(state);
            }
        });
    }

    @Override
    public void getPosition() {
        mediaControlApi.getPosition(new MediaControlApi.PositionCallback() {
            @Override
            public void onLoaded(long position) {
                mainActivityView.setPosition(position);
            }
        });
    }
}
