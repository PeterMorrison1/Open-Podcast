package com.the_canuck.openpodcast.fragments.bottom_sheet;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.the_canuck.openpodcast.dialogs.EpisodeDialog;
import com.the_canuck.openpodcast.download.DownloadCompleteService;
import com.the_canuck.openpodcast.download.DownloadHelper;
import com.the_canuck.openpodcast.download.DownloadReceiver;
import com.the_canuck.openpodcast.fragments.library.MyLibraryRecyclerViewAdapter;
import com.the_canuck.openpodcast.fragments.settings.PreferenceKeys;
import com.the_canuck.openpodcast.media_store.MediaStoreHelper;
import com.the_canuck.openpodcast.search.RssReader;
import com.the_canuck.openpodcast.search.enums.ItunesJsonKeys;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     PodcastListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 * <p>You activity (or fragment) needs to implement {@link PodcastListDialogFragment.Listener}.</p>
 */
public class PodcastListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static Listener mListener;
    private static int collectionId;
    private static String artistName;
    private static String artwork600;
    private static String artwork100;
    private static String collectionName;
    private static String censoredName;
    private static int trackCount;
    private static String feedUrl;

    private List<Episode> episodes;
    private RssReader reader;
    private Bitmap bitmapResource;
    private MySQLiteHelper sqLiteHelper;
    private RecyclerView libraryRecyclerView;
    private int position = -1;
    private RecyclerView recyclerView;
    private Palette palette;

    public static PodcastListDialogFragment newInstance(int collectionId, String artistName,
                                                 String artwork600, String artwork100,
                                                 String collectionName, String censoredName,
                                                 int trackCount, String feedUrl) {
        PodcastListDialogFragment.collectionId = collectionId;
        PodcastListDialogFragment.artistName = artistName;
        PodcastListDialogFragment.artwork600 = artwork600;
        PodcastListDialogFragment.artwork100 = artwork100;
        PodcastListDialogFragment.collectionName = collectionName;
        PodcastListDialogFragment.censoredName = censoredName;
        PodcastListDialogFragment.trackCount = trackCount;
        PodcastListDialogFragment.feedUrl = feedUrl;

        final PodcastListDialogFragment fragment = new PodcastListDialogFragment();
        final Bundle args = new Bundle();

        // TODO: Can probably remove these at some time honestly
        args.putInt(ItunesJsonKeys.COLLECTIONID.getValue(), collectionId);
        args.putString(ItunesJsonKeys.ARTISTNAME.getValue(), artistName);
        args.putString(ItunesJsonKeys.ARTWORKURL600.getValue(), artwork600);
        args.putString(ItunesJsonKeys.ARTWORKURL100.getValue(), artwork100);
        args.putString(ItunesJsonKeys.COLLECTIONNAME.getValue(), collectionName);
        args.putString(ItunesJsonKeys.TRACKCENSOREDNAME.getValue(), censoredName);
        args.putInt(ItunesJsonKeys.TRACKCOUNT.getValue(), trackCount);
        args.putString(ItunesJsonKeys.FEEDURL.getValue(), feedUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public PodcastListDialogFragment setLibraryRecyclerView(RecyclerView libraryRecyclerView) {
        this.libraryRecyclerView = libraryRecyclerView;
        return this;
    }

    public PodcastListDialogFragment setPosition(int position) {
        this.position = position;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_podcast_list_dialog, container,false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        final Podcast podcast = buildPodcast();
        sqLiteHelper = new MySQLiteHelper(view.getContext());

        recyclerView = view.findViewById(R.id.bottom_sheet_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new PodcastAdapter());
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),
                LinearLayoutManager.VERTICAL));

        // Creates the list of episodes for the episode list adapter
        new Thread(new Runnable() {
            @Override
            public void run() {
                episodeListInstantiator();
            }
        }).start();

        // TODO: Replace later with a loading bar/circle while content not loaded
        while (episodes == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // TODO: Make the instantiations and declarations more readable and in their own method
        final TextView title = view.findViewById(R.id.bottom_sheet_title);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(1);

        final TextView artist = view.findViewById(R.id.bottom_sheet_artist);
        artist.setEllipsize(TextUtils.TruncateAt.END);
        artist.setMaxLines(1);

        final ImageView image = view.findViewById(R.id.bottom_sheet_image);
        final TextView description = view.findViewById(R.id.bottom_sheet_description);

        final ConstraintLayout constraintLayout =
                view.findViewById(R.id.bottom_sheet_constraint_colour);
        final ConstraintLayout descriptionLayout = view.findViewById(R.id.description_layout);
        final Button subscribeButton = view.findViewById(R.id.description_button_subscribe);
        final Button unsubscribeButton = view.findViewById(R.id.description_button_unsubscribe);
        final Button settingsButton = view.findViewById(R.id.bottom_sheet_settings);

        // check if the podcast clicked is already subscribed, then set (un)sub buttons accordingly
        if (sqLiteHelper.doesPodcastExist(podcast)) {
            unsubscribeButton.setVisibility(View.VISIBLE);
            subscribeButton.setVisibility(View.GONE);
        } else {
            unsubscribeButton.setVisibility(View.GONE);
            subscribeButton.setVisibility(View.VISIBLE);
        }

        // when subscribe button is clicked it becomes invisible and the unsubscribe button visible
        // TODO: Consider making an animation for button press
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Set the most recent episode as the pubdate so auto download wont download every
                episode that exists for this podcast. Then add to sqlite.
                TODO: Later will be moved to follow MVP better.
                 */
                podcast.setNewestDownloadDate(episodes.get(0).getPubDate());
                sqLiteHelper.subscribe(podcast, 1);
                subscribeButton.setVisibility(View.GONE);
                unsubscribeButton.setVisibility(View.VISIBLE);
            }
        });

        // when unsubscribe button is clicked it becomes invisible and the subscribe button visible
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteHelper.unsubscribe(podcast);
                subscribeButton.setVisibility(View.VISIBLE);
                unsubscribeButton.setVisibility(View.GONE);

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

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "settings", Toast.LENGTH_SHORT).show();
            }
        });

        // Set text for title, artist, and description
        title.setText(collectionName);
        artist.setText(artistName);
        description.setText(reader.getPodcastDescription());

        // set options for Glide
        final RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_error_black_24dp)
                .override(600, 600);

        // load image content and set colours for bottom sheet using palette and the image
        Glide.with(view.getContext())
                .asBitmap()
                .load(artwork600)
                .thumbnail(0.1f)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        Glide.with(view.getContext()).load(artwork100).apply(myOptions).into(image);
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
//                ColorFilter filter = new LightingColorFilter(dominantSwatch.getBodyTextColor(), dominantSwatch.getBodyTextColor());
                drawable.setColorFilter(dominantSwatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
                button.setBackground(drawable);
//                drawable = DrawableCompat.wrap(drawable);
//                DrawableCompat.setTint(drawable, dominantSwatch.getTitleTextColor());
//                button.setCompoundDrawables(null, drawable, null, null);
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

    /**
     * Creates an arraylist with downloaded episodes from database at the lowest # index,
     * and sets the non-downloaded episodes from rssReader at higher # index than downloaded,
     * sets the {@link PodcastListDialogFragment#episodes} to the combined list of downloaded and
     * non-downloaded episodes.
     */
    private void episodeListInstantiator() {
        reader = new RssReader(feedUrl);
        reader.setCollectionId(collectionId);
        reader.setCollectionArtist(artistName);
        List<Episode> rssEpisodeList = reader.createEpisodeList();
        List<Episode> sqlEpisodeList = sqLiteHelper.getEpisodes(collectionId);

        episodes = new ArrayList<>();

        if (!sqlEpisodeList.isEmpty()) {
            // deletes episodes from RSS list if they have matching title with the SQLite episode
            for (Episode episodeSqlite : sqlEpisodeList) {
                boolean found = false;
                int i = 0;
                while (i < rssEpisodeList.size() && !found) {
                    if (rssEpisodeList.get(i).getTitle().equalsIgnoreCase(episodeSqlite.getTitle())) {
                        rssEpisodeList.remove(i);
                        found = true;
                    } else {
                        i++;
                    }
                }
            }
            episodes.addAll(sqlEpisodeList);
            episodes.addAll(rssEpisodeList);

        } else {
            episodes = rssEpisodeList;
        }
    }

    // TODO: Refactor to serialize podcast and send to this class instead of rebuilding podcast obj
    /**
     * Builds a podcast object with information for currently clicked podcast.
     *
     * @return podcast object.
     */
    private Podcast buildPodcast() {
        Podcast newPodcast;
        newPodcast = new Podcast.PodcastBuilder()
                .setCollectionName(collectionName)
                .setCensoredName(censoredName)
                .setCollectionId(collectionId)
                .setArtistName(artistName)
                .setTrackCount(trackCount)
                .setArtworkUrl100(artwork100)
                .setArtworkUrl600(artwork600)
                .setFeedUrl(feedUrl)
                .build();
        return newPodcast;
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
        sqLiteHelper.close();
        super.onDetach();
    }

    public interface Listener {
        void onPodcastClicked(Episode episode);
        void onPlayClicked(Episode episode);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView episode;
        final Button downloadButton;
        final ProgressBar progressBar;
        final Button playButton;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate
                    (R.layout.fragment_podcast_list_dialog_item, parent, false));

            episode = itemView.findViewById(R.id.episode);
            downloadButton = itemView.findViewById(R.id.download_button);
            progressBar = itemView.findViewById(R.id.episode_progress_bar);
            playButton = itemView.findViewById(R.id.play_episode_button);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
//                        runEpisodeDownloader(v);

                        downloadButton.setVisibility(View.INVISIBLE);
                        downloadButton.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        final DownloadHelper downloadHelper = new
                                DownloadHelper(episodes.get(getAdapterPosition()), collectionId,
                                v.getContext());
                        long enqueue = downloadHelper.downloadEpisode();

                        final int firstPosition = getAdapterPosition();

                        /* creates episode object for newly moved/downloading episode
                        and sets the download status to currently downloading and adds it to the
                        sqlite episodes table so re-opening bottom sheet it's info will persist.
                         */
                        final Episode movedEpisode = episodes.get(getAdapterPosition());
                        movedEpisode.setDownloadId(enqueue);
                        movedEpisode.setDownloadStatus(Episode.CURRENTLY_DOWNLOADING);
                        sqLiteHelper.addEpisode(movedEpisode);

                        // TODO: Is this missing an add statement or should it be just remove?
                        episodes.remove(getAdapterPosition());

                        final int finalPosition = dateSorter(movedEpisode);
                        if (finalPosition != -1) {
                            recyclerView.getAdapter().notifyItemMoved(firstPosition, finalPosition);
//                            recyclerView.getAdapter().notifyDataSetChanged();
                        }

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

                                String action = intent.getAction();
                                String status = downloadHelper.getDownloadStatus();

                                /* the valid check is needed because DownloadManager sends multiple
                                ACTION_DOWNLOAD_COMPLETE intents while downloading, not just when
                                finished the download. Also stops cancel from adding to the database
                                 */
                                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                                    if (status.equalsIgnoreCase(DownloadHelper.STATUS_SUCCESSFUL)) {
                                        // Update download status and update the episode in sqlite

                                        Toast.makeText(context, "Download Complete",
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        recyclerView.getAdapter().notifyItemChanged(finalPosition);
//                                    context.unregisterReceiver(this);
                                    } else {
                                        // Handles canceled and failed downloads
                                        Uri uri;
                                        long id = intent.getLongExtra
                                                (DownloadManager.EXTRA_DOWNLOAD_ID, 0);

                                        uri = downloadHelper.getDownloadUri(id);

                                        /* uri is null when the download is canceled, which allows
                                        us to check if the action is for successful download or a
                                        canceled download
                                         */
                                        if (uri == null) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            downloadButton.setVisibility(View.VISIBLE);
                                            downloadButton.setEnabled(true);
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

            episode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        EpisodeDialog dialog =
                                EpisodeDialog.newInstance(episodes.get(getAdapterPosition()));
                        dialog.show(getFragmentManager(), "EpisodeDialog");

                        dialog.setmListener(new EpisodeDialog.EpisodeDialogListener() {
                            @Override
                            public void onDialogDeleteClick() {
                                // delete click
                                // FIXME: Episodes that didn't update downloadstatus wont delete
                                if (episodes.get(getAdapterPosition()).getDownloadStatus() == 1) {
                                    MediaStoreHelper.deleteEpisode(getContext(),
                                            episodes.get(getAdapterPosition()));
                                    sqLiteHelper.deleteEpisode(episodes.get(getAdapterPosition()));
                                    downloadButton.setVisibility(View.VISIBLE);
                                    downloadButton.setEnabled(true);

                                    // FIXME: Doesn't seem to update view with download button
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
            return episodes.size();
        }

    }
}
