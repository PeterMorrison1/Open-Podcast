package com.the_canuck.openpodcast.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.fragments.discover.DiscoverFragment;
import com.the_canuck.openpodcast.fragments.library.LibraryFragment;
import com.the_canuck.openpodcast.fragments.bottom_sheet.PodcastListDialogFragment;
import com.the_canuck.openpodcast.fragments.search_results.SearchFragment;
import com.the_canuck.openpodcast.fragments.settings.SettingsFragment;
import com.the_canuck.openpodcast.media_player.MediaPlayerService;
import com.the_canuck.openpodcast.media_store.MediaStoreHelper;
import com.the_canuck.openpodcast.misc_helpers.TimeHelper;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements
        SearchFragment.OnListFragmentInteractionListener, PodcastListDialogFragment.Listener,
        LibraryFragment.OnListFragmentInteractionListener,
        DiscoverFragment.OnListFragmentInteractionListener {

    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 112;
    final String LIBRARY_TAG = "library";
    final String SEARCH_TAG = "search";
    final String DISCOVER_TAG = "discover";
    final String SETTINGS_TAG = "settings";

    private DrawerLayout mDrawerLayout;
    private SlidingUpPanelLayout slidingPanel;
    private FrameLayout panelTinyContainer;
    private FrameLayout panelBigContainer;
    private TextView panelTinyTitle;
    private TextView panelBigTitle;
    private ImageView panelImage;
    private Palette palette;
    private AppCompatSeekBar seekBar;
    private TextView seekBarMaxDuration;
    private TextView seekBarCurrentDuration;
    private TextView thumbCardDuration;
    private CardView thumbCard;
    private Episode currentEpisode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        handleIntent(getIntent());

        // TODO: Make this based on android version
        requestAppPermissions();

        // TODO: Might want to remove this, since it will cost lots of resources on startup
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse(Environment.DIRECTORY_PODCASTS)));

        // TODO: Later make sliding panel visible from start with last played episode
        // TODO: Can make sliding panel invisible but not visible again ????????????????????
