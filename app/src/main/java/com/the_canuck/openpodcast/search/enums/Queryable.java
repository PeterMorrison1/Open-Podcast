package com.the_canuck.openpodcast.search.enums;

public enum Queryable {
    MEDIA("media"),
    TERM("term"),
    LANGUAGE("language"),
    COUNTRY("country"),
    GENREID("genreId");

    private final String value;
    Queryable(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
