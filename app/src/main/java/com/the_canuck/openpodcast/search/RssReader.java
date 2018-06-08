package com.the_canuck.openpodcast.search;

import android.util.Xml;

import com.the_canuck.openpodcast.Episode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RssReader {
//    private String testUrl = "https://adventurezone.libsyn.com/rss";
    private String testUrl;
    private List<Episode> episodeList = new ArrayList<>();
    private String podcastDescription;
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String LINK = "link";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String CHANNEL = "channel";

    public RssReader(String testUrl) {
        this.testUrl = testUrl;
    }


    public RssReader() {
    }

    public List<Episode> createEpisodeList() {
        List<Episode> list = new ArrayList<>();
        XmlPullParser xmlParser = Xml.newPullParser();
        InputStream stream = null;
        String text = null;

        try {
            stream = new URL(testUrl).openConnection().getInputStream();
            xmlParser.setInput(stream, null);

            int event = xmlParser.getEventType();
            boolean done = false;
            Episode item = null;

            while (event != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                String descriptionParser = xmlParser.getName();

                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        name = xmlParser.getName();
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
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        // gets the text for the description in END_TAG case
                        text = xmlParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (descriptionParser.equalsIgnoreCase(DESCRIPTION)) {
                            podcastDescription = text;
                        }

                        name = xmlParser.getName();
                        if (name.equalsIgnoreCase(ITEM) && item != null) {
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

    public String getPodcastDescription() {
        return podcastDescription;
    }
}
