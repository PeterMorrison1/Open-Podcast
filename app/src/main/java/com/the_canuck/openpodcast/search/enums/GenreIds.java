package com.the_canuck.openpodcast.search.enums;

public enum GenreIds {
    ARTS(1301),
    BUSINESS(1321),
    COMEDY(1303),
    EDUCATION(1304),
    GAMES_AND_HOBBIES(1323),
    GOVERNMENT_AND_ORGANIZATIONS(1325),
    HEALTH(1307),
    KIDS_AND_FAMILY(1305),
    MUSIC(1310),
    NEWS_AND_POLITICS(1311),
    RELIGION_AND_SPIRITUALITY(1314),
    SCIENCE_AND_MEDICINE(1315),
    SOCIETY_AND_CULTURE(1324),
    SPORTS_AND_RECREATION(1316),
    TECHNOLOGY(1318),
    TV_AND_FILM(1309);


    private final int value;
    private static final int SIZE = 16;
    GenreIds(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public static int getSize() {
        return SIZE;
    }
}
