package com.example.news;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourcesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ResourcesAdapter adapter;
    private List<Resource> resourcesList;
    private SharedPreferences sharedPreferences;
    public PreferencesManager preferencesManager = new PreferencesManager(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        // Ajouter une source
        preferencesManager.addSource("https://www.mosaiquefm.net/ar/rss");

        // Supprimer une source
        //preferencesManager.removeSource("https://lapresse.tn/feed/");

        recyclerView = findViewById(R.id.recyclerViewResources);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("RSS_PREFS", MODE_PRIVATE);

        resourcesList = loadResources();
        adapter = new ResourcesAdapter(this, resourcesList);
        recyclerView.setAdapter(adapter);

    }

    private List<Resource> loadResources() {
        List<Resource> list = new ArrayList<>();
        Set<String> selectedSources = sharedPreferences.getStringSet("SELECTED_RSS", new HashSet<>());

        list.add(new Resource("Kapitalis", "https://kapitalis.com/tunisie/feed/", selectedSources.contains("https://kapitalis.com/tunisie/feed/")));
        list.add(new Resource("Mosaïque FM", "https://www.mosaiquefm.net/ar/rss", selectedSources.contains("https://www.mosaiquefm.net/ar/rss")));
        list.add(new Resource("La Presse", "https://lapresse.tn/feed/", selectedSources.contains("https://lapresse.tn/feed/")));
        // Ajoute les autres flux ici...

        return list;
    }
}
