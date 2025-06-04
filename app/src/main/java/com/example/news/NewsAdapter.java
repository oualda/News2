package com.example.news;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.news.R;
import com.example.news.NewsItem;
import com.example.news.NewsDetailActivity;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private List<NewsItem> newsList;
    private TimeDifferenceCalculator tdc;

    public NewsAdapter(Context context, List<NewsItem> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        tdc=new TimeDifferenceCalculator();
        NewsItem newsItem = newsList.get(position);
        holder.title.setText(newsItem.getTitle());
        //holder.date.setText(newsItem.getPubDate().toString());
        holder.date.setText(tdc.getTimeDifferenceMessage(newsItem.getPubDate().toString()));

        // Charger l'image de l'article si elle existe
        String imageUrl = newsItem.getImageUrl();
        String imageFromDescription= newsItem.getImageUrlFromDescription();

        if (imageUrl != null) {
            Log.d("Debug_image", imageUrl);
        } else {
            Log.d("Debug_image", "Image URL is null");
        }
        if(imageFromDescription!=null)
        {
            Glide.with(context)
                    .load(imageFromDescription)
                    .placeholder(R.drawable.placeholder) // Image de chargement
                    .error(R.drawable.image_not_found) // Image si erreur
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.newsImage);
            holder.newsImage.setVisibility(View.VISIBLE);
            Log.d("ImageFromeDescription",imageFromDescription);
        }
       else if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder) // Image de chargement
                    .error(R.drawable.image_not_found) // Image si erreur
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.newsImage);
            holder.newsImage.setVisibility(View.VISIBLE);
        } else {
            holder.newsImage.setVisibility(View.GONE);
        }

        // Déterminer la source et afficher le bon logo
        String source = newsItem.getLink();
        Log.d("Debug_url",source);
        if (source.contains("mosaiquefm")) {
            holder.newsSourceLogo.setImageResource(R.drawable.mosaique_logo);
        } else if (source.contains("kapitalis")) {
            holder.newsSourceLogo.setImageResource(R.drawable.kapitalis_logo);
        } else if(source.contains("alchourouk")){
            holder.newsSourceLogo.setImageResource(R.drawable.alchourouk_logo);
        }else if(source.contains("hakaekonline")){
            holder.newsSourceLogo.setImageResource(R.drawable.hakaekonline_logo);
        }else if(source.contains("lapresse.tn")){
            holder.newsSourceLogo.setImageResource(R.drawable.lapresse_tn_logo);
        }else if(source.contains("tuniscope")){
            holder.newsSourceLogo.setImageResource(R.drawable.tuniscope_logo);
        }else if(source.contains("business")){
            holder.newsSourceLogo.setImageResource(R.drawable.businessnews_logo);
        }else if(source.contains("tunisienumerique")){
            holder.newsSourceLogo.setImageResource(R.drawable.tunisienumerique_logo);
        }else if (source.contains("leaders")){
            holder.newsSourceLogo.setImageResource(R.drawable.leader_logo);
        }else if(source.contains("babnet")){
            holder.newsSourceLogo.setImageResource(R.drawable.babnet_logo);
        }else if(source.contains("radioexpressfm")){
            holder.newsSourceLogo.setImageResource(R.drawable.expressfm_logo);
        } else if (source.contains("arabesque")) {
            holder.newsSourceLogo.setImageResource(R.drawable.arabesque_logo);
        } else if (source.contains("webmanagercenter")) {
            holder.newsSourceLogo.setImageResource(R.drawable.almasdar_logo);
        } else if (source.contains("rassdtunisia")) {
            holder.newsSourceLogo.setImageResource(R.drawable.rass_logo);
        } else if(source.contains("jawharafm")){
            holder.newsSourceLogo.setImageResource(R.drawable.jawharafm_logo);
        } else if(source.contains("africanmanager")){
            holder.newsSourceLogo.setImageResource(R.drawable.africanmanager_logo);
        } else if(source.contains("brands")){
            holder.newsSourceLogo.setImageResource(R.drawable.brands_logo);
        }

        // Ouvrir l’article complet sur clic
        /*holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("url", newsItem.getLink());
            context.startActivity(intent);
        });*/
        String content = (newsItem.getContentEncoded() != null && !newsItem.getContentEncoded().isEmpty())
                ? newsItem.getContentEncoded()
                : newsItem.getDescription();

        // Ouvrir l'article dans une interface
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArticleDetailActivity.class);
            intent.putExtra("title", newsItem.getTitle());
            intent.putExtra("date", newsItem.getPubDate());
            intent.putExtra("content", content); // Assurez-vous que NewsItem a bien un getDescription()
            intent.putExtra("url", newsItem.getLink());

            context.startActivity(intent);
        });

        holder.saveButton.setOnClickListener(v -> {
            String title = newsItem.getTitle();
            String date = (newsItem.getPubDate() != null) ? newsItem.getPubDate().toString() : null;
            String link = newsItem.getLink();
            String image = (imageFromDescription != null) ? imageFromDescription : imageUrl;

            // Vérifier que les champs essentiels ne sont pas null
            if (title == null || title.isEmpty() || date == null || date.isEmpty() || link == null || link.isEmpty()) {
                Log.e("SaveError", "Données manquantes : " + title + " | " + date + " | " + link);
                Toast.makeText(context, "Erreur : données article incomplètes", Toast.LENGTH_SHORT).show();
                return;
            }

            ArticleEntity savedArticle = new ArticleEntity();
            savedArticle.title = title;
            savedArticle.date = date;
            savedArticle.link = link;
            savedArticle.imageUrl = image;

            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                db.articleDao().insert(savedArticle);
            }).start();

            Toast.makeText(context, "Article sauvegardé pour lecture hors ligne", Toast.LENGTH_SHORT).show();

        });

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
    public void updateList(List<NewsItem> newList) {
        if (newList == null) {
            Log.e("DEBUG_ADAPTER", "La nouvelle liste est null !");
            return;
        }

        newsList.clear();
        newsList.addAll(newList);
        notifyDataSetChanged();
    }


    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        ImageView newsImage, newsSourceLogo;
        ImageButton saveButton;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            date = itemView.findViewById(R.id.newsDate);
            newsImage = itemView.findViewById(R.id.newsImage);
            newsSourceLogo = itemView.findViewById(R.id.newsSourceLogo);
            saveButton = itemView.findViewById(R.id.saveButton);
        }
    }
}
