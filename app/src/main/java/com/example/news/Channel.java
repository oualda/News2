package com.example.news;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

@Root(name = "channel", strict = false)
public class Channel {

    @ElementList(entry = "item", inline = true)
    private List<NewsItem> newsItems;

    public List<NewsItem> getNewsItems() {
        return newsItems;
    }
}
