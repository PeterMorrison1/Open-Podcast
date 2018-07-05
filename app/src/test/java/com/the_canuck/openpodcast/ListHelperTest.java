package com.the_canuck.openpodcast;

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
    public void testGetSortedIndexEmpty() {
        int actual = ListHelper.getSortedIndex(episodeOne, episodeList);
        int expected = 0;
        assertEquals("Not empty arraylist", expected, actual);
    }

    @Test
    public void testGetSortedIndexTwoEps() {
        episodeList.add(episodeOne);
        int actual = ListHelper.getSortedIndex(episodeTwo, episodeList);
        int expected = -1;
        assertEquals("Index not equal to -1", expected, actual);
    }

    @Test
    public void testGetSortedIndexThreeEps() {
        episodeList.add(episodeOne);
        episodeList.add(episodeThree);
        int actual = ListHelper.getSortedIndex(episodeTwo, episodeList);
        int expected = 1;
        assertEquals("Index not equal to proper place (1)", expected, actual);
    }
}
