package com.example.news;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode, switchNotifications;
    private Spinner spinnerRssSource;
    private Button btnSaveSettings;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialisation des vues
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);
        spinnerRssSource = findViewById(R.id.spinnerRssSource);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Charger les préférences existantes
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        loadPreferences();

        // Bouton Enregistrer
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });
    }

    private void loadPreferences() {
        // Charger les préférences existantes
        switchDarkMode.setChecked(sharedPreferences.getBoolean("dark_mode", false));
        switchNotifications.setChecked(sharedPreferences.getBoolean("notifications", true));
        spinnerRssSource.setSelection(sharedPreferences.getInt("rss_source", 0));
    }

    private void savePreferences() {
        // Enregistrer les paramètres
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", switchDarkMode.isChecked());
        editor.putBoolean("notifications", switchNotifications.isChecked());
        editor.putInt("rss_source", spinnerRssSource.getSelectedItemPosition());
        editor.apply();

        Toast.makeText(this, "Paramètres enregistrés avec succès !", Toast.LENGTH_SHORT).show();
    }
}
