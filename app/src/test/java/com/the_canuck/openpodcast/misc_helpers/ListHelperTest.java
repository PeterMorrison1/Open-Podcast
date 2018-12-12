package com.the_canuck.openpodcast.misc_helpers;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.misc_helpers.ListHelper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListHelperTest {
    private Episode episodeOne;
    private Episode episodeTwo;
    private Episode episodeThree;

    private List<Episode> episodeList;

    @Before
    public void setUp() {
        episodeOne = new Episode();
        episodeOne.setTitle("Ep 1");
        episodeOne.setPubDate("Mon, 24 Oct 2016 09:23:29 +0000");

        episodeTwo = new Episode();
        episodeTwo.setTitle("Ep 2");
        episodeTwo.setPubDate("Mon, 07 Nov 2016 15:36:58 +0000");

        episodeThree = new Episode();
        episodeThree.setTitle("Ep 3");
        episodeThree.setPubDate("Mon, 21 Nov 2016 15:48:41 +0000");

        episodeList = new ArrayList<>();
    }

    @Test
    public void getSortedIndex_Should_ReturnFirstIndexOf0_When_ListIsEmpty() {
        // Tests for when the list is empty, then the episode should be placed in the first index
        int actual = ListHelper.getSortedIndex(episodeOne.getPubDate(), episodeList);
        int expected = 0;
        assertEquals("Not empty arraylist", expected, actual);
    }

    @Test
    public void getSortedIndex_Should_ReturnNeg1_When_OlderDateAddedWhenListContainsNewer() {
        // If list has ep 2, then ep 1 being added should be placed before, therefore -1
        // Special case to handle only 1 list item and adding an older item in.
        episodeList.add(episodeOne);
        int actual = ListHelper.getSortedIndex(episodeTwo.getPubDate(), episodeList);
        int expected = -1;
        assertEquals("Index not equal to -1", expected, actual);
    }

    @Test
    public void getSortedIndex_Should_ReturnIndexPlace_When_MiddleDateAddedToListWithOneOlderOneNewer() {
        episodeList.add(episodeOne);
        episodeList.add(episodeThree);
        int actual = ListHelper.getSortedIndex(episodeTwo.getPubDate(), episodeList);
        int expected = 1;
        assertEquals("Index not equal to proper place (1)", expected, actual);
    }

    @Test
    public void determineNewerDate_Should_ReturnOlder_When_DateAIsOlderThanDateB() {
        int actual = ListHelper.determineNewerDate(episodeOne.getPubDate(), episodeTwo.getPubDate());
        int expected = -1;
        assertEquals("Formatting of date incorrect", expected, actual);
    }

    @Test
    public void determineNewerDate_Should_ReturnNewer_When_DateAIsOlderThanDateB() {
        int actual = ListHelper.determineNewerDate(episodeTwo.getPubDate(), episodeOne.getPubDate());
        int expected = 1;
        assertEquals("Formatting of date incorrect", expected, actual);
    }

    @Test
    public void determineNewerDate_Should_ReturnSame_When_DateAIsSameAsDateB() {
        int actual = ListHelper.determineNewerDate(episodeOne.getPubDate(), episodeOne.getPubDate());
        int expected = 0;
        assertEquals("Formatting of date incorrect", expected, actual);
    }
}
