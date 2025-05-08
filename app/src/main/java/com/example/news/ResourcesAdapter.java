package com.example.news;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ViewHolder> {
    private List<Resource> resources;
    private Context context;
    private SharedPreferences sharedPreferences;

    public ResourcesAdapter(Context context, List<Resource> resources) {
        this.context = context;
        this.resources = resources;
        sharedPreferences = context.getSharedPreferences("RSS_PREFS", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource resource = resources.get(position);
        holder.name.setText(resource.getName());
        holder.checkbox.setChecked(resource.isSelected());

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            resource.setSelected(isChecked);
            saveSelectedResources();
        });
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    private void saveSelectedResources() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> selectedUrls = new HashSet<>();
        for (Resource resource : resources) {
            if (resource.isSelected()) {
                selectedUrls.add(resource.getUrl());
            }
        }
        editor.putStringSet("SELECTED_RSS", selectedUrls);
        editor.apply();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.resource_name);
            checkbox = itemView.findViewById(R.id.resource_checkbox);
        }
    }
}
