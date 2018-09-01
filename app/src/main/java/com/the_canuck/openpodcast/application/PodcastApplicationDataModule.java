package com.the_canuck.openpodcast.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.the_canuck.openpodcast.data.episode.EpisodeRepository;
import com.the_canuck.openpodcast.data.episode.EpisodeRepositoryImpl;
import com.the_canuck.openpodcast.data.episode.EpisodeServiceApiImpl;
import com.the_canuck.openpodcast.data.episode.EpisodesServiceApi;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;
import com.the_canuck.openpodcast.data.podcast.PodcastRepositoryImpl;
import com.the_canuck.openpodcast.data.podcast.PodcastServiceApi;
import com.the_canuck.openpodcast.data.podcast.PodcastServiceApiImpl;
import com.the_canuck.openpodcast.search.RssReader;
import com.the_canuck.openpodcast.search.RssReaderApi;
import com.the_canuck.openpodcast.search.RssReaderApiImpl;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import dagger.Module;
import dagger.Provides;

@Module (includes = ContextModule.class)
public class PodcastApplicationDataModule {

    @Provides
    @PodcastApplicationScope
    public MySQLiteHelper mySQLiteHelper(Context context) {
        return new MySQLiteHelper(context);
    }

    @Provides
    @PodcastApplicationScope
    public SharedPreferences sharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

//    @Provides
//    @PodcastApplicationScope
//    public EpisodeRepository episodeRepositoryNotImpl(EpisodeRepositoryImpl episodeRepository) {
//        return episodeRepository;
//    }

    @Provides
    @PodcastApplicationScope
    public EpisodeRepository episodeRepository(EpisodesServiceApi episodesServiceApi, RssReaderApi readerApi) {
        return new EpisodeRepositoryImpl(episodesServiceApi, readerApi);
    }

    @Provides
    @PodcastApplicationScope
    public EpisodesServiceApi episodesServiceApi(MySQLiteHelper sqLiteHelper) {
        return new EpisodeServiceApiImpl(sqLiteHelper);
    }

    @Provides
    @PodcastApplicationScope
    public RssReaderApi rssReaderApi(RssReader rssReader) {
        return new RssReaderApiImpl(rssReader);
    }

    @Provides
    @PodcastApplicationScope
    public RssReader rssReader() {
        return new RssReader();
    }

    @Provides
    @PodcastApplicationScope
    public PodcastRepository podcastRepository(PodcastServiceApi podcastServiceApi) {
        return new PodcastRepositoryImpl(podcastServiceApi);
    }

    @Provides
    @PodcastApplicationScope
    public PodcastServiceApi podcastServiceApi(MySQLiteHelper sqLiteHelper) {
        return new PodcastServiceApiImpl(sqLiteHelper);
    }

}
