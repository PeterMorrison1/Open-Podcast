package com.the_canuck.openpodcast.download;

import android.net.Uri;


public interface DownloadHelperApi {

    interface GetEnqueueCallback {

        void onEnqueueLoaded(long enqueue);
    }

    interface GetStatusCallback {

        void onStatusReturned(String status);
    }

    interface GetURICallback {

        void onURIReturned(Uri uri);
    }

    void startDownload(GetEnqueueCallback callback);

    void getStatus(long enqueue, GetStatusCallback callback);

    void getURI(long downloadId, GetURICallback callback);
}
