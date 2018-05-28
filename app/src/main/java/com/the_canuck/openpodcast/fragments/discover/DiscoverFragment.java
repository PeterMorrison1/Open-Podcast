package com.the_canuck.openpodcast.fragments.discover;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.activities.MainActivity;
import com.the_canuck.openpodcast.fragments.search_results.SearchFragment;
import com.the_canuck.openpodcast.search.SearchHelper;
import com.the_canuck.openpodcast.search.SearchResultHelper;
import com.the_canuck.openpodcast.search.enums.GenreIds;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DiscoverFragment extends Fragment implements View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    // TODO: Delete these when you delete the huge amount of if statements below
//    private RecyclerView artRecyclerView;
//    private RecyclerView businessRecyclerView = null;
//    private RecyclerView comedyRecyclerView = null;
//    private RecyclerView educationRecyclerView = null;
//    private RecyclerView gamesRecyclerView = null;
//    private RecyclerView govRecyclerView = null;
//    private RecyclerView healthRecyclerView = null;
//    private RecyclerView familyRecyclerView = null;
//    private RecyclerView musicRecyclerView = null;
//    private RecyclerView newsRecyclerView = null;
//    private RecyclerView religionRecyclerView = null;
//    private RecyclerView scienceRecyclerView = null;
//    private RecyclerView societyRecyclerView = null;
//    private RecyclerView sportsRecyclerView = null;
//    private RecyclerView technologyRecyclerView = null;
//    private RecyclerView tvRecyclerView = null;
    private List<RecyclerView> recyclerViews = new ArrayList<>();
    List<Button> buttons;
    List<Integer> genres;


    private ProgressBar progressBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoverFragment() {
    }

//    // TODO: Customize parameter initialization
//    @SuppressWarnings("unused")
//    public static DiscoverFragment newInstance(int columnCount) {
//        DiscoverFragment fragment = new DiscoverFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
//        return fragment;
//    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
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
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
//                (view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManager = null;

        progressBar = view.findViewById(R.id.discover_progress_bar);
        progressBar.setVisibility(View.GONE);

        // not sure if im going to use the decoration yet
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(view.getContext(),
                RecyclerView.VERTICAL);



        List<View> views = createViewList(view);

        buttons = createButtonList(view);
        genres = createGenreList();

        // set layout manager and execute searchtask for each recyclerview in the list
        for (int i = 0; i < views.size(); i++) {
            if (view.findViewById(views.get(i).getId()) instanceof RecyclerView) {
                layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);

                RecyclerView recyclerView = view.findViewById(views.get(i).getId());

                recyclerView.setLayoutManager(layoutManager);
//
                recyclerViews.add(recyclerView);

                new SearchTask().execute(genres.get(i));
            }
        }

        // set onclicklistener for each button in list
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setOnClickListener(this);
        }

        // TODO: Delete this huge collection of "if" statements below if above loop for sure works

