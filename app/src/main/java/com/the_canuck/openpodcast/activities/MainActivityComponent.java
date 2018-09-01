package com.the_canuck.openpodcast.activities;

import com.the_canuck.openpodcast.fragments.FragmentComponent;
import com.the_canuck.openpodcast.fragments.FragmentModule;

import dagger.Subcomponent;

@Subcomponent(modules = MainActivityModule.class)
@MainActivityScope
public interface MainActivityComponent {

    void injectMainActivity(MainActivity mainActivity);

//    FragmentComponent plusLibraryComponent(LibraryFragmentModule libraryFragmentModule);

//    FragmentComponent plusSearchComponent(SearchModule searchModule);

    FragmentComponent plusFragmentComponent(FragmentModule fragmentModule);

}
