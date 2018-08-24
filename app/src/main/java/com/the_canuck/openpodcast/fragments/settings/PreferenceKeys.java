package com.the_canuck.openpodcast.fragments.settings;

/**
 * Stores keys for preference files (file names) and keys for data pairs (key + value).
 */
public class PreferenceKeys {
    // TODO: Consider moving this into keys.xml or keep this for non SharedDefaultPrefs

    // Preference file keys
    public static String PREF_DOWNLOADS = "pref_downloads";

    // Preference individual data keys

    // if there are files in pref_downloads waiting to be updated in database
    public static String IS_FINISHED_DOWNLOADS = "is_finished_downloads";
}
