package com.example.news;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class SavedArticlesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedArticlesAdapter adapter;
    private List<ArticleEntity> savedArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        recyclerView = findViewById(R.id.savedArticlesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Charger les articles sauvegardés depuis Room
        new Thread(() -> {
            savedArticles = AppDatabase.getInstance(getApplicationContext())
                    .articleDao()
                    .getAll();

            runOnUiThread(() -> {
                if (savedArticles != null && !savedArticles.isEmpty()) {
                    adapter = new SavedArticlesAdapter(this, savedArticles);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "Aucun article sauvegardé", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
