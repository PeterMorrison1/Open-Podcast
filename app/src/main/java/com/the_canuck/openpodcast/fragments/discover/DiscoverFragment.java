package com.the_canuck.openpodcast.fragments.discover;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

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

    private OnListFragmentInteractionListener mListener;

    // TODO: Delete these when you delete the huge amount of if statements below
    private List<RecyclerView> recyclerViews = null;
    List<Button> buttons;
    List<Integer> genres;


    private ProgressBar progressBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoverFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // this is the view of the constraint layout
        View view = inflater.inflate(R.layout.fragment_discover_constraint, container, false);

        RecyclerView.LayoutManager layoutManager;

        /* Set a progress bar while recyclerviews load. Might change this in the future since
        its hidden behind card views
         */
        progressBar = view.findViewById(R.id.discover_progress_bar);
        progressBar.setVisibility(View.GONE);

        // not sure if im going to use the decoration yet
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(view.getContext(),
                RecyclerView.VERTICAL);

        // Create recyclerViews list (empty) which will be filled by for loop below
        recyclerViews = new ArrayList<>();
        List<View> views = createViewList(view);

        // Create button list for every categories buttons
        buttons = createButtonList(view);

        // Create genre list to store genres for searching
        genres = createGenreList();

        /* Set layout manager and execute searchtask for each recyclerview in the list
           then adds the newly made recyclerview into the recyclerview list
          */
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

        return view;
    }

    /**
     * Create list of button views for each genre's "more" button.
     *
     * @param view layout view
     * @return list of every more button
     */
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

    /**
     * Create list of views for each recycler view in layout.
     *
     * @param view layout view
     * @return list of every recycler view
     */
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

    /**
     * Create a list of genre ids for each category.
     *
     * @return list of every genre id
     */
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
        // determines which "more" button is clicked, ends when clicked one is found
        while (!isFound && i < buttons.size()) {
            if (v.getId() == buttons.get(i).getId()) {
                genreId = Integer.toString(genres.get(i));
                isFound = true;
            }
            i++;
        }

        // Sends an intent to genreSearchIntent() in main activity to start search fragment
        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.putExtra("query", genreId);
        intent.putExtra("isGenre", true);
        ((MainActivity)getActivity()).genreSearchIntent(intent);

        /* This is the same as above except doesn't interact with main activity, starts search
        fragment from this fragment. Not sure which solution to use so I'm keeping it for now, but
        commented out.
         */
//        Bundle bundle = new Bundle();
//        bundle.putString("query", genreId);
//        bundle.putBoolean("isGenre", true);
//        SearchFragment searchFragment = new SearchFragment();
//        searchFragment.setArguments(bundle);
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container, searchFragment);
//        transaction.addToBackStack("search");
//        transaction.commit();
    }

    /**
     * Runs the SearchHelper and returns the podcast list and sets the recyclerview adapter.
     */
    private class SearchTask extends AsyncTask<Integer, Void, List<Podcast>> {
        @Override
        protected List<Podcast> doInBackground(Integer... integers) {
            SearchHelper searchHelper;
            SearchResultHelper resultHelper;
            List<Podcast> podcastList;

            searchHelper = new SearchHelper((String.valueOf(integers[0])), true);
            resultHelper = new SearchResultHelper();
            podcastList = resultHelper.populatePodcastList(searchHelper.runSearch());
            podcastList.get(0).setGroupingGenre(integers[0]);
            return podcastList;
        }

        @Override
        protected void onPostExecute(List<Podcast> podcasts) {
            int i = 0;
            boolean isFound = false;
            // Checks which genre is being set to an adapter and sets it
            while (i < genres.size() && !isFound) {
                if (podcasts.get(0).getGroupingGenre() == genres.get(i)) {
                    recyclerViews.get(i).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
                    isFound = true;
                }
                i++;
            }
            // TODO: Delete this huge switch statment later when im sure the above loop works.
//            switch (podcasts.get(0).getGroupingGenre()) {
//                case 1301:
//                    recyclerViews.get(0).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1321:
//                    recyclerViews.get(1).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1303:
//                    recyclerViews.get(2).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1304:
//                    recyclerViews.get(3).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1323:
//                    recyclerViews.get(4).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1325:
//                    recyclerViews.get(5).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1307:
//                    recyclerViews.get(6).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1305:
//                    recyclerViews.get(7).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1310:
//                    recyclerViews.get(8).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1311:
//                    recyclerViews.get(9).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1314:
//                    recyclerViews.get(10).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1315:
//                    recyclerViews.get(11).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1324:
//                    recyclerViews.get(12).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1316:
//                    recyclerViews.get(13).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1318:
//                    recyclerViews.get(14).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//
//                case 1309:
//                    recyclerViews.get(15).setAdapter(new MyDiscoverRecyclerViewAdapter(podcasts, mListener));
//                    break;
//            }
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
