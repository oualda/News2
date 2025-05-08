package com.example.news;

import android.util.Log;

import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class RssParserUtils {

    private static final String TAG = "RssParserUtils";

    /**
     * Cette méthode récupère et analyse un flux RSS depuis l'URL fournie.
     * @param source L'URL du flux RSS
     * @return Liste d'articles (NewsItem)
     */
    public static List<NewsItem> fetchRssArticles(String source) {
        List<NewsItem> newsItems = new ArrayList<>();

        try {
            ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
            Response<RSSFeed> response = apiService.getNews(source).execute();  // Appel SYNCHRONE

            if (response.isSuccessful() && response.body() != null && response.body().getChannel() != null) {
                newsItems.addAll(response.body().getChannel().getNewsItems());
            } else {
                Log.e(TAG, "Réponse invalide ou vide : " + response.code());
            }

        } catch (Exception e) {
            Log.e(TAG, "Erreur Retrofit lors de l'analyse du flux RSS", e);
        }

        return newsItems;
    }
}
