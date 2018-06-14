package com.the_canuck.openpodcast.activities;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.fragments.discover.DiscoverFragment;
import com.the_canuck.openpodcast.fragments.library.LibraryFragment;
import com.the_canuck.openpodcast.fragments.library.dummy.DummyContent;
import com.the_canuck.openpodcast.fragments.search_results.PodcastListDialogFragment;
import com.the_canuck.openpodcast.fragments.search_results.SearchFragment;
import com.the_canuck.openpodcast.fragments.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity implements
        SearchFragment.OnListFragmentInteractionListener, PodcastListDialogFragment.Listener,
        LibraryFragment.OnListFragmentInteractionListener,
        DiscoverFragment.OnListFragmentInteractionListener {

    private DrawerLayout mDrawerLayout;
    final String LIBRARY_TAG = "library";
    final String SEARCH_TAG = "search";
    final String DISCOVER_TAG = "discover";
    final String SETTINGS_TAG = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleIntent(getIntent());

        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Sets the fragment container as library fragment on startup
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            Fragment libraryFragment = new LibraryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, libraryFragment);
            transaction.addToBackStack(LIBRARY_TAG);
            transaction.commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_subscribed);
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                Fragment container = getSupportFragmentManager().findFragmentById
                        (R.id.fragment_container);

                Fragment newFragment = null;
                String fragTag = null;

                switch (item.getItemId()) {
                    case R.id.nav_subscribed:
                        if (container instanceof LibraryFragment) {
                            mDrawerLayout.closeDrawers();
                            return true;
                        } else {
                            getSupportFragmentManager().popBackStack(LIBRARY_TAG,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            newFragment = new LibraryFragment();
                            fragTag = LIBRARY_TAG;
                        }
                        break;

                    case R.id.nav_search:
                        if (container instanceof DiscoverFragment) {
                            mDrawerLayout.closeDrawers();
                            return true;
                        } else {
                            getSupportFragmentManager().popBackStack(DISCOVER_TAG,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            newFragment = new DiscoverFragment();
                            fragTag = DISCOVER_TAG;
                        }
                        break;

                    case R.id.nav_settings:
                        if (container instanceof SettingsFragment) {
                            mDrawerLayout.closeDrawers();
                            return true;
                        } else {
                            getSupportFragmentManager().popBackStack(SETTINGS_TAG,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            newFragment = new SettingsFragment();
                            fragTag = SETTINGS_TAG;
                        }
                }
                if (newFragment != null) {
                    replaceFragment(newFragment, fragTag);
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Takes the search query from the searchview intent and sends it to the search fragment.
     *
     * @param intent from the searchview, which holds query that contains user input
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Bundle bundle = new Bundle();

            String query = intent.getStringExtra(SearchManager.QUERY);
            bundle.putString("query", query);
            bundle.putBoolean("isGenre", false);

            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(bundle);
            replaceFragment(searchFragment, SEARCH_TAG);
        }
    }

    public void genreSearchIntent(Intent intent) {
        Bundle bundle = new Bundle();

        String query = intent.getStringExtra("query");
        bundle.putString("query", query);
        bundle.putBoolean("isGenre", intent.getBooleanExtra("isGenre", false));
        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(bundle);
        replaceFragment(searchFragment, SEARCH_TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Swaps out current fragment in fragment container for new fragment, adds old to backstack.
     *
     * @param newFragment new fragment being created/put in view
     * @param tag tag of the fragment for possible query later
     */
    public void replaceFragment(Fragment newFragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // closes the search menu so you don't have to hit backbutton 3 times to go back
                searchItem.collapseActionView();
                getSupportFragmentManager().popBackStack(SEARCH_TAG,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // This code below can change the icons of the search bar
//        ImageView searchCloseIcon = searchView.findViewById
//                (android.support.v7.appcompat.R.id.search_close_btn);
//        searchCloseIcon.setImageResource(R.drawable.ic_close_white_24dp);

        return true;
    }

    @Override
    public void onPodcastClicked(int position) {

    }

    @Override
    public void onListFragmentInteraction(Podcast item) {
        PodcastListDialogFragment.newInstance(item.getCollectionId(), item.getArtistName(),
                item.getArtworkUrl600(), item.getArtworkUrl100(), item.getCollectionName(),
                item.getCensoredName(), item.getTrackCount(), item.getFeedUrl()).show(getSupportFragmentManager(),
                "dialog");
    }

    @Override
    public void onListFragmentInteractionLibrary(Podcast item) {

    }

    @Override
    public void onFragmentInteraction(Podcast item) {
        PodcastListDialogFragment.newInstance(item.getCollectionId(), item.getArtistName(),
                item.getArtworkUrl600(), item.getArtworkUrl100(), item.getCollectionName(),
                item.getCensoredName(), item.getTrackCount(), item.getFeedUrl()).show(getSupportFragmentManager(),
                "dialog");
    }
}
