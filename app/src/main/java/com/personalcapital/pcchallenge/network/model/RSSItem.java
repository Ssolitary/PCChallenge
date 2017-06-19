package com.personalcapital.pcchallenge.network.model;

/**
 * Model for RSSItem
 */

public class RSSItem {

    private String title;
    private String link;
    private String pubDate;
    private String description;
    private String mediaContent;

    public RSSItem(String title, String link, String pubDate, String description, String mediaContent) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.mediaContent = mediaContent;
    }

    // RSSItem Accessors
    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaContent() {
        return mediaContent;
    }
}
