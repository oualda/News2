package com.example.news;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String PREF_NAME = "RSS_PREFS";
    private static final String KEY_SELECTED_RSS = "SELECTED_RSS";

    private SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 🔹 Sauvegarder les sources RSS sélectionnées
    public void saveSelectedSources(Set<String> sources) {
        sharedPreferences.edit().putStringSet(KEY_SELECTED_RSS, sources).apply();
    }

    // 🔹 Récupérer les sources RSS enregistrées
    public Set<String> getSelectedSources() {
        return sharedPreferences.getStringSet(KEY_SELECTED_RSS, new HashSet<>());
    }

    // 🔹 Ajouter une nouvelle source RSS
    public void addSource(String source) {
        Set<String> sources = getSelectedSources();
        sources.add(source);
        saveSelectedSources(sources);
    }

    // 🔹 Supprimer une source RSS
    public void removeSource(String source) {
        Set<String> sources = getSelectedSources();
        if (sources.contains(source)) {
            sources.remove(source);
            saveSelectedSources(sources);
        }
    }
}
