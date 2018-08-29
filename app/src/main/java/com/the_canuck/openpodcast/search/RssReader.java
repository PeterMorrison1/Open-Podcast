package com.the_canuck.openpodcast.search;

import android.util.Log;

import com.the_canuck.openpodcast.Episode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RssReader {
    private String url;
    private String podcastDescription;
    private int collectionId;
    private String collectionArtist;

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LINK = "link";
    private static final String ITEM = "item";
    private static final String PUB_DATE = "pubDate";
    private static final String CHANNEL = "channel";
    private static final String MEDIA_ENCLOSURE = "enclosure";
    private static final String MEDIA_LENGTH = "length";
    private static final String MEDIA_URL = "url";
    private static final String DURATION = "itunes:duration";

    public RssReader(String url) {
        this.url = url;
    }

    public RssReader(String url, int collectionId, String collectionArtist) {
        this.url = url;
        this.collectionId = collectionId;
        this.collectionArtist = collectionArtist;
    }

    /**
     * Parses RSS link and puts items (episodes) into an array.
     *
     * @return array of parsed episodes
     */
    public List<Episode> createEpisodeList() {
        List<Episode> list = new ArrayList<>();
        InputStream stream = null;
        String text = null;

        try {
            XmlPullParser xmlParser = XmlPullParserFactory.newInstance().newPullParser();

            stream = new URL(url).openConnection().getInputStream();
            xmlParser.setInput(stream, null);
            Log.d("test", "Test entered rss reader");


            int event = xmlParser.getEventType();
            boolean done = false;
            Episode item = null;

            while (event != XmlPullParser.END_DOCUMENT && !done) {
                String name;
                String descriptionParser = xmlParser.getName();

                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        name = xmlParser.getName();

                        // parses anything within an "item" tag (so each episode listed)
                        if (name.equalsIgnoreCase(ITEM)) {
                            item = new Episode();
                        } else if (item != null) {
                            if (name.equalsIgnoreCase(LINK)) {
                                item.setLink(xmlParser.nextText());
                            } else if (name.equalsIgnoreCase(DESCRIPTION)) {
                                item.setDescription(xmlParser.nextText().trim());
                            } else if (name.equalsIgnoreCase(PUB_DATE)) {
                                item.setPubDate(xmlParser.nextText());
                            } else if (name.equalsIgnoreCase(TITLE)) {
                                item.setTitle(xmlParser.nextText().trim());
                            } else if (name.equalsIgnoreCase(MEDIA_ENCLOSURE)) {
                                item.setMediaUrl(xmlParser.getAttributeValue
                                        (null, MEDIA_URL));
                                item.setLength(xmlParser.getAttributeValue
                                        (null, MEDIA_LENGTH));
                            } else if (name.equalsIgnoreCase(DURATION)) {
                                item.setDuration(xmlParser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        // gets the text for the description in END_TAG case
                        text = xmlParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        // finds description tag in html for the whole rss feed (not per episode)
                        if (descriptionParser.equalsIgnoreCase(DESCRIPTION)) {
                            podcastDescription = text;
                        }

                        // check for end of item or end of RSS feed
                        name = xmlParser.getName();
                        if (name.equalsIgnoreCase(ITEM) && item != null) {
                            // sets the collectionId (podcast) for the episode
                            item.setCollectionId(collectionId);
                            item.setArtist(collectionArtist);
                            list.add(item);
                        } else if (name.equalsIgnoreCase(CHANNEL)) {
                            done = true;
                        }
                        break;
                }
                event = xmlParser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * Gets parsed description for whole podcast (not individual episodes).
     *
     * @return parsed string of description
     */
    public String getPodcastDescription() {
        return podcastDescription;
    }

    public RssReader setCollectionId(int collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    public RssReader setCollectionArtist(String collectionArtist) {
        this.collectionArtist = collectionArtist;
        return this;
    }
}
