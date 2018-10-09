package com.the_canuck.openpodcast.update_pods;

import android.support.annotation.NonNull;
import android.util.Log;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.download.DownloadHelper;
import com.the_canuck.openpodcast.misc_helpers.ListHelper;
import com.the_canuck.openpodcast.search.RssReader;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.work.Worker;

public class DownloadWorker extends Worker {

    private MySQLiteHelper sqLiteHelper;

    @NonNull
    @Override
    public Result doWork() {
        // TODO: Remove logs before final release
        Log.d("test", "Enter worker");
        sqLiteHelper = new MySQLiteHelper(getApplicationContext());

        UpdateHelper updateHelper = new UpdateHelper(sqLiteHelper, getApplicationContext());
        updateHelper.downloadNewEpisodes();

        Log.d("test", "Exit worker");

        // TODO: Maybe set try-catch around method calls to be able to return Result.FAILURE
        return Result.SUCCESS;
    }
}
