package com.example.news;

public class Resource {
    private String name;
    private String url;
    private boolean isSelected; // Indique si l'utilisateur a sélectionné cette source

    public Resource(String name, String url, boolean isSelected) {
        this.name = name;
        this.url = url;
        this.isSelected = isSelected;
    }

    public String getName() { return name; }
    public String getUrl() { return url; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
