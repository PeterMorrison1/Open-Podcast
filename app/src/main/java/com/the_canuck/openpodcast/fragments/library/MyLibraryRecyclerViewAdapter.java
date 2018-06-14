package com.the_canuck.openpodcast.fragments.library;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.the_canuck.openpodcast.fragments.library.LibraryFragment.OnListFragmentInteractionListener;
import com.the_canuck.openpodcast.fragments.library.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyLibraryRecyclerViewAdapter extends RecyclerView.Adapter<MyLibraryRecyclerViewAdapter.ViewHolder> {

//    private final List<DummyItem> mValues;
    private final List<Podcast> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyLibraryRecyclerViewAdapter(List<Podcast> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_library_constraint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

//        holder.mIdView.setText(mValues.get(position).getCollectionName());
//        holder.mIdView.setEllipsize(TextUtils.TruncateAt.END);
//        holder.mIdView.setMaxLines(1);
//
//        holder.mContentView.setText("test");

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
                .override(400);
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
                    mListener.onListFragmentInteractionLibrary(holder.mItem);
                }
            }
        });
    }

    public boolean getConnectivityStatus(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
//        public final TextView mContentView;
        public final ImageView mImageView;
        public Podcast mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.library_title);
//            mContentView = view.findViewById(R.id.content);
            mImageView = view.findViewById(R.id.library_podcast_image);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
