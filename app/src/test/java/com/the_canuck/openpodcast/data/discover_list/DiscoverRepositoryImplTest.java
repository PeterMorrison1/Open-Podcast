package com.the_canuck.openpodcast.data.discover_list;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.search.enums.GenreIds;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;


public class DiscoverRepositoryImplTest {
    private DiscoverRepositoryImpl repository;

    @Mock
    private DiscoverLocalApi discoverLocalApi;

    @Mock
    private DiscoverRepository.EveryListLoadedCallback everyListLoadedCallback;

    @Mock
    private DiscoverLocalApi.PodcastListLoadedCallback podcastListLoadedCallback;

    @Captor
    private ArgumentCaptor<DiscoverRepository.ListLoadedCallback> listLoadedCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<DiscoverRepository.EveryListLoadedCallback> everyListLoadedCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<DiscoverLocalApi.PodcastListLoadedCallback> podcastListLoadedCallbackArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        repository = spy(new DiscoverRepositoryImpl(discoverLocalApi));
    }

    @Test
    public void getAllPodcastLists_Should_ReturnPodcastList_When_CachedListsNull() {
        List<List<Podcast>> listOfLists = new ArrayList<>();
        List<Podcast> podcastList = new ArrayList<>();

        podcastList.add(new Podcast());
        listOfLists.add(podcastList);

        repository.getAllPodcastLists(everyListLoadedCallback);

        verify(repository, times(GenreIds.getSize())).getPodcastList(anyInt(), listLoadedCallbackArgumentCaptor.capture());
        listLoadedCallbackArgumentCaptor.getValue().onListLoaded(podcastList);

        verify(everyListLoadedCallback).onListsLoaded(listOfLists);

    }

    @Test
    public void getAllPodcastLists_Should_ReturnCachedList_When_CachedListNotNulL() {
        List<List<Podcast>> listOfLists = new ArrayList<>();
        List<Podcast> podcastList = new ArrayList<>();

        podcastList.add(new Podcast());
        listOfLists.add(podcastList);

        repository.setCachedLists(listOfLists);

        repository.getAllPodcastLists(everyListLoadedCallback);
        verify(repository).getAllPodcastLists(everyListLoadedCallbackArgumentCaptor.capture());

        verify(everyListLoadedCallback).onListsLoaded(listOfLists);
    }

    @Test
    public void getPodcastList_Should_ReturnPodcastList_When_GenreIdPassed() {
        List<Podcast> podcastList = new ArrayList<>();
        podcastList.add(new Podcast());
        int genre = 1;

        discoverLocalApi.parsePodcastList(genre, podcastListLoadedCallback);

        verify(discoverLocalApi).parsePodcastList(anyInt(), podcastListLoadedCallbackArgumentCaptor.capture());
        podcastListLoadedCallbackArgumentCaptor.getValue().onPodcastsLoaded(podcastList);

        podcastListLoadedCallback.onPodcastsLoaded(podcastList);
    }
}
