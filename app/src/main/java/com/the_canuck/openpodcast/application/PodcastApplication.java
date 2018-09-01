package com.the_canuck.openpodcast.application;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.the_canuck.openpodcast.activities.MainActivity;
import com.the_canuck.openpodcast.activities.MainActivityComponent;
import com.the_canuck.openpodcast.activities.MainActivityModule;
import com.the_canuck.openpodcast.fragments.FragmentComponent;
import com.the_canuck.openpodcast.fragments.FragmentModule;

public class PodcastApplication extends Application {

    private static PodcastApplication instance;

    private PodcastApplicationComponent component;

    private PodcastApplicationComponent applicationComponent;
    private MainActivityComponent activityComponent;

    private FragmentComponent fragmentComponent;


    public static PodcastApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        applicationComponent = DaggerPodcastApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

    }

    public MainActivityComponent plusActivityComponent(MainActivity mainActivity) {
        if (activityComponent == null) {
            activityComponent = applicationComponent.plusMainActivityComponent(new MainActivityModule(mainActivity));
        }
        return activityComponent;
    }

    public void clearActivityComponent() {
        activityComponent = null;
    }

    public FragmentComponent plusFragmentComponent(Fragment fragment) {
        // TODO: Test changing passed value as Fragment and do elif statement for instanceof [specific]Fragment
        if (fragmentComponent == null) {
            fragmentComponent = activityComponent.plusFragmentComponent(new FragmentModule(fragment));
        }
        return fragmentComponent;
    }

    public void clearFragmentComponent() {
        fragmentComponent = null;
    }

    public PodcastApplicationComponent component() {
        return component;
    }
}
