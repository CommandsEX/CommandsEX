package com.commandsex.helpers;

import com.commandsex.CommandsEX;
import org.apache.commons.lang.WordUtils;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebHelper {

    public enum Dictionary {
        URBAN_DICTIONARY("http://api.urbandictionary.com/v0/define?term=%WORD%", 0),
        DUCK_DUCK_GO("http://api.duckduckgo.com/?q=define+%WORD%&format=json", 1);

        private String url;
        private int id;

        Dictionary(String url, int id) {
            this.url = url;
            this.id = id;
        }

        public URL getFormattedURL(String word) throws MalformedURLException {
            return new URL(this.url.replace("%WORD%", word));
        }

        public int getID() {
            return this.id;
        }

        @Override
        public String toString() {
            return WordUtils.capitalize(this.name().replace("_", " "));
        }

        public static Dictionary getDictionaryFromID(int id) {
            for(Dictionary dict : Dictionary.values()) {
                if(dict.getID() == id)
                    return dict;
            }
            return null;
        }

        public static class Definition {
            private String definition, url;

            public Definition(String definition, String url) {
                this.definition = definition;
                this.url = url;
            }

            public String getDefinition() {return definition;}
            public String getUrl() {return url;}

        }

    }

    public static String readURLToString(URL url) throws IOException {
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setConnectTimeout(10000);
        http.setReadTimeout(10000);
        http.connect();
        return http.getErrorStream() == null ? Utils.convertStreamToString((InputStream)url.getContent()) : null;
    }

    public static String shortenURL(String url) {
        try {
            return readURLToString(new URL("http://v.gd/create.php?format=simple&url=" + url));
        } catch (IOException e) {
            return null;
        }
    }

    public static Dictionary.Definition getDefinition(String word) throws IOException {
        return getDefinition(word, Dictionary.valueOf(CommandsEX.config.getString("defaultDictionary", "DUCK_DUCK_GO")));
    }

    public static Dictionary.Definition getDefinition(String word, Dictionary dictionary) throws IOException {
        if(dictionary == Dictionary.URBAN_DICTIONARY) {
            JsonObject obj = new JsonParser().parse(readURLToString(dictionary.getFormattedURL(word))).getAsJsonObject();
            if(!obj.get("result_type").getAsString().equals("no_results") && !obj.getAsJsonArray("list").isJsonNull())
                return new Dictionary.Definition(obj.getAsJsonArray("list").get(0).getAsJsonObject().get("definition").getAsString(), shortenURL("http://www.urbandictionary.com/define.php?term=" + word));
        } else if(dictionary == Dictionary.DUCK_DUCK_GO) {
            JsonObject obj = new JsonParser().parse(readURLToString(dictionary.getFormattedURL(word))).getAsJsonObject();
            if(!obj.get("AbstractText").getAsString().equals(""))
                return new Dictionary.Definition(obj.get("AbstractText").getAsString(), shortenURL("http://www.thefreedictionary.com/" + word));
        }
        return null;
    }

}
