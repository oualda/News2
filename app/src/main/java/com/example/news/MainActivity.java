package com.example.news;

import static com.example.news.R.id.progressBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.news.NewsAdapter;
import com.example.news.NewsItem;
import com.example.news.NewsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import androidx.appcompat.widget.SearchView;
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private NewsViewModel viewModel;
    private ProgressBar progressBar;
    private List<NewsItem> allNewsList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private TextView userName, userEmail;
    private NewsAdapter articleAdapter;
    private List<NewsItem> articleList, filteredList;



    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("DEBUG_NEWS", "L'application a démarré !");

        // Lancer la vérification périodique du flux RSS pour les notifications
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(RssCheckWorker.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "CheckNewArticlesWork",
                ExistingPeriodicWorkPolicy.KEEP,
                request
        );

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int id =item.getItemId();
            if(id==R.id.navigation_account){
                Intent intent = new Intent(this,CompteActivity.class);
                startActivity(intent);
            }
            /*switch (item.getItemId()) {
                case R.id.navigation_resources:
                    // Affiche la page d'accueil
                    return true;

                case R.id.navigation_account:
                    // Ouvre l'activité compte
                    Intent intent = new Intent(MainActivity.this, CompteActivity.class);
                    startActivity(intent);
                    return true;

                // autres cas...
            }*/
            return false;
        });


   

        // Initialiser la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        //searchView = findViewById(R.id.searchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        //viewModel = new NewsViewModel(this);

        // Initialisation du Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialisation du DrawerLayout et de la Toolbar
        drawerLayout = findViewById(R.id.drawer_layout);
        // Ajout du bouton pour ouvrir le menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialisation de la NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Mise à jour du profil utilisateur dans le header
        updateNavHeader(navigationView);
        // Observer les données du flux RSS
        viewModel.getNews().observe(this, newsList -> {
            Log.d("DEBUG_UI", "Mise à jour UI avec " + (newsList != null ? newsList.size() : 0) + " articles");

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (newsList != null && !newsList.isEmpty()) {
                adapter = new NewsAdapter(this, newsList);
                recyclerView.setAdapter(adapter);
            } else {
                Log.e("DEBUG_UI", "Aucun article récupéré !");
            }
        });

    }
    private void updateNavHeader(NavigationView navigationView) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
            userEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Ouvrir Accueil
        } else if (id == R.id.nav_articles) {
            // Ouvrir Liste des Articles
            startActivity(new Intent(this, ArticleDetailActivity.class));
        } else if (id == R.id.nav_settings) {
            // Ouvrir Paramètres
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            // Déconnexion Firebase
            firebaseAuth.signOut();
            //startActivity(new Intent(this, LoginActivity.class));
            //finish();
        }
        if (item.getItemId() == R.id.nav_resources) {
            startActivity(new Intent(this, ResourcesActivity.class));
        }

        // Fermer le menu après sélection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void updateUI(List<NewsItem> newsList) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        if (newsList != null) {
            adapter = new NewsAdapter(this, newsList);
            recyclerView.setAdapter(adapter);
        }
    }
    // Filtrer les articles en fonction de la recherche
    private void filterNews(String query) {
        if (adapter == null) {
            Log.e("DEBUG_SEARCH", "L'Adapter est null, impossible de filtrer !");
            return;
        }

        List<NewsItem> filteredList = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            for (NewsItem item : viewModel.getNews().getValue()) {
                if (item.getLink().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                    Log.d("Debug_search_article:", item.getTitle());
                }
            }
        } else if(filteredList.isEmpty()) {
            filteredList.addAll(viewModel.getNews().getValue());
        }

        Log.d("DEBUG_SEARCH", "Nombre d'articles après filtrage : " + filteredList.size());
        adapter.updateList(filteredList);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView(); // Assurez-vous que l'import est correct !

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Toast.makeText(MainActivity.this, "Recherche : " + query, Toast.LENGTH_SHORT).show();
                    filterNews(query);
                    //filterArticles(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    return false;
                }
            });
        }

        return true;
    }

    // Filtrage de la liste d'articles
    private void filterArticles(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(articleList);
        } else {
            for (NewsItem article : articleList) {
                if (article.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(article);
                }
            }
        }
        articleAdapter.notifyDataSetChanged();
    }



}
