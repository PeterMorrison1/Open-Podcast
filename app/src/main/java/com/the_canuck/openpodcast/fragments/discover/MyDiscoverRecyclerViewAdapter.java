package com.the_canuck.openpodcast.fragments.discover;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.fragments.discover.DiscoverFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Podcast} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDiscoverRecyclerViewAdapter extends
        RecyclerView.Adapter<MyDiscoverRecyclerViewAdapter.ViewHolder> {

    private final List<Podcast> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDiscoverRecyclerViewAdapter(List<Podcast> items,
                                         OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_discover, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getCollectionName());
        holder.mContentView.setText(mValues.get(position).getArtistName());

        final RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_error_black_24dp)
                .override(600, 600);
        Glide.with(holder.mView.getContext())
                .asBitmap()
                .load(mValues.get(position).getArtworkUrl600())
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Glide.with(holder.mView.getContext()).load(holder.mItem.getArtworkUrl100()).apply(myOptions).into(holder.mImageView);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        setCardViewColours(resource, holder.mIdView, holder.mContentView, holder.mConstraintView);
                        return false;
                    }
                })
                .apply(myOptions)
                .into(holder.mImageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFragmentInteraction(holder.mItem);
                }
            }
        });
    }
    public void setCardViewColours(Bitmap resource, TextView title, TextView artist,
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
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView mImageView;
//        public final CardView mCardView;
        public final ConstraintLayout mConstraintView;
        public Podcast mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.discover_title);
            mContentView = view.findViewById(R.id.discover_artist);
            mImageView = view.findViewById(R.id.discover_image);
//            mCardView = view.findViewById(R.id.card_view);
            mConstraintView = view.findViewById(R.id.card_view_constraint);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
