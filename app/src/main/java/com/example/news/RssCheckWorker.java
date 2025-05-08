package com.example.news;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RssCheckWorker extends Worker {

    public RssCheckWorker(Context context, WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean newArticlesFound = checkForNewArticles();
        if (newArticlesFound) {
            // Notification si de nouveaux articles sont trouvés
            sendNotification("Nouveaux articles disponibles !");
            return Result.success();
        } else {
            return Result.failure();
        }
    }

    private boolean checkForNewArticles() {
        // Récupérer les derniers articles RSS
        List<NewsItem> latestArticles = new ArrayList<>();

        for (String source : NewsViewModel.RSS_SOURCES) {
            List<NewsItem> articlesFromSource = RssParserUtils.fetchRssArticles(source);
            if (articlesFromSource != null) {
                latestArticles.addAll(articlesFromSource);
            }
        }

        if (latestArticles.isEmpty()) {
            return false;
        }

        // Trier les articles par date
        Collections.sort(latestArticles, (a, b) -> b.getPubDate().compareTo(a.getPubDate()));
        String latestTitle = latestArticles.get(0).getTitle();

        // Comparer avec le dernier titre d'article connu
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("news_prefs", Context.MODE_PRIVATE);
        String lastTitle = prefs.getString("last_article_title", "");

        if (!latestTitle.equals(lastTitle)) {
            // Mettre à jour le dernier titre trouvé
            prefs.edit().putString("last_article_title", latestTitle).apply();
            return true;
        }

        return false;
    }

    private void sendNotification(String message) {
        // Créer un canal de notification (requis pour Android Oreo et plus)
        String channelId = "news_channel";
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "News Updates";
            String description = "Notifies when new news articles are available.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Créer la notification
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle("Nouvelle actualité")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification);  // L'ID 1 peut être changé pour gérer plusieurs notifications.
    }
}
