package com.the_canuck.openpodcast.fragments.search_results;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.search.RssReader;
import com.the_canuck.openpodcast.search.SearchHelper;
import com.the_canuck.openpodcast.search.enums.ItunesJsonKeys;

import java.util.List;

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
//    private List<RSSItem> episodes;
    private List<Episode> episodes;
    RssReader reader;

    // TODO: Customize parameters
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
        Log.d("Test", "Title main: " + collectionName);

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_podcast_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.bottom_sheet_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new PodcastAdapter());
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL));

        new Thread(new Runnable() {
            @Override
            public void run() {
                reader = new RssReader(feedUrl);
                episodes = reader.createEpisodeList();
            }
        }).start();

        while (episodes == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getContext(), Integer.toString(episodes.size()), Toast.LENGTH_SHORT).show();

        final TextView title = view.findViewById(R.id.bottom_sheet_title);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(1);

        final TextView artist = view.findViewById(R.id.bottom_sheet_artist);
        artist.setEllipsize(TextUtils.TruncateAt.END);
        artist.setMaxLines(1);

        final ImageView image = view.findViewById(R.id.bottom_sheet_image);
        final TextView description = view.findViewById(R.id.bottom_sheet_description);

        final ConstraintLayout constraintLayout = view.findViewById(R.id.bottom_sheet_constraint_colour);

        title.setText(collectionName);
        artist.setText(artistName);
        description.setText(reader.getPodcastDescription());
        final RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_error_black_24dp)
                .override(600, 600);

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
                        setBottomSheetColours(resource, title, artist, constraintLayout);
                        return false;
                    }
                })
                .apply(myOptions)
                .into(image);
    }

    /**
     * Gets the dominant colour swatch of the image and sets the constraint layout and text views
     * with appropriate colours.
     *
     * @param resource bitmap of the podcast artwork
     * @param title textview of the podcast title
     * @param artist textview of the podcast artist
     * @param constraintLayout layout holding the artwork, title, and artist in bottom sheet
     */
    public void setBottomSheetColours(Bitmap resource, TextView title, TextView artist,
                                      ConstraintLayout constraintLayout) {
        if (resource != null) {
            int defaultColour = 0x000000;
            Palette palette = Palette.from(resource).generate();
            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
            if (dominantSwatch != null) {
                constraintLayout.setBackgroundColor(dominantSwatch.getRgb());
                title.setTextColor(dominantSwatch.getTitleTextColor());
                artist.setTextColor(dominantSwatch.getBodyTextColor());
            } else {
                constraintLayout.setBackgroundColor(defaultColour);
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
        super.onDetach();
    }

    public interface Listener {
        void onPodcastClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView episode;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.fragment_podcast_list_dialog_item, parent, false));
            episode = itemView.findViewById(R.id.episode);
            // maybe use this if i implement clicking episodes in this bottom sheet view
            episode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onPodcastClicked(getAdapterPosition());
                        dismiss();
                    }
                }
            });
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
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.episode.setText(episodes.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return episodes.size();
        }

    }
}
