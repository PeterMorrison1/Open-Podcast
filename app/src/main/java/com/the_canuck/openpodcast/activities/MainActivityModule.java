package com.the_canuck.openpodcast.activities;

import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.FragmentManager;


import androidx.work.WorkManager;
import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    private final MainActivity mainActivity;

    public MainActivityModule(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Provides
    @MainActivityScope
    public WorkManager workManager() {
        return WorkManager.getInstance();
    }

    @Provides
    @MainActivityScope
    public SearchManager searchManager() {
        return (SearchManager) mainActivity.getSystemService(Context.SEARCH_SERVICE);
    }

    @Provides
    @MainActivityScope
    public FragmentManager fragmentManager() {
        return mainActivity.getSupportFragmentManager();
    }

}
