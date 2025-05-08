package com.example.news;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "content", strict = false)
public class MediaContent {

    @Attribute(name = "url", required = false)
    private String url;

    public String getUrl() {
        return url;
    }
}
