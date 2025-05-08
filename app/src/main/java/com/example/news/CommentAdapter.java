package com.example.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.news.R;
import com.example.news.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Remplir les données dans les vues
        holder.textUsername.setText(comment.getUsername());
        holder.textComment.setText(comment.getComment());

        // Charger l'image de profil avec Glide (par défaut si non disponible)
        if (comment.getProfileImageUrl() != null && !comment.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(comment.getProfileImageUrl())
                    .placeholder(R.drawable.ic_user)  // Image par défaut
                    .into(holder.imageProfile);

        } else {
            holder.imageProfile.setImageResource(R.drawable.ic_user);

        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView textUsername, textComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textUsername = itemView.findViewById(R.id.textUsername);
            textComment = itemView.findViewById(R.id.textComment);
        }
    }
}