//
//        // Art Recycler view
//        if (view.findViewById(R.id.discover_arts_recycler) instanceof RecyclerView) {
//            layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//
//            artRecyclerView = view.findViewById(R.id.discover_arts_recycler);
//            artRecyclerView.setLayoutManager(layoutManager);
////            recyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(searchPodcastGenre(GENRE_ARTS), mListener));
//
//            progressBar.setVisibility(View.VISIBLE);
//            new SearchTask().execute(GenreIds.ARTS.getValue());
//        }
//
//        // Business Recycler view
//        if (view.findViewById(R.id.discover_business_recycler) instanceof RecyclerView) {
//            layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//
//            businessRecyclerView = view.findViewById(R.id.discover_business_recycler);
//            businessRecyclerView.setLayoutManager(layoutManager);
//            new SearchTask().execute(GenreIds.BUSINESS.getValue());
//        }
//
//        // Comedy Recycler view
//        if (view.findViewById(R.id.discover_comedy_recycler) instanceof RecyclerView) {
//            layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//
//            comedyRecyclerView = view.findViewById(R.id.discover_comedy_recycler);
//            comedyRecyclerView.setLayoutManager(layoutManager);
//            new SearchTask().execute(GenreIds.COMEDY.getValue());
//        }
//
//        // Education Recycler view
//        if (view.findViewById(R.id.discover_education_recycler) instanceof RecyclerView) {
//            layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//
//            educationRecyclerView = view.findViewById(R.id.discover_education_recycler);
//            educationRecyclerView.setLayoutManager(layoutManager);
//            new SearchTask().execute(GenreIds.EDUCATION.getValue());
//        }
//
//        // Games and Hobbies Recycler view
//        if (view.findViewById(R.id.discover_hobbies_recycler) instanceof RecyclerView) {
//            layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//
//            gamesRecyclerView = view.findViewById(R.id.discover_hobbies_recycler);
//            gamesRecyclerView.setLayoutManager(layoutManager);
//            new SearchTask().execute(GenreIds.GAMES_AND_HOBBIES.getValue());
//        }
//
//        // Government Recycler view
//        if (view.findViewById(R.id.discover_gov_recycler) instanceof RecyclerView) {
//            layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//
//            govRecyclerView = view.findViewById(R.id.discover_gov_recycler);
//            govRecyclerView.setLayoutManager(layoutManager);
//            new SearchTask().execute(GenreIds.GOVERNMENT_AND_ORGANIZATIONS.getValue());
//        }
//
//        // Health Recycler view
//        if (view.findViewById(R.id.discover_health_recycler) instanceof RecyclerView) {
//            layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
//
//            healthRecyclerView = view.findViewById(R.id.discover_health_recycler);
//            healthRecyclerView.setLayoutManager(layoutManager);
//            new SearchTask().execute(GenreIds.HEALTH.getValue());
//        }
        return view;
    }

    public List<Button> createButtonList(View view) {
        // Create list of views used in genre screen
        List<Button> views = new ArrayList<>();
        views.add((Button) view.findViewById(R.id.arts_button));
        views.add((Button) view.findViewById(R.id.business_button));
        views.add((Button) view.findViewById(R.id.comedy_button));
        views.add((Button) view.findViewById(R.id.education_button));
        views.add((Button) view.findViewById(R.id.hobbies_button));
        views.add((Button) view.findViewById(R.id.gov_button));
        views.add((Button) view.findViewById(R.id.health_button));
        views.add((Button) view.findViewById(R.id.family_button));
        views.add((Button) view.findViewById(R.id.music_button));
        views.add((Button) view.findViewById(R.id.politics_button));
        views.add((Button) view.findViewById(R.id.religion_button));
        views.add((Button) view.findViewById(R.id.science_button));
        views.add((Button) view.findViewById(R.id.society_button));
        views.add((Button) view.findViewById(R.id.sports_button));
        views.add((Button) view.findViewById(R.id.technology_button));
        views.add((Button) view.findViewById(R.id.film_button));

        return views;
    }

    public List<View> createViewList(View view) {
        // Create list of views used in genre screen
        List<View> views = new ArrayList<>();
        views.add(view.findViewById(R.id.discover_arts_recycler));
        views.add(view.findViewById(R.id.discover_business_recycler));
        views.add(view.findViewById(R.id.discover_comedy_recycler));
        views.add(view.findViewById(R.id.discover_education_recycler));
        views.add(view.findViewById(R.id.discover_hobbies_recycler));
        views.add(view.findViewById(R.id.discover_gov_recycler));
        views.add(view.findViewById(R.id.discover_health_recycler));
        views.add(view.findViewById(R.id.discover_family_recycler));
        views.add(view.findViewById(R.id.discover_music_recycler));
        views.add(view.findViewById(R.id.discover_politics_recycler));
        views.add(view.findViewById(R.id.discover_religion_recycler));
        views.add(view.findViewById(R.id.discover_science_recycler));
        views.add(view.findViewById(R.id.discover_society_recycler));
        views.add(view.findViewById(R.id.discover_sports_recycler));
        views.add(view.findViewById(R.id.discover_technology_recycler));
        views.add(view.findViewById(R.id.discover_film_recycler));

        return views;
    }

    public List<Integer> createGenreList() {
        // create list of genre values used
        List<Integer> genres = new ArrayList<>();
        genres.add(GenreIds.ARTS.getValue());
        genres.add(GenreIds.BUSINESS.getValue());
        genres.add(GenreIds.COMEDY.getValue());
        genres.add(GenreIds.EDUCATION.getValue());
        genres.add(GenreIds.GAMES_AND_HOBBIES.getValue());
        genres.add(GenreIds.GOVERNMENT_AND_ORGANIZATIONS.getValue());
        genres.add(GenreIds.HEALTH.getValue());
        genres.add(GenreIds.KIDS_AND_FAMILY.getValue());
        genres.add(GenreIds.MUSIC.getValue());
        genres.add(GenreIds.NEWS_AND_POLITICS.getValue());
        genres.add(GenreIds.RELIGION_AND_SPIRITUALITY.getValue());
        genres.add(GenreIds.SCIENCE_AND_MEDICINE.getValue());
        genres.add(GenreIds.SOCIETY_AND_CULTURE.getValue());
        genres.add(GenreIds.SPORTS_AND_RECREATION.getValue());
        genres.add(GenreIds.TECHNOLOGY.getValue());
        genres.add(GenreIds.TV_AND_FILM.getValue());

        return genres;
    }

    @Override
    public void onClick(View v) {
        String genreId = null;

        int i = 0;
        boolean isFound = false;
        while (!isFound && i < buttons.size()) {
            if (v.getId() == buttons.get(i).getId()) {
                genreId = Integer.toString(genres.get(i));
                isFound = true;
            }
            i++;
        }

        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.putExtra("query", genreId);
        intent.putExtra("isGenre", true);
        SearchFragment searchFragment = new SearchFragment();
        ((MainActivity)getActivity()).replaceFragment(searchFragment, "search");
//        ((MainActivity)getActivity()).genreSearchIntent(intent);

//        Bundle bundle = new Bundle();
//        bundle.putString("query", genreId);
//        bundle.putBoolean("isGenre", true);
//        SearchFragment searchFragment = new SearchFragment();
//        searchFragment.setArguments(bundle);
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, searchFragment);
//        transaction.addToBackStack("search");
//        transaction.commit();


        // maybe replace this switch statement with a loop, if its not too inefficient
