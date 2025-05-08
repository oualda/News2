package com.example.news;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.FileWriter;
import java.io.IOException;

public class RSSGenerator {
    public static void main(String[] args) {
        String url = "https://www.exemple.com";  // Remplace par l'URL de la page
        String rssFile = "output_rss.xml";

        try {
            // Charger la page HTML
            Document doc = Jsoup.connect(url).get();

            // Commencer le flux RSS
            StringBuilder rss = new StringBuilder();
            rss.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            rss.append("<rss version=\"2.0\">\n");
            rss.append("<channel>\n");
            rss.append("<title>Flux RSS - Exemple</title>\n");
            rss.append("<link>").append(url).append("</link>\n");
            rss.append("<description>Flux RSS généré automatiquement</description>\n");

            // Extraire les articles (exemple basé sur les balises <article>)
            for (Element article : doc.select("article")) {
                String title = article.select("h2").text(); // Modifier selon la structure du site
                String link = article.select("a").attr("href");
                String description = article.select("p").text();

                if (!link.startsWith("http")) {
                    link = url + link;  // Pour les liens relatifs
                }

                // Ajouter l'article au flux RSS
                rss.append("<item>\n");
                rss.append("<title>").append(title).append("</title>\n");
                rss.append("<link>").append(link).append("</link>\n");
                rss.append("<description>").append(description).append("</description>\n");
                rss.append("</item>\n");
            }

            // Fermer le canal RSS
            rss.append("</channel>\n");
            rss.append("</rss>");

            // Écrire dans un fichier XML
            try (FileWriter writer = new FileWriter(rssFile)) {
                writer.write(rss.toString());
                System.out.println("Flux RSS généré avec succès : " + rssFile);
            }

        } catch (IOException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
