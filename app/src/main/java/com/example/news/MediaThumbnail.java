package com.example.news;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "thumbnail", strict = false)
public class MediaThumbnail {

    @Attribute(name = "url", required = false)
    private String url;

    @Attribute(name = "width", required = false)
    private int width;

    @Attribute(name = "height", required = false)
    private int height;

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
