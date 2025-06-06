package com.example.news;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ArticleEntity article);

    @Query("SELECT * FROM saved_articles")
    List<ArticleEntity> getAll();

    @Delete
    void delete(ArticleEntity article);
}
