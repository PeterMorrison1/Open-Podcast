package com.the_canuck.openpodcast.misc_helpers;

import com.the_canuck.openpodcast.Episode;

import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ListHelper {

    public static final int A_OLDERTHAN_B = -1;
    public static final int A_NEWERTHAN_B = 1;
    public static final int A_SAMEAS_B = 0;

    // Error is set as -5000 because im not sure if .compareTo() goes lower than -1 ¯\_(ツ)_/¯
    public static final int ERROR = -5000;

    /**
     * Returns the index the episode belongs in the list based on pubDate.
     *
     * @param pubDate the publish date of the episode being added into the list
     * @param episodeList list of episodes being added to
     * @return the index the episode should be placed in
     */
    public static int getSortedIndex(String pubDate, List<Episode> episodeList) {
        // Checks to see which episode was published first, adds episode to appropriate index
        // FIXME: Only returning the initial value of position. Maybe put directly in getEpisodes()
        // FIXME: Seems to work for what we want, fix the hack on a later date.
        int position = -1;
        Date currentEpisodeDate;
        DateFormat formatter = new SimpleDateFormat
                ("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        try {
            currentEpisodeDate = formatter.parse(pubDate);
            if (episodeList.isEmpty()) {
                position = 0;
                return position;
            } else if (!episodeList.isEmpty()){
                for (int i = 0; i < episodeList.size(); i++){
                    Date iterationDate = formatter.parse(episodeList.get(i).getPubDate());

                    if (currentEpisodeDate.compareTo(iterationDate) <= 0) {
                        position = i;
                        return position;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return position;
    }

    /**
     * Checks two episode dates with podcast/episode pubDate format for the newer of the two.
     *
     * @param dateA the String publish date of the first episode/podcast
     * @param dateB the String publish date of the second episode/podcast
     * @return the int of which is newer/older/same. A newerthan B = 1. A==B = 0. A olderthan B = -1
     */
    public static int determineNewerDate(String dateA, String dateB) {
        int position = ERROR;
        DateFormat formatter = new SimpleDateFormat
                ("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

        try {
            Date mDateA = formatter.parse(dateA);
            Date mDateB = formatter.parse(dateB);

            position = mDateA.compareTo(mDateB);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return position;
    }
}
