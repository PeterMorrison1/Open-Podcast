package com.the_canuck.openpodcast.data.episode;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.search.RssReaderApi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class EpisodeRepositoryImplTest {
    private EpisodeRepositoryImpl repository;

    @Mock
    private EpisodesServiceApi serviceApi;

    @Mock
    private RssReaderApi readerApi;

    @Mock
    private EpisodeRepository.LoadEpisodesCallback loadEpisodesCallback;

    @Mock
    private EpisodeRepository.GetEpisodeCallback getEpisodeCallback;

    @Mock
    private EpisodeRepository.GetStringCallback stringCallback;

    @Captor
    private ArgumentCaptor<EpisodesServiceApi.EpisodesServiceCallback> episodesServiceCallbackCaptor;

    @Captor
    private ArgumentCaptor<RssReaderApi.RssServiceCallback> rssServiceCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<EpisodesServiceApi.EpisodesServiceCallback>
            episodesServiceCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<EpisodeRepository.LoadEpisodesCallback> episodesCallbackArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        repository = spy(new EpisodeRepositoryImpl(serviceApi, readerApi));
    }

    @Test
    public void getAllEpisodesSorted_Should_ReturnSortedList_When_CacheNull() {
        List<Episode> episodeList = new ArrayList<>();
        episodeList.add(new Episode());
        int collectionId = 0;
        String feed = "test";
        String artist = "test";

        repository.getAllEpisodesSorted(feed, collectionId, artist, loadEpisodesCallback);

        // downloaded episode method call
        verify(repository).getDownloadedEpisodes(anyInt(),
                episodesCallbackArgumentCaptor.capture());
        episodesCallbackArgumentCaptor.getValue().onEpisodesLoaded(episodeList);

        // non downloaded episode method call
        verify(repository).getNonDownloadedEpisodes(anyString(), anyInt(), anyString(),
                episodesCallbackArgumentCaptor.capture());
        episodesCallbackArgumentCaptor.getValue().onEpisodesLoaded(episodeList);

        repository.setCachedDownloadList(episodeList);
        repository.setCachedNonDownloadedList(episodeList);

        // creating the mixed list
        verify(repository).combineCachedLists();

        verify(loadEpisodesCallback).onEpisodesLoaded(repository.getCachedEpisodeList());

    }

    @Test
    public void getAllEpisodesSorted_Should_ReturnSortedList_When_CacheNotNull() {
        String feed = "test";
        String artist = "test";
        int collectionId = 0;

        List<Episode> episodeList = new ArrayList<>();
        Episode episode = new Episode();
        episode.setCollectionId(collectionId);
        episodeList.add(episode);

        repository.setCachedEpisodeList(episodeList);
        repository.setCachedDownloadList(episodeList);

        repository.getAllEpisodesSorted(feed, collectionId, artist, loadEpisodesCallback);

        repository.setCachedEpisodeList(episodeList);
        verify(loadEpisodesCallback).onEpisodesLoaded(repository.getCachedEpisodeList());
    }

    @Test
    public void getAllEpisodesSorted_Should_RefreshData_When_CachedNotNulLWrongColId() {
        String feed = "test";
        String artist = "test";
        int collectionId = 0;
        int wrongColId = 1;

        List<Episode> episodeList = new ArrayList<>();
        Episode episode = new Episode();
        episode.setCollectionId(wrongColId);
        episodeList.add(episode);

        repository.setCachedEpisodeList(episodeList);
        repository.getAllEpisodesSorted(feed, collectionId, artist, loadEpisodesCallback);

        verify(repository).refreshData();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getDownloadedEpisodes_Should_ReturnList_When_CacheNull() {
        List<Episode> episodeList = new ArrayList<>();
        episodeList.add(new Episode());
        int collectionId = 0;

        repository.getDownloadedEpisodes(collectionId, loadEpisodesCallback);

        verify(serviceApi).getAllEpisodesForCollection(anyInt(),
                episodesServiceCallbackArgumentCaptor.capture());

        episodesServiceCallbackArgumentCaptor.getValue().onLoaded(episodeList);

        verify(loadEpisodesCallback).onEpisodesLoaded(episodeList);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getDownloadedEpisodes_Should_ReturnCachedList_When_CacheNotNull() {
        int collectionId = 0;
        List<Episode> episodeList = new ArrayList<>();
        episodeList.add(new Episode());

        repository.setCachedDownloadList(episodeList);

        repository.getDownloadedEpisodes(collectionId, loadEpisodesCallback);

        verify(serviceApi, times(0)).getAllEpisodesForCollection(anyInt(),
                episodesServiceCallbackArgumentCaptor.capture());

        verify(loadEpisodesCallback).onEpisodesLoaded(episodeList);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getNonDownloadedEpisodes_Should_ReturnList_When_CacheNull() {
        List<Episode> episodeList = new ArrayList<>();
        episodeList.add(new Episode());
        int collectionId = 0;
        String feed = "test";
        String artist = "test";

        repository.getNonDownloadedEpisodes(feed, collectionId, artist, loadEpisodesCallback);

        verify(readerApi).getEpisodes(anyString(), anyInt(), anyString(),
                rssServiceCallbackArgumentCaptor.capture());

        rssServiceCallbackArgumentCaptor.getValue().onLoaded(episodeList);
        verify(loadEpisodesCallback).onEpisodesLoaded(episodeList);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getNonDownloadedEpisdoes_Should_ReturnList_When_CacheNotNull() {
        List<Episode> episodeList = new ArrayList<>();
        episodeList.add(new Episode());
        int collectionId = 0;
        String feed = "test";
        String artist = "test";

        repository.setCachedNonDownloadedList(episodeList);

        repository.getNonDownloadedEpisodes(feed, collectionId, artist, loadEpisodesCallback);

        verify(readerApi, times(0)).getEpisodes(anyString(), anyInt(),
                anyString(), rssServiceCallbackArgumentCaptor.capture());

        verify(loadEpisodesCallback).onEpisodesLoaded(episodeList);
    }

    @Test
    public void addEpsiode_Should_AddEpToSql_When_EpisodePassed() {
        Episode episode = new Episode();

        repository.addEpisode(episode);

        verify(serviceApi).addEpisode(episode);
    }

    @Test
    public void updateEpisode_Should_UpdateEpInSql_When_EpisodePassed() {
        Episode episode = new Episode();

        repository.updateEpisode(episode);

        verify(serviceApi).updateEpisode(episode);
    }

    @Test
    public void deleteEpisode_Should_RemoveEpFromSql_When_EpisodePassed() {
        Episode episode = new Episode();

        repository.deleteEpisode(episode);

        verify(serviceApi).deleteEpisode(episode);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getLastPlayed_Should_ReturnEpisode_When_EpisodeIsInSql() {
        Episode episode = new Episode();

        repository.getLastPlayed(getEpisodeCallback);

        verify(serviceApi).getLastPlayed(episodesServiceCallbackCaptor.capture());

        episodesServiceCallbackCaptor.getValue().onLoaded(episode);

        verify(getEpisodeCallback).onEpisodeLoaded(episode);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getNewDownloads_Should_ReturnList_When_EpisodesAreInSql() {
        List<Episode> episodeList = new ArrayList<>();
        episodeList.add(new Episode());

        repository.getNewDownloads(loadEpisodesCallback);

        verify(serviceApi).getNewDownloads(episodesServiceCallbackArgumentCaptor.capture());
        episodesServiceCallbackArgumentCaptor.getValue().onLoaded(episodeList);

        verify(loadEpisodesCallback).onEpisodesLoaded(episodeList);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getDescription_Should_ReturnString_When_CacheNull() {
        String description = "test";

        repository.getDescription(stringCallback);

        verify(readerApi).getDescription(rssServiceCallbackArgumentCaptor.capture());
        rssServiceCallbackArgumentCaptor.getValue().onLoaded(description);

        verify(stringCallback).onStringReturned(description);
    }
}
