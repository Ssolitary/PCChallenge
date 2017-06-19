package com.personalcapital.pcchallenge.network;

import android.util.Xml;

import com.personalcapital.pcchallenge.network.model.RSSItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modified from:
 * https://developer.android.com/samples/BasicSyncAdapter/src/com.example.android.basicsyncadapter/net/FeedParser.html#l90
 */

public class XMLParser {

    // enum for HTML tags
    private enum eTag {
        TAG_CHANNEL("channel", null),
        TAG_ITEM("item", null),
        TAG_TITLE("title", null),
        TAG_LINK("link", null),
        TAG_PUBDATE("pubDate", null),
        TAG_DESCRIPTION("description", null),
        TAG_MEDIACONTENT("media:content", "url");
        String mTagStr;
        String mAttributeStr;

        eTag(String sTagStr, String sAttributeStr) {
            this.mTagStr = sTagStr;
            this.mAttributeStr = sAttributeStr;
        }

        @Override
        public String toString() {
            return mTagStr;
        }

        public String getAttributeStr() {
            return mAttributeStr;
        }
    }

    // We don't use XML namespaces
    private static final String NS = null;

    /**
     * Parse an rss feed, returning a List of RSSItem objects.
     *
     * @param in rss feed stream.
     * @return List of {@link com.personalcapital.pcchallenge.network.model.RSSItem} objects.
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException                   on I/O error.
     */
    public List<RSSItem> parse(InputStream in)
        throws XmlPullParserException, IOException, ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag();
            return readChannel(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Decode a rss feed attached to an XmlPullParser.
     *
     * @param parser Incoming XML
     * @return List of {@link com.personalcapital.pcchallenge.network.model.RSSItem} objects.
     * @throws org.xmlpull.v1.XmlPullParserException on error parsing feed.
     * @throws java.io.IOException                   on I/O error.
     */
    private List<RSSItem> readChannel(XmlPullParser parser)
        throws XmlPullParserException, IOException {
        List<RSSItem> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, NS, eTag.TAG_CHANNEL.toString());
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (eTag.TAG_ITEM.toString().equals(name)) {
                entries.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    /**
     * Parses the contents of an item.
     */
    private RSSItem readItem(XmlPullParser parser)
        throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, eTag.TAG_ITEM.toString());

        String title = null;
        String link = null;
        String pubDate = null;
        String description = null;
        String mediaContent = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // put eTag.toString() at front to prevent name's null value
            if (eTag.TAG_TITLE.toString().equals(name)) {
                title = readBasicTag(parser, name);
            } else if (eTag.TAG_LINK.toString().equals(name)) {
                link = readBasicTag(parser, name);
            } else if (eTag.TAG_PUBDATE.toString().equals(name)) {
                pubDate = readBasicTag(parser, name);
            } else if (eTag.TAG_DESCRIPTION.toString().equals(name)) {
                description = readBasicTag(parser, name);
            } else if (eTag.TAG_MEDIACONTENT.toString().equals(name)) {
                mediaContent = readMediaTag(parser);
            } else {
                skip(parser);
            }
        }
        return new RSSItem(title, link, pubDate, description, mediaContent);
    }

    /**
     * Read the basic tag media:content.
     */
    private String readBasicTag(XmlPullParser parser, String tag)
        throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, NS, tag);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, tag);
        return result;
    }

    /**
     * For the tag media:content, extracts url attribut values.
     */
    private String readMediaTag(XmlPullParser parser)
        throws IOException, XmlPullParserException {
        String url = null;
        parser.require(XmlPullParser.START_TAG, NS, eTag.TAG_MEDIACONTENT.toString());
        String name = parser.getName();
        if (eTag.TAG_MEDIACONTENT.toString().equals(name)) {
            url = parser.getAttributeValue(null, eTag.TAG_MEDIACONTENT.getAttributeStr());
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, NS, eTag.TAG_MEDIACONTENT.toString());
        return url;
    }

    /**
     * For the tags / link / pubDate / description, extracts their text values.
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skips tags the parser isn't interested in.
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}