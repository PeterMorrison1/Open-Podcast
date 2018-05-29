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

        String actual = urlBuilder.createQueryUrl("adventure+zone%24%21%3F", false);
        String expected = "https://itunes.apple.com/search?&term=adventure+zone%24%21%3F&media=podcast";
        assertEquals("Creating query url failed", expected, actual);
    }

    @Test
    public void testCreateQueryUrlInvalid() {
        UrlBuilder urlBuilder = new UrlBuilder();

        String actual = urlBuilder.createQueryUrl("adventure zone", false);
        String expected = "https://itunes.apple.com/search?&term=adventure+zone%24%21%3F&media=podcast";
        assertNotEquals("Space found in query term", expected, actual);
    }

    @Test
    public void testCreateQueryUrlEmpty() {
        UrlBuilder urlBuilder = new UrlBuilder();

        String actual = urlBuilder.createQueryUrl(" ", false);
        String expected = "https://itunes.apple.com/search?&term=adventure+zone%24%21%3F&media=podcast";
        assertNotEquals("Empty query", expected, actual);
    }

    @Test
    public void testCreateQueryUrlGenre() {
        UrlBuilder urlBuilder = new UrlBuilder();

        String actual = urlBuilder.createQueryUrl("1301", true);
        String expected = "https://itunes.apple.com/search?&genreId=1301&term=podcast&limit=15&media=podcast";
        assertEquals("Parameter creation failed", expected, actual);
    }
}
