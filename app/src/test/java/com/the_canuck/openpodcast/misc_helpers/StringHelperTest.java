package com.the_canuck.openpodcast.misc_helpers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringHelperTest {
    private StringHelper helper;

    @Test
    public void encodeFileName_Should_ReturnEncodedOutput_When_SpecialCharactersInString() {
        String term = "Test title /slash #hashtag $dollar &and *star _underscore";
        String actual = StringHelper.encodeFileName(term);

        String expected = "Test title %2Fslash %23hashtag %24dollar %26and *star _underscore";

        assertEquals(expected, actual);
    }

    @Test
    public void encodeFileName_Should_ReturnEncodedOutput_When_NoSpecialCharacterInString() {
        String term = "testTitle";
        String actual = StringHelper.encodeFileName(term);
        String expected = "testTitle";

        assertEquals(expected, actual);
    }

    @Test
    public void encodeFileName_Should_ReturnEncodedOutput_When_RealTestInput() {
        String term = "Ep. 47 - Half-Blood Prince Ch. 17 w/ Miel Bredouw";
        String actual = StringHelper.encodeFileName(term);
        String expected = "Ep. 47 - Half-Blood Prince Ch. 17 w%2F Miel Bredouw";

        assertEquals(expected, actual);
    }
}
