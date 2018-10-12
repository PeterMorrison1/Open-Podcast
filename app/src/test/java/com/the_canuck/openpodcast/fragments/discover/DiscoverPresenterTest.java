package com.the_canuck.openpodcast.fragments.discover;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.data.discover_list.DiscoverRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

public class DiscoverPresenterTest {
    private DiscoverPresenter presenter;

    @Mock
    private DiscoverContract.DiscoverView mDiscoverView;

    @Mock
    private DiscoverRepository discoverRepository;

    @Captor
    private ArgumentCaptor<DiscoverRepository.EveryListLoadedCallback> callbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        presenter = Mockito.spy(new DiscoverPresenter(discoverRepository, mDiscoverView));
    }

    @Test
    public void populatePodcastList_Should_ReturnList_When_NormalState() {
        List<List<Podcast>> lists = new ArrayList<>();
        List<Podcast> podcasts1 = new ArrayList<>();
        podcasts1.add(new Podcast().setCollectionName("Test1"));
        podcasts1.add(new Podcast().setCollectionName("Test2"));
        List<Podcast> podcasts2 = new ArrayList<>();
        podcasts2.add(new Podcast().setCollectionName("Test1"));
        podcasts2.add(new Podcast().setCollectionName("Test2"));

        lists.add(podcasts1);
        lists.add(podcasts2);

        presenter.populatePodcastList();

        verify(discoverRepository).getAllPodcastLists(callbackCaptor.capture());
        callbackCaptor.getValue().onListsLoaded(lists);

        verify(mDiscoverView).setPodcastList(lists);
    }
}
