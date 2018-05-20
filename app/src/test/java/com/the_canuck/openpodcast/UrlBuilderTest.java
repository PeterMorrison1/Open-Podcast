package com.the_canuck.openpodcast;

import com.the_canuck.openpodcast.search.UrlBuilder;

import static org.junit.Assert.*;

import org.junit.Test;

public class UrlBuilderTest {

    @Test
    public void testEncodeQueryTerms() {
        UrlBuilder urlBuilder = new UrlBuilder();

        String actual = urlBuilder.encodeQueryTerms("Adventure Zone$!?");
        String expected = "Adventure+Zone%24%21%3F";
        assertEquals("Encoding query failed", expected, actual);
    }

    @Test
    public void testCreateQueryUrl() {
        UrlBuilder urlBuilder = new UrlBuilder();

        String actual = urlBuilder.createQueryUrl("adventure+zone%24%21%3F");
        String expected = "https://itunes.apple.com/search?&term=adventure+zone%24%21%3F&media=podcast";
        assertEquals("Creating query url failed", expected, actual);
    }
}
