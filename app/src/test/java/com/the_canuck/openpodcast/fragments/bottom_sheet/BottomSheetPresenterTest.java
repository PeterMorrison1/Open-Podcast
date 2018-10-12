package com.the_canuck.openpodcast.fragments.bottom_sheet;

import android.net.Uri;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.data.episode.EpisodeRepository;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;
import com.the_canuck.openpodcast.download.DownloadHelper;
import com.the_canuck.openpodcast.download.DownloadHelperApi;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BottomSheetPresenterTest {
    private BottomSheetPresenter presenter;

    @Mock
    private PodcastRepository podcastRepository;

    @Mock
    private EpisodeRepository episodeRepository;

    @Mock
    private BottomSheetContract.BottomSheetView bottomSheetView;

    @Mock
    private DownloadHelperApi downloadHelperApi;

    @Mock
    private Uri uri;

    @Captor
    private ArgumentCaptor<DownloadHelperApi.GetEnqueueCallback> enqueueCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<DownloadHelperApi.GetStatusCallback> statusCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<DownloadHelperApi.GetURICallback> uriCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<EpisodeRepository.LoadEpisodesCallback> loadEpisodesCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<EpisodeRepository.GetStringCallback> stringCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<PodcastRepository.GetPodcastExistCallback> podcastExistCallbackArgumentCaptor;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        presenter = Mockito.spy(new BottomSheetPresenter(bottomSheetView, episodeRepository,
                podcastRepository));
        presenter.setDownloadHelperApi(downloadHelperApi);
    }

    @Test
    public void startDownload_Should_BeginDownload_When_EpisodeUrlValid() {
        long enqueue = 20; // doesnt matter what the enqueue/download id is
        Episode episode = new Episode();
        episode.setTitle("Test1");
        episode.setMediaUrl("http://dts.podtrac.com/redirect.mp3/traffic.libsyn.com/potterless/ep41.m4a?dest-id=614116");

        presenter.startDownload(episode);

        verify(downloadHelperApi).startDownload(enqueueCallbackArgumentCaptor.capture());
        enqueueCallbackArgumentCaptor.getValue().onEnqueueLoaded(enqueue);

        verify(episodeRepository).addEpisode(episode);
    }

    @Test
    public void getDownloadStatus_Should_SetStatus_When_DownloadIdIsValid() {
        presenter.getDownloadStatus(anyLong());

        verify(downloadHelperApi).getStatus(anyLong(), statusCallbackArgumentCaptor.capture());
        statusCallbackArgumentCaptor.getValue().onStatusReturned(DownloadHelper.STATUS_SUCCESSFUL);

        verify(bottomSheetView).setDownloadStatus(DownloadHelper.STATUS_SUCCESSFUL);
    }

    @Test
    public void getDownloadUri_Should_SetUri_When_DownloadIdIsValid() {
        presenter.getDownloadUri(anyLong());

        verify(downloadHelperApi).getURI(anyLong(), uriCallbackArgumentCaptor.capture());
        uriCallbackArgumentCaptor.getValue().onURIReturned(uri);

        verify(bottomSheetView).setDownloadUri(uri);
    }

    @Test
    public void episodeListInstantiator_Should_CreateViews_When_ValidEpInfo() {
        String feed = "http://adventurezone.libsyn.com/rss";
        int collectionId = 947899573;
        String artist = "The McElroys";

        List<Episode> testList = new ArrayList<>();
        testList.add(new Episode().setTitle("Test1").setCollectionId(collectionId).setArtist(artist));
        testList.add(new Episode().setTitle("Test2").setCollectionId(collectionId).setArtist(artist));

        presenter.episodeListInstantiator(feed, collectionId, artist);

        verify(episodeRepository).getAllEpisodesSorted(anyString(), anyInt(), anyString(),
                loadEpisodesCallbackArgumentCaptor.capture());
        loadEpisodesCallbackArgumentCaptor.getValue().onEpisodesLoaded(testList);

        InOrder inOrder = Mockito.inOrder(bottomSheetView);
        inOrder.verify(bottomSheetView).showLoadingIndicator(true);
        inOrder.verify(bottomSheetView).setEpisodeList(testList);
        inOrder.verify(bottomSheetView).showLoadingIndicator(false);
        inOrder.verify(bottomSheetView).populateBottomSheetViews();
    }

    @Test
    public void getDescription_Should_SetDescription_When_PodcastValid() {
        String description = "My Description";
        presenter.getDescription();

        verify(episodeRepository).getDescription(stringCallbackArgumentCaptor.capture());
        stringCallbackArgumentCaptor.getValue().onStringReturned(description);

        verify(bottomSheetView).setPodcastDescription(description);
    }

    @Test
    public void getDescription_Should_PrintError_When_DescriptionNull() {
        String description = null;
        presenter.getDescription();

        verify(episodeRepository).getDescription(stringCallbackArgumentCaptor.capture());
        stringCallbackArgumentCaptor.getValue().onStringReturned(description);

        verify(bottomSheetView).setPodcastDescription("Podcast Description Failed to load");
    }

    @Test
    public void deleteEpisode_Should_DeleteFromSql_When_EpisodeValid() {
        Episode episode = new Episode();

        // TODO: Will later add in checks to make sure its a valid episode
        presenter.deleteEpisode(episode);

        verify(episodeRepository).deleteEpisode(episode);
    }

    @Test
    public void subscribe_Should_AddPodToSql_When_PodcastValid() {
        Podcast podcast = new Podcast();
        int autoUpdate = 1;

        presenter.subscribe(podcast, autoUpdate);

        verify(podcastRepository).subscribe(podcast, autoUpdate);
        verify(bottomSheetView).hideSubscribeButton();
    }

    @Test
    public void unsubscribe_Should_RemovePodFromSql_When_PodcastValid() {
        Podcast podcast = new Podcast();

        // TODO: Will later add in checks to make sure its a valid podcast
        presenter.unsubscribe(podcast);

        verify(podcastRepository).unsubscribe(podcast);
        verify(bottomSheetView).showSubscribeButton();
    }

    @Test
    public void updatePodcast_Should_UpdatePodInfoInSql_When_PodcastValid() {
        Podcast podcast = new Podcast();
        int autoUpdate = 1;

        // TODO: Will later add in checks to make sure its a valid podcast
        presenter.updatePodcast(podcast, autoUpdate);

        verify(podcastRepository).updatePodcast(podcast, autoUpdate);
    }

    @Test
    public void doesPodcastExist_Should_SetUnsubscribeButton_When_PodcastExistsInSql() {
        boolean exists = true;
        Podcast podcast = new Podcast();

        presenter.doesPodcastExist(podcast);

        verify(podcastRepository).doesPodcastExist(any(Podcast.class),
                podcastExistCallbackArgumentCaptor.capture());
        podcastExistCallbackArgumentCaptor.getValue().onExistLoaded(exists);

        verify(bottomSheetView, times(1)).hideSubscribeButton();
        verify(bottomSheetView, times(0)).showSubscribeButton();
    }

    @Test
    public void doesPodcastExist_Should_SetSubscribeButton_When_PodcastNotExistInSql() {
        boolean exists = false;
        Podcast podcast = new Podcast();

        presenter.doesPodcastExist(podcast);

        verify(podcastRepository).doesPodcastExist(any(Podcast.class),
                podcastExistCallbackArgumentCaptor.capture());
        podcastExistCallbackArgumentCaptor.getValue().onExistLoaded(exists);

        verify(bottomSheetView, times(0)).hideSubscribeButton();
        verify(bottomSheetView, times(1)).showSubscribeButton();
    }

}
