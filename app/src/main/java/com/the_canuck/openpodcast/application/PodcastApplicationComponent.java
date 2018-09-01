package com.the_canuck.openpodcast.application;

import android.content.SharedPreferences;

import com.the_canuck.openpodcast.activities.MainActivityComponent;
import com.the_canuck.openpodcast.activities.MainActivityModule;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import dagger.Component;

@PodcastApplicationScope
@Component (modules = {PodcastApplicationDataModule.class})
public interface PodcastApplicationComponent {

    MainActivityComponent plusMainActivityComponent(MainActivityModule mainActivityModule);

    MySQLiteHelper getMySQLiteHelper();

    SharedPreferences getSharedPreferences();

}
