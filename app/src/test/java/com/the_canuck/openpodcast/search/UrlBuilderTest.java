package com.the_canuck.openpodcast.search;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlBuilderTest {
    private UrlBuilder urlBuilder;

    @Before
    public void setUp() {
        urlBuilder = new UrlBuilder();
    }

    @Test
    public void createQueryUrl_Should_ReturnValidUrl_When_SingleQueryWordNoGenre() {
        String query = "Adventure";
        boolean isGenre = false;

        String expected = "https://itunes.apple.com/search?&term=Adventure&media=podcast";
        String actual = urlBuilder.createQueryUrl(query, isGenre);

        assertEquals(expected, actual);
    }

    @Test
    public void createQueryUrl_Should_ReturnValidUrl_When_MultipleQueryWordsNoGenre() {
        String query = "Adventure zone";
        boolean isGenre = false;

        String expected = "https://itunes.apple.com/search?&term=Adventure%20zone&media=podcast";
        String actual = urlBuilder.createQueryUrl(query, isGenre);

        assertEquals(expected, actual);
    }

    @Test
    public void createQueryUrl_Should_ReturnItunesUrlWithNoTermBlank_When_EmptyQueryNoGenre() {
        // The search function prevents user from searching an empty string, but test either way
        String query = "";
        boolean isGenre = false;

        String expected = "https://itunes.apple.com/search?&term=&media=podcast";
        String actual = urlBuilder.createQueryUrl(query, isGenre);

        assertEquals(expected, actual);
    }

    @Test
    public void createQueryUrl_Should_ReturnValidUrl_When_SpecialCharactersInQueryNoGenre() {
        String query = "Adventure zone$!?";
        boolean isGenre = false;

        String expected = "https://itunes.apple.com/search?&term=Adventure%20zone$!?&media=podcast";
        String actual = urlBuilder.createQueryUrl(query, isGenre);

        assertEquals(expected, actual);
    }

    @Test
    public void createQueryUrl_Should_EncodeMultipleSpacesInUrl_When_UrlContainsExcessiveSpacesNoGenre() {
        String query = "    Adventure     Zone    ";
        boolean isGenre = false;

        // Testing to see if spaces side by side mess it up, and the below url DOES give back result
        String expected = "https://itunes.apple.com/search?&term=%20%20%20%20Adventure%20%20%20%20" +
                "%20Zone%20%20%20%20&media=podcast";
        String actual = urlBuilder.createQueryUrl(query, isGenre);

        assertEquals(expected, actual);
    }

    @Test
    public void createQueryUrl_Should_ReturnValidUrl_When_UrlContainsTopLevelDomainNoGenre() {
        // Still gives results as if .com wasn't in it
        String query = "Adventure.com";
        boolean isGenre = false;

        String expected = "https://itunes.apple.com/search?&term=Adventure.com&media=podcast";
        String actual = urlBuilder.createQueryUrl(query, isGenre);

        assertEquals(expected, actual);
    }

    @Test
    public void createQueryUrl_Should_ReturnValidUrl_When_GenreIdIsValid() {
        String query = "1301";
        boolean isGenre = true;

        String expected = "https://itunes.apple.com/search?&genreId=1301&term=podcast&limit=15&media=podcast";
        String actual = urlBuilder.createQueryUrl(query, isGenre);

        assertEquals(expected, actual);
    }

    @Test
    public void createQueryUrl_Should_ReturnValidUrl_When_GenreIdIsEmpty() {
        String query = "";
        boolean isGenre = true;

        String expected = "https://itunes.apple.com/search?&genreId=&term=podcast&limit=15&media=podcast";
        String actual = urlBuilder.createQueryUrl(query, isGenre);

        assertEquals(expected, actual);
    }

    @Test
    public void encodeQueryTerms_Should_ReturnUTF8EncodedQuery_When_PassedValidString() {
        String query = "Adventure zone";

        String expected = "Adventure+zone";
        String actual = urlBuilder.encodeQueryTerms(query);

        assertEquals(expected, actual);
    }

    @Test
    public void encodeQueryTerms_Should_ReturnUTF8EncodedQuery_When_SpecialCharactersInQuery() {
        String query = "Adventure zone$!?";

        String expected = "Adventure+zone%24%21%3F";
        String actual = urlBuilder.encodeQueryTerms(query);

        assertEquals(expected, actual);
    }

}
