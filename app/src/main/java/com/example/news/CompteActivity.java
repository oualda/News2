package com.example.news;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CompteActivity extends AppCompatActivity {

    TextView txtNom, txtEmail;
    Button btnLogin, btnRegister;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compte);

        txtNom = findViewById(R.id.txtNom);
        txtEmail = findViewById(R.id.txtEmail);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Utilisateur connecté
            txtNom.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
            txtNom.setText("Nom : " + (user.getDisplayName() != null ? user.getDisplayName() : "Non disponible"));
            txtEmail.setText("Email : " + user.getEmail());
        } else {
            // Utilisateur non connecté
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);

            btnLogin.setOnClickListener(v -> {
                // Rediriger vers activité de connexion
                Intent intent = new Intent(CompteActivity.this, LoginActivity.class);
                startActivity(intent);
            });

            btnRegister.setOnClickListener(v -> {
                // Rediriger vers activité d’inscription
                Intent intent = new Intent(CompteActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }
}
