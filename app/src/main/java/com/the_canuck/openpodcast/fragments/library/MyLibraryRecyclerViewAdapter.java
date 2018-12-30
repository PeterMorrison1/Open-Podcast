package com.the_canuck.openpodcast.fragments.library;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.fragments.library.LibraryFragment.OnListFragmentInteractionListener;
import com.the_canuck.openpodcast.misc_helpers.ImageHelper;

import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Podcast} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyLibraryRecyclerViewAdapter extends
        RecyclerView.Adapter<MyLibraryRecyclerViewAdapter.ViewHolder> {

    private final List<Podcast> mValues;
    private final OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private Map<String, Integer> imageMap;

    public MyLibraryRecyclerViewAdapter(List<Podcast> items,
                                        OnListFragmentInteractionListener listener,
                                        RecyclerView recyclerView) {
        mValues = items;
        mListener = listener;
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_library_constraint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // When attached determine image size. Not in bindViewHolder since it is called too often
        imageMap = ImageHelper.calculateImageSizes(recyclerView.getContext());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);

        if (!getConnectivityStatus(holder.mView.getContext())) {
            holder.mTitleView.setText(mValues.get(position).getCollectionName());
            holder.mTitleView.setEllipsize(TextUtils.TruncateAt.END);
            holder.mTitleView.setMaxLines(1);
            holder.mTitleView.setVisibility(View.VISIBLE);
        }

        /*set options for glide
        the placeholder/error image is incase no image loads and so the text if offline is visible*/
        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_glide_placeholder_library_24dp)
                .error(R.drawable.ic_glide_placeholder_library_24dp)
                .override(imageMap.get(ImageHelper.IMAGE_SIZE_MAP));
        Glide.with(holder.mView.getContext())
                .load(mValues.get(position).getArtworkUrl600())
                .apply(myOptions)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    holder.mItem.setRecyclerView(recyclerView);
                    holder.mItem.setPosition(holder.getAdapterPosition());
                    mListener.onListFragmentInteractionLibrary(holder.mItem);
                }
            }
        });
    }

    /**
     * Checks if the device is connected to the internet or not and returns true if so.
     *
     * @param context app context
     * @return boolean of connectivity status
     */
    private boolean getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Removes the podcast item at the adapter position.
     *
     * @param position current adapter position in recyclerview
     */
    public void removePodcastAt(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mValues.size());
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        } else {
            // TODO: If episodes is null show an error message or attempt to reopen this
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final ImageView mImageView;
        public Podcast mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.library_title);
            mImageView = view.findViewById(R.id.library_podcast_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
