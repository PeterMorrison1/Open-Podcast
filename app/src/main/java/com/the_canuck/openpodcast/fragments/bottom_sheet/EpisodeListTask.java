package com.the_canuck.openpodcast.fragments.bottom_sheet;

import android.os.AsyncTask;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.search.RssReader;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

//public class EpisodeListTask extends AsyncTask<Void, Void, List<Episode>> {
//
//    private BottomSheetContract.BottomSheetView bottomSheetView;
//    private int collectionId;
//    private String artist;
//    private MySQLiteHelper sqLiteHelper;
//    private RssReader reader;
//
//    public EpisodeListTask(BottomSheetContract.BottomSheetView bottomSheetView, int collectionId,
//                           String artist, MySQLiteHelper sqLiteHelper, RssReader reader) {
//        this.bottomSheetView = bottomSheetView;
//        this.collectionId = collectionId;
//        this.artist = artist;
//        this.sqLiteHelper = sqLiteHelper;
//        this.reader = reader;
//    }
//
//    public boolean isRunning() {
//        return getStatus() == Status.RUNNING;
//    }
//
//    @Override
//    protected List<Episode> doInBackground(Void... voids) {
//        List<Episode> episodes;
//        List<Episode> rssEpisodeList;
//        List<Episode> sqlEpisodeList;
//
//
//        reader.setCollectionId(collectionId);
//        reader.setCollectionArtist(artist);
//
////        rssEpisodeList = reader.createEpisodeList();
//        sqlEpisodeList = sqLiteHelper.getEpisodes(collectionId);
//
//        episodes = new ArrayList<>();
//
//        try {
//            if (!sqlEpisodeList.isEmpty()) {
//                // deletes episodes from RSS list if they have matching title with the SQLite episode
//                for (Episode episodeSqlite : sqlEpisodeList) {
//                    boolean found = false;
//                    int i = 0;
//                    while (i < rssEpisodeList.size() && !found) {
//                        if (rssEpisodeList.get(i).getTitle().equalsIgnoreCase(episodeSqlite.getTitle())) {
//                            rssEpisodeList.remove(i);
//                            found = true;
//                        } else {
//                            i++;
//                        }
//                    }
//                }
//                episodes.addAll(sqlEpisodeList);
//                episodes.addAll(rssEpisodeList);
//
//            } else {
//                episodes = rssEpisodeList;
//            }
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//        return episodes;
//    }
//
//    @Override
//    protected void onPostExecute(List<Episode> episodeList) {
//        super.onPostExecute(episodeList);
//
//        bottomSheetView.showLoadingIndicator(false);
//
//        bottomSheetView.setEpisodeList(episodeList);
//        bottomSheetView.setPodcastDescription(reader.getPodcastDescription());
//        bottomSheetView.populateBottomSheetViews();
//    }
//}
