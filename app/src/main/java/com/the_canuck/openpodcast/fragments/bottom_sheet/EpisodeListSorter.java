package com.the_canuck.openpodcast.fragments.bottom_sheet;

import com.the_canuck.openpodcast.Episode;

import java.util.ArrayList;
import java.util.List;

public class EpisodeListSorter {
    public static List<Episode> sortTwoEpisodeLists(List<Episode> rssEpisodeList,
                                                    List<Episode> sqlEpisodeList) {
        List<Episode> episodes;

        episodes = new ArrayList<>();

        try {
            if (!sqlEpisodeList.isEmpty()) {
                // deletes episodes from RSS list if they have matching title with the SQLite episode
                for (Episode episodeSqlite : sqlEpisodeList) {
                    boolean found = false;
                    int i = 0;
                    while (i < rssEpisodeList.size() && !found) {
                        if (rssEpisodeList.get(i).getTitle().equalsIgnoreCase(episodeSqlite.getTitle())) {
                            rssEpisodeList.remove(i);
                            found = true;
                        } else {
                            i++;
                        }
                    }
                }
                episodes.addAll(sqlEpisodeList);
                episodes.addAll(rssEpisodeList);

            } else {
                episodes = rssEpisodeList;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return episodes;
    }

}
