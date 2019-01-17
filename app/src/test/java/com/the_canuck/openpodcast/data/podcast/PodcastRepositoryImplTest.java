package com.the_canuck.openpodcast.data.podcast;

import com.the_canuck.openpodcast.Podcast;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class PodcastRepositoryImplTest {
    private PodcastRepositoryImpl repository;

    @Mock
    private PodcastServiceApi serviceApi;

    @Captor
    private ArgumentCaptor<PodcastServiceApi.PodcastServiceCallback> podcastServiceCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<PodcastServiceApi.GetPodcastExistCallback> existCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<PodcastServiceApi.GetArtworkCallback> artworkCallbackArgumentCaptor;

    @Mock
    private PodcastRepository.LoadPodcastsCallback loadPodcastsCallback;

    @Mock
    private PodcastRepository.GetArtworkCallback artworkCallback;

    @Mock
    private PodcastRepository.GetPodcastExistCallback existCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        repository = spy(new PodcastRepositoryImpl(serviceApi));
    }

    @Test
    public void subscribe_Should_AddPodcastToSql_When_PodcastValid() {
        Podcast podcast = new Podcast();
        int autoUpdate = 1;

        repository.subscribe(podcast, autoUpdate);

        verify(serviceApi).subscribe(podcast, autoUpdate);
    }

    @Test
    public void unsubscribe_Should_RemovePodcastFromSql_When_PodcastValid() {
        Podcast podcast = new Podcast();

        repository.unsubscribe(podcast);

        verify(serviceApi).unsubscribe(podcast);
    }

    @Test
    public void updatePodcast_Should_UpdatePodcastSqlEntry_When_PodcastValid() {
        Podcast podcast = new Podcast();
        int autoUpdate = 1;

        repository.updatePodcast(podcast, autoUpdate);

        verify(serviceApi).updatePodcast(podcast, autoUpdate);
    }

    @Test
    public void doesPodcastExist_Should_ReturnTrue_When_PodcastInSql() {
        boolean podExist = true;
        Podcast podcast = new Podcast();

        repository.doesPodcastExist(podcast, existCallback);

        verify(serviceApi).doesPodcastExist(any(Podcast.class), existCallbackArgumentCaptor.capture());
        existCallbackArgumentCaptor.getValue().onExistLoaded(true);

        existCallback.onExistLoaded(podExist);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getSubscribedPodcasts_Should_ReturnSqlPodcastList_When_CachedListNull() {
        List<Podcast> podcastList = new ArrayList<>();
        podcastList.add(new Podcast());

        repository.getSubscribedPodcasts(loadPodcastsCallback);

        verify(serviceApi).getSubscribedPodcasts(podcastServiceCallbackArgumentCaptor.capture());
        podcastServiceCallbackArgumentCaptor.getValue().onLoaded(podcastList);

        loadPodcastsCallback.onPodcastsLoaded(podcastList);
    }

    @Test
    public void getPodcastArtwork_Should_ReturnArtworkUrl_When_CollectionIdCorrect() {
        String artwork = "";
        int collectionId = 1;

        repository.getPodcastArtwork600(collectionId, artworkCallback);

        verify(serviceApi).getPodcastArtwork600(anyInt(), artworkCallbackArgumentCaptor.capture());
        artworkCallbackArgumentCaptor.getValue().onArtworkLoaded(artwork);

        artworkCallback.onArtworkLoaded(artwork);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getAutoUpdatePodcasts_Should_ReturnPodcastList_When_PodcastsExist() {
        List<Podcast> podcastList = new ArrayList<>();
        podcastList.add(new Podcast());

        repository.getAutoUpdatePodcasts(loadPodcastsCallback);

        verify(serviceApi).getAutoUpdatePodcasts(podcastServiceCallbackArgumentCaptor.capture());
        podcastServiceCallbackArgumentCaptor.getValue().onLoaded(podcastList);

        loadPodcastsCallback.onPodcastsLoaded(podcastList);
    }

}
