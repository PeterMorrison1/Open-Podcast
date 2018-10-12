package com.the_canuck.openpodcast.fragments.library;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LibraryPresenterTest {
    private LibraryPresenter presenter;

    @Mock
    private PodcastRepository podcastRepository;

    @Mock
    private LibraryContract.LibraryView mLibraryView;

    @Captor
    private ArgumentCaptor<PodcastRepository.LoadPodcastsCallback> callbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        presenter = Mockito.spy(new LibraryPresenter(mLibraryView, podcastRepository));
    }

    @Test
    public void updateSubscribedPodcasts_Should_CallbackEpList_When_NormalState() {
        List<Podcast> podcasts = new ArrayList<>();

        podcasts.add(new Podcast().setCollectionName("Test1"));
        podcasts.add(new Podcast().setCollectionName("Test2"));

        presenter.updateSubscribedPodcasts();

        verify(podcastRepository, times(1))
                .getSubscribedPodcasts(callbackCaptor.capture());
        callbackCaptor.getValue().onPodcastsLoaded(podcasts);

        verify(mLibraryView).showSubscribedPodcasts(podcasts);

        InOrder inOrder = Mockito.inOrder(mLibraryView);
        inOrder.verify(mLibraryView).showLoadingIndicator(true);
        inOrder.verify(mLibraryView).showLoadingIndicator(false);

        verify(mLibraryView).populatePodcastViews();
    }
}
