package com.the_canuck.openpodcast.misc_helpers;

import com.the_canuck.openpodcast.Episode;

import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class ListHelper {

    /**
     * Adds the passed episode into the passed list, in a sorted position based on publish date.
     *
     * @param mEpisode episode being added into the list
     * @param episodeList list of episodes being added to
     * @return the updated episode list
     */
    public static List<Episode> addToListSorted(Episode mEpisode, List<Episode> episodeList) {
        // Checks to see which episode was published first, adds episode to appropriate index
        Date currentEpisodeDate;
        DateFormat formatter = new SimpleDateFormat
                ("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        try {
            currentEpisodeDate = formatter.parse
                    (mEpisode.getPubDate());
            if (episodeList.isEmpty()) {
                episodeList.add(mEpisode);
            } else {
                for (int i = 0; i < episodeList.size(); i++) {
                    Date iterationDate = formatter.parse
                            (episodeList.get(i).getPubDate());

                    if (currentEpisodeDate.compareTo(iterationDate) <= 0) {
                        episodeList.add(i, mEpisode);
                        break;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return episodeList;
    }
}