//        switch (v.getId()) {
//            case R.id.arts_button:
//                genreId = Integer.toString(GenreIds.ARTS.getValue());
//                break;
//
//            case R.id.business_button:
//                genreId = Integer.toString(GenreIds.BUSINESS.getValue());
//                break;
//
//            case R.id.comedy_button:
//                genreId = Integer.toString(GenreIds.COMEDY.getValue());
//                break;
//
//            case R.id.education_button:
//                genreId = Integer.toString(GenreIds.EDUCATION.getValue());
//                break;
//
//            case R.id.hobbies_button:
//                genreId = Integer.toString(GenreIds.GAMES_AND_HOBBIES.getValue());
//                break;
//
//            case R.id.gov_button:
//                genreId = Integer.toString(GenreIds.GOVERNMENT_AND_ORGANIZATIONS.getValue());
//                break;
//
//            case R.id.health_button:
//                genreId = Integer.toString(GenreIds.HEALTH.getValue());
//                break;
//
//            case R.id.family_button:
//                genreId = Integer.toString(GenreIds.KIDS_AND_FAMILY.getValue());
//                break;
//
//            case R.id.music_button:
//                genreId = Integer.toString(GenreIds.MUSIC.getValue());
//                break;
//
//            case R.id.politics_button:
//                genreId = Integer.toString(GenreIds.NEWS_AND_POLITICS.getValue());
//                break;
//
//            case R.id.religion_button:
//                genreId = Integer.toString(GenreIds.RELIGION_AND_SPIRITUALITY.getValue());
//                break;
//
//            case R.id.science_button:
//                genreId = Integer.toString(GenreIds.SCIENCE_AND_MEDICINE.getValue());
//                break;
//
//            case R.id.society_button:
//                genreId = Integer.toString(GenreIds.SOCIETY_AND_CULTURE.getValue());
//                break;
    }

    /**
     * Runs the SearchHelper and returns the podcast list and sets the recyclerview adapter.
     */
    private class SearchTask extends AsyncTask<Integer, Void, List<Podcast>> {
        @Override
        protected List<Podcast> doInBackground(Integer... integers) {
            SearchHelper searchHelper;
            SearchResultHelper resultHelper = null;
            List<Podcast> podcastList = null;

            searchHelper = new SearchHelper((String.valueOf(integers[0])), true);
            resultHelper = new SearchResultHelper();
            podcastList = resultHelper.populatePodcastList(searchHelper.runSearch());
            podcastList.get(0).setGroupingGenre(integers[0]);
            return podcastList;
        }

        @Override
        protected void onPostExecute(List<Podcast> podcasts) {
            switch (podcasts.get(0).getGroupingGenre()) {
                case 1301:
                    recyclerViews.get(0).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    // TODO: delete this commented out line when you delete above huge if statement
//                    artRecyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1321:
                    recyclerViews.get(1).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    businessRecyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1303:
                    recyclerViews.get(2).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    comedyRecyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1304:
                    recyclerViews.get(3).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    educationRecyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1323:
                    recyclerViews.get(4).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    gamesRecyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1325:
                    recyclerViews.get(5).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    govRecyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1307:
                    recyclerViews.get(6).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    healthRecyclerView.setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1305:
                    recyclerViews.get(7).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1310:
                    recyclerViews.get(8).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1311:
                    recyclerViews.get(9).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1314:
                    recyclerViews.get(10).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1315:
                    recyclerViews.get(11).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1324:
                    recyclerViews.get(12).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1316:
                    recyclerViews.get(13).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1318:
                    recyclerViews.get(14).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;

                case 1309:
                    recyclerViews.get(15).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    break;
            }
            progressBar.setVisibility(View.GONE);
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
