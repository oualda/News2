package com.example.news;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.news.ApiClient;
import com.example.news.ApiService;
import com.example.news.NewsItem;
import com.example.news.RSSFeed;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsViewModel extends ViewModel {
    public static final String[] RSS_SOURCES = {
            "https://kapitalis.com/tunisie/feed/",
            "https://brands.com.tn/feed/",
            "https://www.mosaiquefm.net/ar/rss",
            "https://lapresse.tn/feed/",
            "https://ar.africanmanager.com/feed/",
            "https://ar.leaders.com.tn/rss",
           // "https://www.leaders.com.tn/rss",
           // "https://www.tuniscope.com/feed",
            "https://www.jawharafm.net/ar/rss/showRss/88/1/1",
            "https://www.alchourouk.com/rss",
            "https://hakaekonline.com/feed/",
            "https://www.businessnews.com.tn/rss.xml",
            "https://www.babnet.net/feed.php",
            "https://www.arrakmia.com/feed-actualites-tunisie.xml",
            "https://www.arabesque.tn/ar/rss",
           "https://radioexpressfm.com/ar/feed/",
           "http://ar.webmanagercenter.com/feed/",
            "https://rassdtunisia.net/feed/"

    };

    private final MutableLiveData<List<NewsItem>> newsLiveData = new MutableLiveData<>();
    private final List<NewsItem> allNewsList = new ArrayList<>();
    private int sourcesProcessed = 0;


    private ApiService apiService;
    private Context context;
    private PreferencesManager preferencesManager;


    public LiveData<List<NewsItem>> getNews() {
        for (String source : RSS_SOURCES) {
            fetchNewsFromSource(source);
        }
        return newsLiveData;
    }



     private void fetchNewsFromSource(String baseUrl) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.getNews(baseUrl).enqueue(new Callback<RSSFeed>() {
            @Override
            public void onResponse(Call<RSSFeed> call, Response<RSSFeed> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allNewsList.addAll(response.body().getChannel().getNewsItems());
                }

                sourcesProcessed++;
                if (sourcesProcessed == RSS_SOURCES.length) {
                    Collections.sort(allNewsList, (a, b) -> b.getPubDate().compareTo(a.getPubDate()));
                    newsLiveData.setValue(allNewsList);
                }
            }

            @Override
            public void onFailure(Call<RSSFeed> call, Throwable t) {
                Log.e("DEBUG_RSS", "Erreur sur " + baseUrl + ": " + t.getMessage());
                sourcesProcessed++;
                if (sourcesProcessed == RSS_SOURCES.length) {
                    newsLiveData.setValue(allNewsList);
                }
            }
        });
    }
   public void loadNews() {
       SharedPreferences sharedPreferences = context.getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);
       Set<String> selectedSources = sharedPreferences.getStringSet("SELECTED_RSS", new HashSet<>());

       allNewsList.clear();
       sourcesProcessed = 0;

       for (String url : selectedSources) {
           apiService.getNews(url).enqueue(new Callback<RSSFeed>() {
               @Override
               public void onResponse(Call<RSSFeed> call, Response<RSSFeed> response) {
                   if (response.isSuccessful() && response.body() != null) {
                       allNewsList.addAll(response.body().getChannel().getNewsItems());
                   }

                   sourcesProcessed++;
                   if (sourcesProcessed == selectedSources.size()) {
                       Collections.sort(allNewsList, (a, b) -> b.getPubDate().compareTo(a.getPubDate()));
                       newsLiveData.setValue(allNewsList);
                   }
               }

               @Override
               public void onFailure(Call<RSSFeed> call, Throwable t) {
                   sourcesProcessed++;
               }
           });
       }
   }

}
