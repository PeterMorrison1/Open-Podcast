package com.the_canuck.openpodcast.fragments.discover;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.fragments.discover.dummy.DummyContent;
import com.the_canuck.openpodcast.fragments.discover.dummy.DummyContent.DummyItem;
import com.the_canuck.openpodcast.search.SearchHelper;
import com.the_canuck.openpodcast.search.SearchResultHelper;
import com.the_canuck.openpodcast.search.enums.GenreIds;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DiscoverFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoverFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DiscoverFragment newInstance(int columnCount) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // this is the view of the constraint layout
        View view = inflater.inflate(R.layout.fragment_discover_constraint, container, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
                (view.getContext(), LinearLayoutManager.HORIZONTAL, false);

        // not sure if im going to use the decoration yet
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(view.getContext(),
                RecyclerView.VERTICAL);

        // Set the first recycler view and its adapter
        if (view.findViewById(R.id.discover_list) instanceof RecyclerView) {
            final int GENRE_ARTS = 1;
            RecyclerView recyclerView = view.findViewById(R.id.discover_list);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(searchPodcastGenre(GENRE_ARTS), mListener));
        }
        return view;
    }

    public List<Podcast> searchPodcastGenre(int genre) {
        SearchHelper searchHelper;
        List<Podcast> podcastList = null;
        switch (genre) {
            case 1:
                searchHelper = new SearchHelper(String.valueOf(GenreIds.ARTS.getValue()), true);
                searchHelper.runSearch();
                podcastList = SearchResultHelper.populatePodcastList(searchHelper.getHolder().getResults());

        }
        return podcastList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Podcast item);
    }
}
