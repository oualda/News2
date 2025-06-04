package com.example.news;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.List;

public class SavedArticlesAdapter extends RecyclerView.Adapter<SavedArticlesAdapter.ViewHolder> {
    private Context context;
    private List<ArticleEntity> articles;

    public SavedArticlesAdapter(Context context, List<ArticleEntity> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public SavedArticlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedArticlesAdapter.ViewHolder holder, int position) {
        ArticleEntity article = articles.get(position);

        holder.title.setText(article.getTitle());
        holder.date.setText(article.getPubDate());

        // Charger l’image locale si elle existe
        if (article.getImagePath() != null) {
            File imgFile = new File(article.getImagePath());
            if (imgFile.exists()) {
                holder.image.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                holder.image.setVisibility(View.VISIBLE);
            } else {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.savedTitle);
            date = itemView.findViewById(R.id.savedDate);
            image = itemView.findViewById(R.id.savedImage);
        }
    }
}
