package com.example.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView titleTextView, dateTextView,commentsList, likeCount;
    private EditText commentInput;
    private TextView articleWebView;
    private Button readMoreButton, likeButton, dislikeButton, postCommentButton, shareButton;
    private String articleUrl;
    private ImageView img_logo;
    private int likeCounter = 0; // Nombre de "J'aime"
    private FirebaseFirestore db;
    private String articleId; // Identifiant unique de l'article
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private TextView userName, userEmail;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    public String test_title,test_content,test_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        // Initialiser la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        // Initialisation du DrawerLayout et de la Toolbar
        drawerLayout = findViewById(R.id.drawer_layout);
        // Ajout du bouton pour ouvrir le menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // Initialisation du Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialisation de la NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Mise à jour du profil utilisateur dans le header
        updateNavHeader(navigationView);

        // Récupérer les vues
        titleTextView = findViewById(R.id.articleTitle);
        dateTextView = findViewById(R.id.articleDate);
        articleWebView = findViewById(R.id.articleWebView);
        readMoreButton = findViewById(R.id.readMoreButton);
        likeButton = findViewById(R.id.likeButton);
        likeCount = findViewById(R.id.likeCount);
        commentInput = findViewById(R.id.commentInput);
        postCommentButton = findViewById(R.id.postCommentButton);

        shareButton = findViewById(R.id.shareButton);
        img_logo= findViewById(R.id.img_logo);
        // Initialisation RecyclerView
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerViewComments.setAdapter(commentAdapter);



        // Récupérer les données de l'article
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            test_title=title;
            String date = intent.getStringExtra("date");
            test_date=date;
            String content = intent.getStringExtra("content");
            test_content=content;
            articleUrl = intent.getStringExtra("url");

            titleTextView.setText(title);
            dateTextView.setText(date);

            // Charger le contenu HTML
            if (content != null) {
                String htmlContent = "<html><body style='padding:10px;'>" + content + "</body></html>";
                //articleWebView.loadData(htmlContent, "text/html", "UTF-8");
                articleWebView.setText(Html.fromHtml(content));
            }
        }

        // Déterminer la source et afficher le bon logo
        String source = articleUrl;
        Log.d("Debug_url",source);
        if (source.contains("mosaiquefm")) {
            img_logo.setImageResource(R.drawable.mosaique_logo);
        } else if (source.contains("kapitalis")) {
            img_logo.setImageResource(R.drawable.kapitalis_logo);
        } else if(source.contains("alchourouk")){
            img_logo.setImageResource(R.drawable.alchourouk_logo);
        }else if(source.contains("hakaekonline")){
            img_logo.setImageResource(R.drawable.hakaekonline_logo);
        }else if(source.contains("lapresse.tn")){
            img_logo.setImageResource(R.drawable.lapresse_tn_logo);
        }else if(source.contains("tuniscope")){
            img_logo.setImageResource(R.drawable.tuniscope_logo);
        }else if(source.contains("business")){
            img_logo.setImageResource(R.drawable.businessnews_logo);
        }else if(source.contains("tunisienumerique")){
            img_logo.setImageResource(R.drawable.tunisienumerique_logo);
        }else if (source.contains("leaders")){
            img_logo.setImageResource(R.drawable.leader_logo);
        }else if(source.contains("babnet")){
            img_logo.setImageResource(R.drawable.babnet_logo);
        }else if(source.contains("africanmanager")){
            img_logo.setImageResource(R.drawable.africanmanager_logo);
        }else if(source.contains("jawharafm")){
            img_logo.setImageResource(R.drawable.jawharafm_logo);
        }else if(source.contains("radioexpressfm")){
            img_logo.setImageResource(R.drawable.expressfm_logo);
        }else if(source.contains("arabesque")){
            img_logo.setImageResource(R.drawable.arabesque_logo);
        }


        // Ouvrir l'article dans un navigateur lorsqu'on clique sur le bouton
       /* readMoreButton.setOnClickListener(view -> {
            if (articleUrl != null) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(articleUrl));
                startActivity(browserIntent);
            }
        });*/

        readMoreButton.setOnClickListener(view -> {
            if (articleUrl != null) {
                Intent browserintent = new Intent(this, BrowserActivity.class);
                browserintent.putExtra("articleUrl",articleUrl);
                startActivity(browserintent);
            }
        });

        // Gestion des boutons
        //likeButton.setOnClickListener(v -> likeCounter++);
        likeButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                startActivity(new Intent(ArticleDetailActivity.this, LoginActivity.class));
            }else {
                //save_like_dislike(articleUrl);
            }
            /*likeCounter++;
            likeCount.setText(String.valueOf(likeCounter));*/
        });
        db = FirebaseFirestore.getInstance();

        // Récupérer l'ID de l'article depuis l'Intent
        articleId = getIntent().getStringExtra("url"); // Utiliser l'URL comme identifiant unique

        // Charger les commentaires existants

        loadComments_adapter(articleId);
        postCommentButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                startActivity(new Intent(ArticleDetailActivity.this, LoginActivity.class));
            } else {
                // L'utilisateur est connecté, il peut commenter
                String comment = commentInput.getText().toString();
                if (!comment.isEmpty()) {
                    //commentsList.append("\n• " + comment);
                    saveComment(user.getDisplayName(), user.getEmail(),comment,user.getPhotoUrl().toString());
                    commentInput.setText("");
                }
            }

        });
        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, articleUrl);
            startActivity(Intent.createChooser(shareIntent, "Partager via"));
        });



    }

    private void saveComment(String username, String userEmail, String comment,String photourl) {
        // Générer l'ID de l'article
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            articleId = Base64.getEncoder().encodeToString(articleUrl.getBytes());
        }
        Log.d("Post_Comment", "Article ID: " + articleId);

        // Créer ou mettre à jour le document articleId avec des métadonnées
        Map<String, Object> articleData = new HashMap<>();
        articleData.put("url", articleUrl); // URL de l'article
        articleData.put("title", titleTextView.getText().toString()); // Remplacez par le titre réel
        articleData.put("createdAt", System.currentTimeMillis());


        db.collection("comments").document(articleId)
                .set(articleData, SetOptions.merge()) // Merge pour ne pas écraser les données existantes
                .addOnSuccessListener(aVoid -> {
                    Log.d("Post_Comment", "✅ Document article créé/mis à jour avec succès");

                    // Ajouter le commentaire dans la sous-collection user_comments
                    Map<String, Object> commentData = new HashMap<>();
                    commentData.put("username", username);
                    commentData.put("userEmail", userEmail);
                    commentData.put("comment", comment);
                    commentData.put("profileImageUrl",photourl);
                    commentData.put("timestamp", System.currentTimeMillis());

                    db.collection("comments").document(articleId).collection("user_comments").document(String.valueOf(System.currentTimeMillis()))
                            .set(commentData,SetOptions.merge())
                            .addOnSuccessListener(documentReference -> {
                                //Log.d("Post_Comment", "✅ Commentaire enregistré avec succès : " + documentReference.getId());
                                //loadComments();  // Recharger les commentaires après ajout
                                // Auto-scroll vers le dernier commentaire
                                recyclerViewComments.post(() ->
                                        recyclerViewComments.smoothScrollToPosition(commentList.size() - 1));
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Post_Comment", "❌ Échec de l'enregistrement du commentaire : " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("Post_Comment", "❌ Échec de la création/mise à jour du document article : " + e.getMessage());
                });

    }

    private void loadComments_adapter(String articleId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            articleId = Base64.getEncoder().encodeToString(articleUrl.getBytes());
        }
        db.collection("comments").document(articleId)
                .collection("user_comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Erreur lors du chargement des commentaires : ", error);
                        return;
                    }

                    commentList.clear();
                    if (value != null && !value.isEmpty()) {
                    for (QueryDocumentSnapshot doc : value) {
                        Comment comment = doc.toObject(Comment.class);
                        commentList.add(comment);

                            Log.d("Photo_url","PHOTO:"+comment.getProfileImageUrl()+" "+comment.getUserEmail()+" "+comment.getTimestamp());

                    }

                    // Mettre à jour l'adaptateur
                    commentAdapter.notifyDataSetChanged();
                    // Auto-scroll vers le dernier commentaire
                    recyclerViewComments.post(() ->
                            recyclerViewComments.smoothScrollToPosition(commentList.size() - 1));
                }
                });
    }

    private String load_like_dislike(String articleId){
        String like="0";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            articleId = Base64.getEncoder().encodeToString(articleUrl.getBytes());
        }
        /*db.collection("like_dislike").document(articleId)
                .collection("user_likedislike")
                .*/
        db.collection("like_dislike")
                .document(articleId)
                .collection("user_likedislike")
                .document("user_likedislikeId")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                         long likeNumber=0;
                                 likeNumber = documentSnapshot.getLong("like_number");
                        long dislikeNumber = documentSnapshot.getLong("dislike_number");
                        // Afficher les likes et dislikes dans votre UI
                       //like=Long.toString(likeNumber);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Erreur lors de la récupération des données", e);
                });
        return like;

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
        return false;
    }
}
