package com.the_canuck.openpodcast.data.discover_list;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.PodcastJsonListHolder;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DiscoverLocalApiImpl implements DiscoverLocalApi {
    // Made a local one incase I decide to connect to a server for updating list in the future

    public Gson gson;
    public Context context;

    @Inject
    public DiscoverLocalApiImpl(Gson gson, Context context) {
        this.gson = gson;
        this.context = context;
    }

    @Override
    public void parsePodcastList(int genre, PodcastListLoadedCallback callback) {
        try {
            InputStream raw = getInputStream(genre);
            byte[] bytes = new byte[raw.available()];
            raw.read(bytes, 0, bytes.length);
            String json = new String(bytes);

            PodcastJsonListHolder podcastJsonListHolder = gson.fromJson(json, PodcastJsonListHolder.class);
            List<PodcastJsonListHolder.PodcastJsonHolder> podcastJsonHolders = podcastJsonListHolder.getPodcastJsonHolderList();

            List<Podcast> podcastList = new ArrayList<>();

            for (PodcastJsonListHolder.PodcastJsonHolder item : podcastJsonHolders) {
                Podcast podcast = item.createPodcast();
                podcastList.add(podcast);
            }

            callback.onPodcastsLoaded(podcastList);
        } catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException e ) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to read discover podcast list", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Determines the raw resource file to get podcasts from based on int genre/category.
     *
     * @param genre the genre/category which is the name of the file
     * @return the InputStream of the raw resource file.
     */
    private InputStream getInputStream(int genre) {
        // Currently returns same file for testing purposes, switch statement will be here
        // when all the files are made
        final int ARTS = 1301;
        final int BUSINESS = 1321;
        final int COMEDY = 1303;
        final int EDUCATION = 1304;
        final int GAMES_AND_HOBBIES = 1323;
        final int GOVERNMENT_AND_ORGANIZATION = 1325;
        final int HEALTH = 1307;
        final int KIDS_AND_FAMILY = 1305;
        final int MUSIC = 1310;
        final int NEWS_AND_POLITICS = 1311;
        final int RELIGION_AND_SPIRITUALITY = 1314;
        final int SCIENCE_AND_MEDICINE = 1315;
        final int SOCIETY_AND_CULTURE = 1324;
        final int SPORTS_AND_RECREATION = 1316;
        final int TECHNOLOGY = 1318;
        final int TV_AND_FILM = 1309;

        InputStream inputStream;

        switch (genre) {
            case ARTS:
                inputStream = context.getResources().openRawResource(R.raw.arts);
                break;
            case BUSINESS:
                inputStream = context.getResources().openRawResource(R.raw.business);
                break;
            case COMEDY:
                inputStream = context.getResources().openRawResource(R.raw.comedy);
                break;
            case EDUCATION:
                inputStream = context.getResources().openRawResource(R.raw.education);
                break;
            case GAMES_AND_HOBBIES:
                inputStream = context.getResources().openRawResource(R.raw.games_and_hobbies);
                break;
            case GOVERNMENT_AND_ORGANIZATION:
                inputStream = context.getResources().openRawResource(R.raw.government_and_organizations);
                break;
            case HEALTH:
                inputStream = context.getResources().openRawResource(R.raw.health);
                break;
            case KIDS_AND_FAMILY:
                inputStream = context.getResources().openRawResource(R.raw.kids_and_family);
                break;
            case MUSIC:
                inputStream = context.getResources().openRawResource(R.raw.music);
                break;
            case NEWS_AND_POLITICS:
                inputStream = context.getResources().openRawResource(R.raw.news_and_politics);
                break;
            case RELIGION_AND_SPIRITUALITY:
                inputStream = context.getResources().openRawResource(R.raw.religion_and_spirituality);
                break;
            case SCIENCE_AND_MEDICINE:
                inputStream = context.getResources().openRawResource(R.raw.science_and_medicine);
                break;
            case SOCIETY_AND_CULTURE:
                inputStream = context.getResources().openRawResource(R.raw.society_and_culture);
                break;
            case SPORTS_AND_RECREATION:
                inputStream = context.getResources().openRawResource(R.raw.sports_and_recreation);
                break;
            case TECHNOLOGY:
                inputStream = context.getResources().openRawResource(R.raw.technology);
                break;
            case TV_AND_FILM:
                inputStream = context.getResources().openRawResource(R.raw.tv_and_film);
                break;
            default:
                inputStream = null;
                break;
        }

        return inputStream;
    }
}
