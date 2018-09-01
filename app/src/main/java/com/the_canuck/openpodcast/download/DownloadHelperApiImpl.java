package com.the_canuck.openpodcast.download;


public class DownloadHelperApiImpl implements DownloadHelperApi {

    private DownloadHelper downloadHelper;

    public DownloadHelperApiImpl(DownloadHelper downloadHelper) {
        this.downloadHelper = downloadHelper;
    }

    @Override
    public void startDownload(GetEnqueueCallback callback) {
        long enqueue = downloadHelper.downloadEpisode();
        callback.onEnqueueLoaded(enqueue);
    }

    @Override
    public void getStatus(long enqueue, GetStatusCallback callback) {
        callback.onStatusReturned(downloadHelper.getDownloadStatus(enqueue));
    }

    @Override
    public void getURI(long id, GetURICallback callback) {
        callback.onURIReturned(downloadHelper.getDownloadUri(id));
    }
}
