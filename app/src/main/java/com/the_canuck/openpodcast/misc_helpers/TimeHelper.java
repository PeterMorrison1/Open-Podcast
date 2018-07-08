package com.the_canuck.openpodcast.misc_helpers;

import java.util.concurrent.TimeUnit;

public class TimeHelper {

    /**
     * Converts HH:MM:SS or MM:SS to long seconds.
     *
     * @param duration the inputted HH:MM:SS or MM:SS time
     * @return the duration in seconds as a long
     */
    public static long convertDurationToSeconds(String duration) {
        String[] episodeDurationSplit = String.valueOf(duration).split(":");
        long totalSeconds = -1;

        if (episodeDurationSplit.length == 3) {
            totalSeconds = TimeUnit.HOURS.toSeconds(Long.valueOf(episodeDurationSplit[0]))
                    + TimeUnit.MINUTES.toSeconds(Long.valueOf(episodeDurationSplit[1]))
                    + Long.valueOf(episodeDurationSplit[2]);
        } else if (episodeDurationSplit.length == 2) {
            totalSeconds = TimeUnit.MINUTES.toSeconds(Long.valueOf(episodeDurationSplit[0]))
                    + Long.valueOf(episodeDurationSplit[1]);
        } else if (episodeDurationSplit.length == 1) {
            totalSeconds = Long.valueOf(episodeDurationSplit[0]);
        }

        return totalSeconds;
    }

    /**
     * Converts seconds into HH:MM:SS or MM:SS.
     *
     * @param time total seconds inputted
     * @return string with time in HH:MM:SS or MM:SS
     */
    public static String convertSecondsToHourMinSec(int time) {
        String duration;
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = (time % 3600) % 60;

        if (hours > 0) {
            duration = String.valueOf(hours) + ":" + String.valueOf(minutes) + ":"
                    + String.valueOf(seconds);
        } else {
            duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
        }
        return duration;
    }

}
