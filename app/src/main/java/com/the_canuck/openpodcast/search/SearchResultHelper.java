package com.the_canuck.openpodcast.search;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.search.enums.ItunesJsonKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultHelper {

    /**
     * Creates a list of podcast objects created from json response in holder.
     *
     * @return list of podcasts
     */
    public List<Podcast> populatePodcastList(String results) {
        try {
            List<Podcast> podcasts = new ArrayList<>();
            JSONObject response = new JSONObject(results);
            JSONArray jsonArray = response.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                podcasts.add(buildPodcast(object));
            }
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
    private Podcast buildPodcast(final JSONObject object) {
        Podcast newPodcast = null;
        try {
            newPodcast = new Podcast.PodcastBuilder()
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
                .setFeedUrl(object.getString(ItunesJsonKeys.FEEDURL.getValue()))
                .build();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newPodcast;
    }
}
