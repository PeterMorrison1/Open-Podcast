package com.the_canuck.openpodcast.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.the_canuck.openpodcast.fragments.settings.PreferenceKeys;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadHelper downloadHelper = new DownloadHelper();
        SharedPreferences prefs = context.getSharedPreferences(PreferenceKeys.PREF_DOWNLOADS,
                Context.MODE_PRIVATE);

        String action = intent.getAction();

        try {

            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                // Set the enqueue/download id for downloadhelper since its a new object again
                downloadHelper.setEnqueue(id);
                downloadHelper.setContext(context);

                String status = downloadHelper.getDownloadStatus(id);

                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    if (status != null) {
                        // Create pref to query in DownloadCompleteService
                        prefs.edit().putString(String.valueOf(id), status).commit();

                        // Create pref to query onResume to know if there are files in above pref
                        prefs.edit().putBoolean(PreferenceKeys.IS_FINISHED_DOWNLOADS, true).commit();
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
