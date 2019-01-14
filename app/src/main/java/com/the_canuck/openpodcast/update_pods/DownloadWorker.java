package com.the_canuck.openpodcast.update_pods;

import android.content.Context;
import android.support.annotation.NonNull;

import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DownloadWorker extends Worker {

    private MySQLiteHelper sqLiteHelper;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        sqLiteHelper = new MySQLiteHelper(getApplicationContext());

        UpdateHelper updateHelper = new UpdateHelper(sqLiteHelper, getApplicationContext());
        updateHelper.downloadNewEpisodes();

        // TODO: Maybe set try-catch around method calls to be able to return Result.FAILURE
        return Result.success();
    }
}
