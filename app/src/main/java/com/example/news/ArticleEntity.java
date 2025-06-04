package com.example.news;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_articles")
public class ArticleEntity {
    @PrimaryKey
    @NonNull
    public String link;  // ou un ID unique
    public String title;
    public String description;
    public String content;
    public String date;
    public String imageUrl;

    public String getTitle() {
        return title;
    }

    public String getPubDate() {
        return date;
    }

    public String getImagePath() {
        return imageUrl;
    }
}
