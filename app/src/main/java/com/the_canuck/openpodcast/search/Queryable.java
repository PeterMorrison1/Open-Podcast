package com.the_canuck.openpodcast.search;

public enum Queryable {
    MEDIA("media"),
    TERM("term"),
    LANGUAGE("language"),
    COUNTRY("country");

    private final String value;
    private Queryable(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
