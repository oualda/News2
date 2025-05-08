package com.example.news;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public class RSSFeed {

    @Element(name = "channel")
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }
}
