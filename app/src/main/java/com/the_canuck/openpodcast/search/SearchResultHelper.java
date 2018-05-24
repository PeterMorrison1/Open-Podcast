package com.the_canuck.openpodcast.search;

import android.util.Log;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.search.enums.ItunesJsonKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultHelper {
    private Podcast podcast;
    private List<Podcast> podcastList = null;

    /**
     * Creates a list of podcast objects created from json response in holder.
     *
     * @return list of podcasts
     */
    public List<Podcast> populatePodcastList(String results) {
        try {
            Log.d("Skipping", "populate podcast list before start");
            List<Podcast> podcasts = new ArrayList<>();
            JSONObject response = new JSONObject(results);
            JSONArray jsonArray = response.getJSONArray("results");

            Podcast oldPodcast = null;
            int i = 0;
            while ((oldPodcast == null || oldPodcast != podcast) && i < jsonArray.length()) {
                JSONObject object = jsonArray.getJSONObject(i);
                boolean donePodcast = false;
                donePodcast = buildPodcast(object);
                while (!donePodcast) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (oldPodcast == null || oldPodcast != podcast) {
                    podcasts.add(podcast);
                }
                oldPodcast = podcast;
                i++;
                if (i >= jsonArray.length()) {
                    return podcasts;
                }
            }
            Log.d("Skipping", "populate podcast list after start");

            return podcasts;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a podcast object with the json result from itunes search.
     *
     * @param object the JSON object holding the itunes results
     * @return podcast object holding entered info.
     */
    private boolean buildPodcast(final JSONObject object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                Log.d("Skipping", "before buildPodcast()");

                Podcast newPodcast = new Podcast.PodcastBuilder()
                        .setCollectionName(object.getString(ItunesJsonKeys.COLLECTIONNAME.getValue()))
                        .setCensoredName(object.getString
                                (ItunesJsonKeys.COLLECTIONCENSOREDNAME.getValue()))
                        .setCollectionId(Integer.valueOf(object.getString
                                (ItunesJsonKeys.COLLECTIONID.getValue())))
                        .setArtistName(object.getString(ItunesJsonKeys.ARTISTNAME.getValue()))
                        .setTrackCount(Integer.valueOf(object.getString
                                (ItunesJsonKeys.TRACKCOUNT.getValue())))
                        .setArtworkUrl30(object.getString(ItunesJsonKeys.ARTWORKURL30.getValue()))
                        .setArtworkUrl60(object.getString(ItunesJsonKeys.ARTWORKURL60.getValue()))
                        .setArtworkUrl100(object.getString(ItunesJsonKeys.ARTWORKURL100.getValue()))
                        .setArtworkUrl600(object.getString(ItunesJsonKeys.ARTWORKURL600.getValue()))
                        .build();

//                Log.d("Skipping", "after buildPodcast(): " + newPodcast);
                setPodcast(newPodcast);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    public List<Podcast> buildPodcastList(final String results) {
        new Thread(new Runnable() {
            String mResults = results;
            List<Podcast> podcasts = new ArrayList<>();
            @Override
            public void run() {
                try {
                    Log.d("Skipping", "populate podcast list before start");
                    JSONObject response = new JSONObject(mResults);
                    JSONArray jsonArray = response.getJSONArray("results");

                    Log.d("Skipping", "before buildPodcast()");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        Podcast newPodcast = new Podcast.PodcastBuilder()
                                .setCollectionName(object.getString(ItunesJsonKeys.COLLECTIONNAME.getValue()))
                                .setCensoredName(object.getString
                                        (ItunesJsonKeys.COLLECTIONCENSOREDNAME.getValue()))
                                .setCollectionId(Integer.valueOf(object.getString
                                        (ItunesJsonKeys.COLLECTIONID.getValue())))
                                .setArtistName(object.getString(ItunesJsonKeys.ARTISTNAME.getValue()))
                                .setTrackCount(Integer.valueOf(object.getString
                                        (ItunesJsonKeys.TRACKCOUNT.getValue())))
                                .setArtworkUrl30(object.getString(ItunesJsonKeys.ARTWORKURL30.getValue()))
                                .setArtworkUrl60(object.getString(ItunesJsonKeys.ARTWORKURL60.getValue()))
                                .setArtworkUrl100(object.getString(ItunesJsonKeys.ARTWORKURL100.getValue()))
                                .setArtworkUrl600(object.getString(ItunesJsonKeys.ARTWORKURL600.getValue()))
                                .build();

//                        Log.d("Skipping", "after buildPodcast(): " + newPodcast);
                        podcasts.add(newPodcast);
                        setPodcastList(podcasts);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        while (getPodcastList() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return getPodcastList();
    }

    public List<Podcast> getPodcastList() {
        return podcastList;
    }

    public SearchResultHelper setPodcastList(List<Podcast> podcastList) {
        this.podcastList = podcastList;
        return this;
    }

    public Podcast getPodcast() {
        return podcast;
    }

    public SearchResultHelper setPodcast(Podcast podcast) {
        this.podcast = podcast;
        return this;
    }
}
