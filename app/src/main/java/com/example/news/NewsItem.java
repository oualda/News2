package com.example.news;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Root(name = "item", strict = false)
public class NewsItem {

    @Element(name = "title")
    private String title="";

    @Element(name = "link")
    private String link;

    @Element(name = "description", required = false)
    private String description;

    @Element(name = "pubDate", required = false)
    private String pubDate;


    @Element(name = "enclosure", required = false)
    private Enclosure enclosure;

    @ElementList(entry = "content", inline = true, required = false)
    private List<MediaContent> mediaContentList;

    @Element(name = "thumbnail", required = false)
    private MediaThumbnail mediaThumbnail;
    @Namespace(reference = "http://purl.org/rss/1.0/modules/content/")
    @Element(name = "encoded", required = false)
    private String contentEncoded;
    public String getTitle() { return title; }
    public String getLink() { return link; }
    public String getDescription() { return description; }
    //public String getPubDate() { return pubDate; }
    public Date getPubDate() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        try {
            return format.parse(pubDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(0); // Retourne une date par défaut en cas d'erreur
        }
    }
    public String getImageUrlFromDescription() {
        if (description == null) return null;

        // Expression régulière pour extraire l'URL de l'image
        Pattern pattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(description);

        if (matcher.find()) {
            return matcher.group(1); // Retourne l'URL de l'image
        }

        return null; // Aucune image trouvée
    }
    public String getContentEncoded() { return contentEncoded; }

    // Méthode pour récupérer l'URL de l'image
    public String getImageUrl() {
        if (enclosure != null) {
            return enclosure.getUrl();
        } else if (mediaThumbnail != null) {
            return mediaThumbnail.getUrl();
        } else if (mediaContentList != null && !mediaContentList.isEmpty()) {
            return mediaContentList.get(0).getUrl();
        }
        return null;
    }
}
