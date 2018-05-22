package com.the_canuck.openpodcast.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.fragments.search_results.PodcastListDialogFragment;
import com.the_canuck.openpodcast.fragments.search_results.SearchFragment;

public class MainActivity extends AppCompatActivity implements
        SearchFragment.OnListFragmentInteractionListener, PodcastListDialogFragment.Listener{

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleIntent(getIntent());

        mDrawerLayout = findViewById(R.id.drawer_layout);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_subscribed);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.nav_subscribed:
                        mDrawerLayout.closeDrawers();
                        return true;

                    case R.id.nav_search:
                        mDrawerLayout.closeDrawers();
                        return true;
                }
                return true;
            }
        });
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
            String query = intent.getStringExtra(SearchManager.QUERY);

            Bundle bundle = new Bundle();
            bundle.putString("query", query);

            SearchFragment searchFragment = new SearchFragment();
            searchFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.search_fragment, searchFragment);
            transaction.commit();
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setSubmitButtonEnabled(true);

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
                item.getCensoredName(), item.getTrackCount()).show(getSupportFragmentManager(),
                "dialog");
    }
}
