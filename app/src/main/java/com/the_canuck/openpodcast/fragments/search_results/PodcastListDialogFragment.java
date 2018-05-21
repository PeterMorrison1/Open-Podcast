package com.the_canuck.openpodcast.fragments.search_results;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.search.enums.ItunesJsonKeys;

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

    // TODO: Customize parameters
    public static PodcastListDialogFragment newInstance(int collectionId, String artistName,
                                                 String artwork600, String artwork100,
                                                 String collectionName, String censoredName,
                                                 int trackCount) {
        PodcastListDialogFragment.collectionId = collectionId;
        PodcastListDialogFragment.artistName = artistName;
        PodcastListDialogFragment.artwork600 = artwork600;
        PodcastListDialogFragment.artwork100 = artwork100;
        PodcastListDialogFragment.collectionName = collectionName;
        PodcastListDialogFragment.censoredName = censoredName;
        PodcastListDialogFragment.trackCount = trackCount;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.bottom_sheet_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new PodcastAdapter(30));

        TextView title = view.findViewById(R.id.bottom_sheet_title);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(1);

        TextView artist = view.findViewById(R.id.bottom_sheet_artist);
        artist.setEllipsize(TextUtils.TruncateAt.END);
        artist.setMaxLines(1);

        ImageView image = view.findViewById(R.id.bottom_sheet_image);
        TextView description = view.findViewById(R.id.bottom_sheet_description);

        title.setText(collectionName);
        artist.setText(artistName);
        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_error_black_24dp)
                .override(600, 600);
        Glide.with(view.getContext())
                .load(artwork600)
                .apply(myOptions)
                .into(image);
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

    public int getTrackCount() {
        return trackCount;
    }

    public PodcastListDialogFragment setTrackCount(int trackCount) {
        PodcastListDialogFragment.trackCount = trackCount;
        return this;
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

        private final int mItemCount;

        PodcastAdapter(int itemCount) {
            mItemCount = itemCount;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.episode.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return mItemCount;
        }

    }

}
