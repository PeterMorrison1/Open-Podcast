package com.the_canuck.openpodcast.fragments.library;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.activities.MainActivity;
import com.the_canuck.openpodcast.application.PodcastApplication;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;
import com.the_canuck.openpodcast.fragments.FragmentComponent;
import com.the_canuck.openpodcast.misc_helpers.ImageHelper;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.List;

import javax.inject.Inject;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LibraryFragment extends Fragment implements LibraryContract.LibraryView {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 3;

    private OnListFragmentInteractionListener mListener;
//    private MySQLiteHelper sqLiteHelper;
    private List<Podcast> podcastList;
    private LibraryContract.LibraryPresenter mLibPresenter;

    @Inject
    public PodcastRepository podcastRepository;

    @Inject
    Context context;
    private FragmentComponent component;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LibraryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LibraryFragment newInstance(int columnCount) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = PodcastApplication.get().plusFragmentComponent(this);
        component.inject(this);

        mLibPresenter = new LibraryPresenter(this, podcastRepository);


//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLibPresenter.updateSubscribedPodcasts();
    }

    @Override
    public void showLoadingIndicator(boolean active) {
        // TODO: Put a loading view in later
    }

    @Override
    public void showSubscribedPodcasts(List<Podcast> podcasts) {
        podcastList = podcasts;
    }

    @Override
    public void populatePodcastViews() {
        if (getActivity() != null) {
            mColumnCount = ImageHelper.calculateNoOfColumns(context);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO: Set column count as a filter option and change here
                    recyclerView = getView().findViewById(R.id.list);
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

                    recyclerView.setAdapter(new MyLibraryRecyclerViewAdapter
                            (podcastList, mListener, recyclerView));
                }
            });
        }
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
        PodcastApplication.get().clearFragmentComponent();
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
        void onListFragmentInteractionLibrary(Podcast item);
    }
}
