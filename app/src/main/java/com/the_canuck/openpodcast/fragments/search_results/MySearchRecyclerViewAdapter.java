package com.the_canuck.openpodcast.fragments.search_results;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.activities.MainActivity;
import com.the_canuck.openpodcast.fragments.search_results.SearchFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Podcast} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySearchRecyclerViewAdapter extends
        RecyclerView.Adapter<MySearchRecyclerViewAdapter.ViewHolder> {

//    private final List<DummyItem> mValues;
    private final List<Podcast> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySearchRecyclerViewAdapter(List<Podcast> items,
                                       OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mImageView.(Integer.toString(mValues.get(position).getCollectionId()));
        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_error_black_24dp)
                .override(250, 250);
        Glide.with(holder.mView.getContext())
                .load(mValues.get(position).getArtworkUrl600())
                .apply(myOptions)
                .into(holder.mImageView);

        holder.mTitleView.setText(mValues.get(position).getCollectionName());
        holder.mTitleView.setEllipsize(TextUtils.TruncateAt.END);
        holder.mTitleView.setMaxLines(1);
        holder.mAuthorView.setText(mValues.get(position).getArtistName());
        holder.mAuthorView.setEllipsize(TextUtils.TruncateAt.END);
        holder.mAuthorView.setMaxLines(1);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTitleView;
        public final TextView mAuthorView;
        public Podcast mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.pod_image);
            mTitleView = view.findViewById(R.id.pod_title);
            mAuthorView = view.findViewById(R.id.pod_author);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