//        if (currentEpisode == null) {
//            panelBigContainer.setVisibility(View.INVISIBLE);
//            panelBigContainer.setClickable(false);
//        } else {
//            panelBigContainer.setVisibility(View.VISIBLE);
//            panelBigContainer.setClickable(true);
//        }

        slidingPanel.setParallaxOffset(1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarCurrentDuration.setText(TimeHelper.convertSecondsToHourMinSec(progress));
                thumbCardDuration.setText(TimeHelper.convertSecondsToHourMinSec(progress));

                // Sets the thumbCard to same position as the seekbar thumb
                // FIXME: thumbCard moves a bit too far left when at 00:00
                if (fromUser) {
                    thumbCard.setVisibility(View.VISIBLE);
                    int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) /
                            seekBar.getMax();
                    int cardHalf = thumbCard.getWidth() / 2;
                    thumbCard.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2 - cardHalf);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                thumbCard.setVisibility(View.INVISIBLE);
            }
        });


        // TODO: view this solution: https://github.com/umano/AndroidSlidingUpPanel/issues/750
        // It will fix the panel opening and closing on click rather than drag
        // Listens for when bottom panel is either sliding or changed state (PanelState enums)
        slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel,
                                            SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {

                if (slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    panelTinyContainer.setVisibility(View.VISIBLE);
                    panelTinyContainer.setEnabled(true);
                } else if (slidingPanel.getPanelState() ==
                        SlidingUpPanelLayout.PanelState.EXPANDED) {
                    // TODO: Maybe add animation here or animate to different viewholder
                    /* Update: Above comment means when you drag panel up the tiny bar remains until
                    the panel is completely expanded or collapsed. So fade away tiny bar basically.
                    Take this as a learning experience to write better comments, future Peter.
                     */
                    panelTinyContainer.setVisibility(View.INVISIBLE);
                    panelTinyContainer.setEnabled(false);
                }
            }
        });

        // Sets the fragment container as library fragment on startup
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            Fragment libraryFragment = new LibraryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, libraryFragment);
            transaction.addToBackStack(LIBRARY_TAG);
            transaction.commit();
        }

        // Set and control toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        // Set and control navigation view (The drawer)
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

                // Add future nav drawer selections here
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

    /**
     * Initialize views that are globally used for this activity.
     */
    private void initializeViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        slidingPanel = findViewById(R.id.panel_layout);
        panelTinyContainer = findViewById(R.id.panel_tiny_container);
        panelBigContainer = findViewById(R.id.panel_big_container);
        panelTinyTitle = findViewById(R.id.panel_small_title);
        panelBigTitle = findViewById(R.id.panel_big_title);
        panelImage = findViewById(R.id.panel_episode_image);
        seekBar = findViewById(R.id.seek_bar);
        seekBarMaxDuration = findViewById(R.id.seek_bar_max_duration);
        seekBarCurrentDuration = findViewById(R.id.seek_bar_current_duration);
        thumbCard = findViewById(R.id.thumb_card);
        thumbCardDuration = findViewById(R.id.thumb_duration);
    }

    /**
     * Prompts user for permissions to use the app.
     */
    private void requestAppPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE);
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

    /**
     * Sets the fragment to search for the clicked genre.
     *
     * @param intent the intent that contains the "isGenre" boolean, and the "query" term
     */
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

    /**
     * Sets the sliding up panel information (episode title, duration, colour, etc).
     *
     * @param episode the episode to use to fill the panel with
     */
    private void setSlidingPanelEpisode(Episode episode) {
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);

        String artwork = sqLiteHelper.getPodcastArtwork600(episode.getCollectionId());
        panelTinyTitle.setText(episode.getTitle());
        panelBigTitle.setText(episode.getTitle());
        seekBar.setMax((int) TimeHelper.convertDurationToSeconds(episode.getDuration()));

        // TODO: Change max duration to use BOOKMARK from mediastore/episodes table
        String maxDuration = "/  " + episode.getDuration();
        seekBarMaxDuration.setText(maxDuration);

        // Anything requiring palette colours MUST be inside onResourceReady below!
        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_error_black_24dp)
                .override(900, 900);

        Glide.with(this)
                .asBitmap()
                .load(artwork)
                .thumbnail(0.1f)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                                                   Target<Bitmap> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        // Sets the panel image and generates the palette from it
                        palette = Palette.from(resource).generate();
                        if (palette != null) {
                            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
                            if (dominantSwatch != null) {
                                // To use the palette it must be set inside here
                                panelBigContainer.setBackgroundColor(dominantSwatch.getRgb());
                                panelBigTitle.setTextColor(dominantSwatch.getTitleTextColor());
                                seekBarMaxDuration.setTextColor(dominantSwatch.getBodyTextColor());
                                seekBarCurrentDuration.setTextColor
                                        (dominantSwatch.getBodyTextColor());
                                thumbCard.setCardBackgroundColor(dominantSwatch.getTitleTextColor());
                                thumbCardDuration.setTextColor(dominantSwatch.getBodyTextColor());
                            }
                        }
                        return false;
                    }
                })
                .apply(myOptions)
                .into(panelImage);
    }


    @Override
    public void onPodcastClicked(Episode episode) {

    }

    @Override
    public void onPlayClicked(Episode episode) {
        currentEpisode = episode;
        Intent mIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
        mIntent.setAction(MediaPlayerService.ACTION_PLAY);
        mIntent.putExtra(Episode.EPISODE, episode);
        startService(mIntent);

        setSlidingPanelEpisode(episode);
//        try {
//            Uri test = MediaStoreHelper.getEpisodeUri(this, episode);
//            Log.d("mediastore", "Uri: " + test);
//            MediaPlayer mediaPlayer = new MediaPlayer();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
//                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
//                        .build());
//            } else {
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            }
//            mediaPlayer.setDataSource(getApplicationContext(), test);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onListFragmentInteraction(Podcast item) {
        PodcastListDialogFragment.newInstance(item.getCollectionId(), item.getArtistName(),
                item.getArtworkUrl600(), item.getArtworkUrl100(), item.getCollectionName(),
                item.getCensoredName(), item.getTrackCount(),
                item.getFeedUrl()).show(getSupportFragmentManager(),"dialog");
    }

    @Override
    public void onListFragmentInteractionLibrary(Podcast item) {
        PodcastListDialogFragment.newInstance(item.getCollectionId(), item.getArtistName(),
                item.getArtworkUrl600(), item.getArtworkUrl100(), item.getCollectionName(),
                item.getCensoredName(), item.getTrackCount(),
                item.getFeedUrl())
                .setLibraryRecyclerView(item.getRecyclerView())
                .setPosition(item.getPosition())
                .show(getSupportFragmentManager(),"dialog");
    }

    @Override
    public void onFragmentInteraction(Podcast item) {
        PodcastListDialogFragment.newInstance(item.getCollectionId(), item.getArtistName(),
                item.getArtworkUrl600(), item.getArtworkUrl100(), item.getCollectionName(),
                item.getCensoredName(), item.getTrackCount(),
                item.getFeedUrl()).show(getSupportFragmentManager(),"dialog");
    }
}
