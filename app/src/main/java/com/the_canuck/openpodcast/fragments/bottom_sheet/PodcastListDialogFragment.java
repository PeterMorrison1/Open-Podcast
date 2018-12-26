package com.the_canuck.openpodcast.fragments.bottom_sheet;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.application.PodcastApplication;
import com.the_canuck.openpodcast.data.episode.EpisodeRepository;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;
import com.the_canuck.openpodcast.dialogs.EpisodeDialog;
import com.the_canuck.openpodcast.download.DownloadCompleteService;
import com.the_canuck.openpodcast.download.DownloadHelper;
import com.the_canuck.openpodcast.download.DownloadHelperApi;
import com.the_canuck.openpodcast.download.DownloadHelperApiImpl;
import com.the_canuck.openpodcast.fragments.FragmentComponent;
import com.the_canuck.openpodcast.fragments.library.MyLibraryRecyclerViewAdapter;
import com.the_canuck.openpodcast.fragments.settings.PreferenceKeys;
import com.the_canuck.openpodcast.media_store.MediaStoreHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static android.content.Context.MODE_PRIVATE;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     PodcastListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link PodcastListDialogFragment.Listener}.</p>
 */
public class PodcastListDialogFragment extends BottomSheetDialogFragment
        implements BottomSheetContract.BottomSheetView {

    public BottomSheetContract.BottomSheetPresenter bottomSheetPresenter;

    @Inject
    public EpisodeRepository episodeRepository;

    @Inject
    public PodcastRepository podcastRepository;

    @Inject
    public FragmentManager fragmentManager;

    @Inject
    public Context context;

    private Listener mListener;

    // Podcast and it's parameters
    // TODO: Change all of the params to just podcast.get[thing] later
    private Podcast podcast;

    private int collectionId;
    private String artistName;
    private String artwork600;
    private String artwork100;
    private String collectionName;
    private int trackCount;
    private String feedUrl;

    private List<Episode> episodes;
    private String podcastDescription;
    private Bitmap bitmapResource;
    private RecyclerView libraryRecyclerView;
    private int position = -1;
    private RecyclerView recyclerView;
    private Palette palette;

    private Bundle podcastBundle;

    // download manager
    private long enqueue;
    private String status;
    private Uri uri;

    private TextView title;
    private TextView artist;
    private TextView description;

    private FragmentComponent component;

    // Views
    private Button subscribeButton;
    private Button unsubscribeButton;
    private ImageView image;
    private ConstraintLayout constraintLayout;
    private ConstraintLayout descriptionLayout;
    private Button settingsButton;

    public static PodcastListDialogFragment newInstance() {
        final PodcastListDialogFragment fragment = new PodcastListDialogFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public PodcastListDialogFragment setPodcastBundle(Bundle podcastBundle) {
        this.podcastBundle = podcastBundle;
        return this;
    }

    public PodcastListDialogFragment setLibraryRecyclerView(RecyclerView libraryRecyclerView) {
        this.libraryRecyclerView = libraryRecyclerView;
        return this;
    }

    public PodcastListDialogFragment setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        podcast = (Podcast) podcastBundle.getSerializable(Podcast.PODCAST);

        initializePodcastParameters();


        component = PodcastApplication.get().plusFragmentComponent(this);
        component.inject(this);

        bottomSheetPresenter = new BottomSheetPresenter(this, episodeRepository,
                podcastRepository);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_podcast_list_dialog, container,false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        // Creates the list of episodes for the episode list adapter
        bottomSheetPresenter.episodeListInstantiator(feedUrl, collectionId, artistName);

        // -------------------------------- Initialize Views --------------------------------
        title = view.findViewById(R.id.bottom_sheet_title);
        artist = view.findViewById(R.id.bottom_sheet_artist);
        image = view.findViewById(R.id.bottom_sheet_image);
        description = view.findViewById(R.id.bottom_sheet_description);
        constraintLayout = view.findViewById(R.id.bottom_sheet_constraint_colour);
        descriptionLayout = view.findViewById(R.id.description_layout);
        subscribeButton = view.findViewById(R.id.description_button_subscribe);
        unsubscribeButton = view.findViewById(R.id.description_button_unsubscribe);
        settingsButton = view.findViewById(R.id.bottom_sheet_settings);

        // ------------------------------- Title & artist properties -------------------------------
        title.setText(collectionName);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(1);

        artist.setText(artistName);
        artist.setEllipsize(TextUtils.TruncateAt.END);
        artist.setMaxLines(1);

        // check if the podcast clicked is already subscribed, then set (un)sub buttons accordingly
        bottomSheetPresenter.doesPodcastExist(podcast);

        // -------------------------------- Button Listeners --------------------------------
        subscribeButtonListener();
        unsubscribeButtonListener();
        settingsButtonListener();

        // Glide and palette :)
        runGlide();

    }

    private void runGlide() {
        // set options for Glide
        final RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_error_black_24dp)
                .override(600, 600);

        // load image content and set colours for bottom sheet using palette and the image
        Glide.with(context)
                .asBitmap()
                .load(podcast.getArtworkUrl600())
                .thumbnail(0.1f)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        Glide.with(context).load(podcast.getArtworkUrl100()).apply(myOptions).into(image);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                                                   Target<Bitmap> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        startPostponedEnterTransition();
                        bitmapResource = resource;
                        setBottomSheetColours(resource, title, artist, constraintLayout,
                                description, descriptionLayout);
                        setButtonColours(bitmapResource, subscribeButton, getResources().getDrawable
                                (R.drawable.ic_add_circle_outline_black_24dp));
                        setButtonColours(bitmapResource, unsubscribeButton,
                                getResources().getDrawable(R.drawable.ic_check_circle_black_24dp));
                        setButtonColours(bitmapResource, settingsButton,
                                getResources().getDrawable(R.drawable.ic_settings_white_24dp));
                        return false;
                    }
                })
                .apply(myOptions)
                .into(image);
    }

    private void initializePodcastParameters() {
        collectionId = podcast.getCollectionId();
        artistName = podcast.getArtistName();
        artwork600 = podcast.getArtworkUrl600();
        artwork100 = podcast.getArtworkUrl100();
        collectionName = podcast.getCollectionName();
        trackCount = podcast.getTrackCount();
        feedUrl = podcast.getFeedUrl();
    }

    private void subscribeButtonListener() {
        // when subscribe button is clicked it becomes invisible and the unsubscribe button visible
        // TODO: Consider making an animation for button press
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Set the most recent episode as the pubdate so auto download wont download every
                episode that exists for this podcast. Then add to sqlite.
                 */
                podcast.setNewestDownloadDate(episodes.get(0).getPubDate());
                bottomSheetPresenter.subscribe(podcast, 1);
            }
        });
    }

    private void unsubscribeButtonListener() {
        // when unsubscribe button is clicked it becomes invisible and the subscribe button visible
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetPresenter.unsubscribe(podcast);

                /* removes podcast from library recycler view if unsubbed
                -1 is the default value for position, since bottomsheet is called from non-library
                fragments
                 */
                if (position != -1) {
                    RecyclerView.Adapter adapter = libraryRecyclerView.getAdapter();
                    ((MyLibraryRecyclerViewAdapter)adapter).removePodcastAt(position);

                    dismiss();
                }
            }
        });
    }

    /**
     * Sets the onclicklistener for the podcast popupmenu element.
     */
    private void settingsButtonListener() {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "settings", Toast.LENGTH_SHORT).show();
                showSettingsPopup(v);
            }
        });
    }

    /**
     * Displays the podcast settings menu, and onclicklisteners for it's menu items.
     *
     * @param v the view of the button clicked to open the popupmenu
     */
    private void showSettingsPopup(View v) {
        final SharedPreferences prefs = context.getSharedPreferences(PreferenceKeys.PREF_AUTO_UPDATE,
                MODE_PRIVATE);
        final PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        // Sets the auto update button to be clicked or not, based on saved podcast info
        final boolean isAutoUpdate = prefs.getBoolean(podcast.getCollectionName(), true);
        popupMenu.getMenu().getItem(0).setChecked(isAutoUpdate);

        // Sets click listener for each menu item
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boolean mAutoUpdatePod = !isAutoUpdate;
                int update;

                // Can put switch statement here in future if more settings are added
                popupMenu.getMenu().getItem(0).setChecked(mAutoUpdatePod);
                prefs.edit().putBoolean(podcast.getCollectionName(), mAutoUpdatePod).apply();

                // Updates the podcast's auto update status in sqlite
                if (mAutoUpdatePod) {
                    update = 1;
                } else {
                    update = 0;
                }
                bottomSheetPresenter.updatePodcast(podcast, update);
                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public void showSubscribeButton() {
        // TODO: Make it just one button and swap what it does like my play button
        subscribeButton.setVisibility(View.VISIBLE);
        unsubscribeButton.setVisibility(View.GONE);
    }

    @Override
    public void hideSubscribeButton() {
        // TODO: Make it just one button and swap what it does like my play button
        subscribeButton.setVisibility(View.GONE);
        unsubscribeButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoadingIndicator(boolean active) {

    }

    @Override
    public void setEpisodeList(List<Episode> episodeList) {
        episodes = episodeList;
    }

    @Override
    public void populateBottomSheetViews() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = getView().findViewById(R.id.bottom_sheet_recyclerview);
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    recyclerView.setAdapter(new PodcastAdapter());
                    recyclerView.setHasFixedSize(false);
                    recyclerView.addItemDecoration(new DividerItemDecoration(context,
                            LinearLayoutManager.VERTICAL));

//                    description.setText(podcastDescription);
                }
            });
        }

    }

    @Override
    public void setPodcastDescription(String description) {
        podcastDescription = description;
        if (this.description == null) {
            this.description = getView().findViewById(R.id.bottom_sheet_description);
        }
        this.description.setText(podcastDescription);
    }

    @Override
    public void setDownloadEnqueue(long enqueue) {
        this.enqueue = enqueue;
    }

    @Override
    public void setDownloadStatus(String status) {
        this.status = status;
    }

    @Override
    public void setDownloadUri(Uri uri) {
        this.uri = uri;
    }

    /**
     * Gets the dominant colour swatch of the image and sets the (un)subscribe buttons to the
     * appropriate colours.
     *
     * @param resource bitmap of the podcast artwork
     * @param button (un)subscribe button being coloured
     * @param buttonDrawable drawable being used for the button being passed in
     */
    public void setButtonColours(Bitmap resource, View button, Drawable buttonDrawable) {
        // TODO: Getting the palette each time will probably slow things down, fix later
        if (resource != null) {
            Palette palette = Palette.from(resource).generate();
            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
            if (dominantSwatch != null) {
                // pass in current drawable for subscribe button (sub or unsub) and tint it
                Drawable drawable = buttonDrawable;
                drawable.setColorFilter(dominantSwatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
                button.setBackground(drawable);
            }
        }
    }

    /**
     * Gets the dominant colour swatch of the image and sets the constraint layout and text views
     * with appropriate colours.
     *
     * @param resource bitmap of the podcast artwork
     * @param title textview of the podcast title
     * @param artist textview of the podcast artist
     * @param constraintLayout layout holding the artwork, title, and artist in bottom sheet
     * @param descrpitonText textview of the podcast description
     * @param descriptionLayout layout holding the description and (un)subscribe buttons
     */
    public void setBottomSheetColours(Bitmap resource, TextView title, TextView artist,
                                      ConstraintLayout constraintLayout, TextView descrpitonText,
                                      ConstraintLayout descriptionLayout) {
        if (resource != null) {
            int defaultColour = 0x000000;
            palette = Palette.from(resource).generate();
            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
            if (dominantSwatch != null) {
                // title, artist, and constraint colours
                constraintLayout.setBackgroundColor(dominantSwatch.getRgb());
                title.setTextColor(dominantSwatch.getTitleTextColor());
                artist.setTextColor(dominantSwatch.getBodyTextColor());

                // description and constraint colours
                descriptionLayout.setBackgroundColor(dominantSwatch.getRgb());
                descrpitonText.setTextColor(dominantSwatch.getBodyTextColor());
            } else {
                constraintLayout.setBackgroundColor(defaultColour);
                descriptionLayout.setBackgroundColor(defaultColour);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mListener = (Listener) parent;
        } else {
            mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        PodcastApplication.get().clearFragmentComponent();
        super.onDetach();
    }

    public interface Listener {
        void onPodcastClicked(Episode episode);
        void onPlayClicked(Episode episode);
    }

    //TODO:----------- Viewholder stuff still needs to be added to BottomSheetPresenter -----------

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView episode;
        final Button downloadButton;
        final ProgressBar progressBar;
        final Button playButton;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate
                    (R.layout.fragment_podcast_list_dialog_item, parent, false));

            // -------------------------------- Initialize views --------------------------------

            episode = itemView.findViewById(R.id.episode);
            downloadButton = itemView.findViewById(R.id.download_button);
            progressBar = itemView.findViewById(R.id.episode_progress_bar);
            playButton = itemView.findViewById(R.id.play_episode_button);


            // -------------------------------- Button Listeners --------------------------------

            downloadButtonListener();
            episodeClickListener();
            playButtonListener();

        }

        private void playButtonListener() {
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onPlayClicked(episodes.get(getAdapterPosition()));
                        dismiss();
                    }
                }
            });
        }
        private void episodeClickListener() {
            episode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {

                        EpisodeDialog dialog =
                                EpisodeDialog.newInstance(episodes.get(getAdapterPosition()));
                        dialog.show(fragmentManager, "EpisodeDialog");

                        dialog.setmListener(new EpisodeDialog.EpisodeDialogListener() {
                            @Override
                            public void onDialogDeleteClick() {
                                // delete click
                                // FIXME: Episodes that didn't update downloadstatus wont delete
                                if (episodes.get(getAdapterPosition()).getDownloadStatus() == 1) {
                                    MediaStoreHelper.deleteEpisode(getContext(),
                                            episodes.get(getAdapterPosition()));
//                                    sqLiteHelper.deleteEpisode(episodes.get(getAdapterPosition()));
                                    bottomSheetPresenter.deleteEpisode(episodes.get(getAdapterPosition()));
//                                    downloadButton.setVisibility(View.VISIBLE);
//                                    downloadButton.setEnabled(true);

                                    episodes.get(getAdapterPosition()).setDownloadStatus(0);

                                    recyclerView.getAdapter()
                                            .notifyItemChanged(getAdapterPosition());

                                } else {
                                    Toast.makeText(getContext(),
                                            "Can't delete, episode isn't downloaded",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onDialogCloseClick() {
                                // close click
                            }
                        });
//                        mListener.onPodcastClicked(episodes.get(getAdapterPosition()));
//                        dismiss();
                    }
                }
            });
        }

        private void downloadButtonListener() {
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        // Creates and sets the download helper for the episode clicked
                        final DownloadHelper downloadHelper = new
                                DownloadHelper(episodes.get(getAdapterPosition()),
                                context);

                        DownloadHelperApi downloadHelperApi = new DownloadHelperApiImpl(downloadHelper);
                        bottomSheetPresenter.setDownloadHelperApi(downloadHelperApi);

                        bottomSheetPresenter.startDownload(episodes.get(getAdapterPosition()));

                        episodes.get(getAdapterPosition()).setDownloadStatus(2);

                        final long id = enqueue;

                        final int firstPosition = getAdapterPosition();

                        /* creates episode object for newly moved/downloading episode
                        and sets the download status to currently downloading and adds it to the
                        sqlite episodes table so re-opening bottom sheet it's info will persist.
                         */
                        final Episode movedEpisode = episodes.get(getAdapterPosition());

                        // Dear future me, dateSorter adds the episode into its new position in
                        // the episodes list. so just having remove here is okay.
                        episodes.remove(getAdapterPosition());

                        final int finalPosition = dateSorter(movedEpisode);
                        if (finalPosition != -1) {
                            recyclerView.getAdapter().notifyItemMoved(firstPosition, finalPosition);
//                            recyclerView.getAdapter().notifyDataSetChanged();
                        }

                        // ------------------------- Broadcast Receiver --------------------------


                        // Receives the download complete intent and adds episode to database
                        final BroadcastReceiver onComplete = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {

                                // Runs the download complete service to update sqlite database
                                SharedPreferences prefs =
                                        context.getSharedPreferences(PreferenceKeys.PREF_DOWNLOADS,
                                                MODE_PRIVATE);

                                if (prefs.getBoolean(PreferenceKeys.IS_FINISHED_DOWNLOADS,
                                        false)) {

                                    Intent serviceIntent = new Intent(context,
                                            DownloadCompleteService.class);

                                    context.startService(serviceIntent);
                                }

                                // gets the download event action
                                String action = intent.getAction();
//                                String status = downloadHelper.getDownloadStatus(enqueue);

                                // sets the status for a specific download enqueue
                                bottomSheetPresenter.getDownloadStatus(id);
                                final String downloadStatus = status;


                                // TODO: Move this to different method later to clean up

                                /* the valid check is needed because DownloadManager sends multiple
                                ACTION_DOWNLOAD_COMPLETE intents while downloading, not just when
                                finished the download. Also stops cancel from adding to the database
                                 */
                                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                    if (downloadStatus.equalsIgnoreCase(DownloadHelper.STATUS_SUCCESSFUL)) {
                                        // Update download status and update the episode in sqlite

                                        episodes.get(finalPosition).setDownloadStatus(1);
                                        recyclerView.getAdapter().notifyItemChanged(finalPosition);

                                        // It should unregister when closed anyways,
                                        // so not sure if it should be unregistered or not
//                                    context.unregisterReceiver(this);
                                    } else {
                                        // Handles canceled and failed downloads
                                        long id = intent.getLongExtra
                                                (DownloadManager.EXTRA_DOWNLOAD_ID, 0);

                                        bottomSheetPresenter.getDownloadUri(id);
                                        final Uri downloadUri = uri;

                                        /* uri is null when the download is canceled, which allows
                                        us to check if the action is for successful download or a
                                        canceled download
                                         */
                                        if (downloadUri == null) {
                                            episodes.get(finalPosition).setDownloadStatus(0);

                                            recyclerView.getAdapter()
                                                    .notifyItemChanged(finalPosition);
//                                            context.unregisterReceiver(this);
                                        }
                                    }
                                }
                            }
                        };

                        // Registers the above receiver (onComplete receiver)
                        v.getContext().registerReceiver(onComplete,
                                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }
            });

        }

        /**
         * Sorts an episode into an arraylist based on publish date.
         *
         * @param mEpisode the episode being inserted into a new position
         * @return index the episode was placed into
         */
        private int dateSorter(Episode mEpisode) {
            // Checks to see which episode was published first, then sets the new pos
            // TODO: Consider replacing with LinkHelper#addToListSorted
            int finalPosition = -1;
            Date currentEpisodeDate;
            DateFormat formatter = new SimpleDateFormat
                    ("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
            try {
                currentEpisodeDate = formatter.parse
                        (mEpisode.getPubDate());
                for (int i = 0; i < episodes.size(); i++) {
                    Date iterationDate = formatter.parse
                            (episodes.get(i).getPubDate());

                    if (currentEpisodeDate.compareTo(iterationDate) <= 0) {
                        episodes.add(i, mEpisode);
                        finalPosition = i;
                        break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return finalPosition;
        }
    }

    private class PodcastAdapter extends RecyclerView.Adapter<ViewHolder> {

        PodcastAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int itemPosition) {
            holder.episode.setText(episodes.get(itemPosition).getTitle());

            // This elif updates any changes to download/play/progress just change downloadstatus

            // Sets download button to invisible if ep is downloaded or pod is not subscribed
            if (episodes.get(itemPosition).getDownloadStatus() == 1) {
                // downloaded, show play button
                holder.downloadButton.setVisibility(View.INVISIBLE);
                holder.downloadButton.setEnabled(false);
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.playButton.setVisibility(View.VISIBLE);
                holder.playButton.setEnabled(true);
            } else if (episodes.get(itemPosition).getDownloadStatus() == 2) {
                // downloading, show progress bar
                holder.downloadButton.setVisibility(View.INVISIBLE);
                holder.downloadButton.setEnabled(false);
                holder.progressBar.setVisibility(View.VISIBLE);
            } else if (episodes.get(itemPosition).getDownloadStatus() == 0 && position != -1) {
                // not downloaded, show download button. Must be library bottom sheet (subscribed)
                holder.downloadButton.setVisibility(View.VISIBLE);
                holder.downloadButton.setEnabled(true);
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.playButton.setVisibility(View.INVISIBLE);
                holder.playButton.setEnabled(false);
            }
        }

        @Override
        public int getItemCount() {
            if (episodes != null) {
                return episodes.size();
            } else {
                // TODO: If episodes is null show an error message or attempt to reopen this
                return 0;
            }
        }

    }
}
