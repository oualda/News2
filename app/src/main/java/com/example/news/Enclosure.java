package com.example.news;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "enclosure", strict = false)
public class Enclosure {

    @Attribute(name = "url", required = false)
    private String url;

    public String getUrl() {
        return url;
    }
}
