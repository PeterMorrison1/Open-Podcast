package com.the_canuck.openpodcast.search;

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
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String LINK = "link";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String CHANNEL = "channel";
    static final String MEDIA_ENCLOSURE = "enclosure";
    static final String MEDIA_LENGTH = "length";
    static final String MEDIA_URL = "url";
    static final String DURATION = "itunes:duration";

    public RssReader(String url) {
        this.url = url;
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
}
