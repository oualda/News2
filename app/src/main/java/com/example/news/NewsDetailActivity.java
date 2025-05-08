package com.example.news;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.news.R;

public class NewsDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        WebView webView = findViewById(R.id.webView);
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }
}
