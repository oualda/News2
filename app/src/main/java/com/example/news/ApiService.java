package com.example.news;

import com.example.news.RSSFeed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Call<RSSFeed> getNews(@Url String url);
}


/*import com.example.news.RSSFeed;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiService {
    @GET("rss")// L'URL est définie dynamiquement dans Retrofit
    Call<RSSFeed> getNews();
}*/