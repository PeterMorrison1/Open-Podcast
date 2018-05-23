package com.the_canuck.openpodcast.search.enums;

public enum GenreIds {
    ARTS(1301),
    BUSINESS(1321),
    COMEDY(1303),
    EDUCATION(1304),
    GAMESANDHOBBIES(1323),
    GOVERNMENTANDORGANIZATIONS(1325),
    HEALTH(1307),
    KIDSANDFAMILY(1305),
    MUSIC(1310),
    NEWSANDPOLITICS(1311),
    RELIGIONANDSPIRITUALITY(1314),
    SCIENCEANDMEDICINE(1315),
    SOCIETYANDCULTURE(1324),
    SPORTSANDRECREATION(1316),
    TECHNOLOGY(1318),
    TVANDFILM(1309);


    private final int value;
    GenreIds(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
