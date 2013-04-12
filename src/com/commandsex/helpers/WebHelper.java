package com.commandsex.helpers;

import com.commandsex.CommandsEX;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.lang.WordUtils;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class WebHelper {

    /**
     * The enum containing all Dictionaries available to CommandsEX
     */
    public enum Dictionary {
        URBAN_DICTIONARY("http://api.urbandictionary.com/v0/define?term=%WORD%", 0),
        DUCK_DUCK_GO("http://api.duckduckgo.com/?q=define+%WORD%&format=json", 1);

        private String url;
        private int id;

        Dictionary(String url, int id) {
            this.url = url;
            this.id = id;
        }

        /**
         * Gets the url of the api of the dictionary when looking up a specified word
         * @param word the word that needs looking up
         * @return the formatted URL
         * @throws MalformedURLException if there is an error with the URL
         */
        public URL getFormattedURL(String word) throws MalformedURLException {
            return new URL(this.url.replace("%WORD%", word));
        }

        /**
         * Returns the unique id of the dictionary
         * @return the unique id
         */
        public int getID() {
            return this.id;
        }

        @Override
        public String toString() {
            return WordUtils.capitalize(this.name().replace("_", " "));
        }

        /**
         * Gets a dictionary from its id
         * @param id the id
         * @return the dictionary or <code>null</code> if it does not exist
         */
        public static Dictionary getDictionaryFromID(int id) {
            for(Dictionary dict : Dictionary.values()) {
                if(dict.getID() == id)
                    return dict;
            }
            return null;
        }

        /**
         * A class that stores a definition of a word.
         * It contains both the definition and the url leading to further information about the word.
         */
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

    /**
     * Reads the contents of a webpage to a string
     * @param url the url to read
     * @return the contents of the webpage
     * @throws IOException if an error occurred during reading of the webpage
     */
    public static String readURLToString(URL url) throws IOException {
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setConnectTimeout(10000);
        http.setReadTimeout(10000);
        http.connect();
        return http.getErrorStream() == null ? Utils.convertStreamToString((InputStream)url.getContent()) : null;
    }

    /**
     * Shortens a URL using the v.gd shortening service
     * @param url the url to shorten
     * @return the shortened url as a string
     */
    public static String shortenURL(String url) {
        try {
            String str = readURLToString(new URL("http://v.gd/create.php?format=simple&url=" + url));
            return String.format("%swww.%s", str.substring(0, 7), str.substring(7));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets the definition of a word using the default dictionary
     * @param word the word to get
     * @return the definition of the word
     * @throws IOException if an error occurred whilst reading the definition
     */
    public static Dictionary.Definition getDefinition(String word) throws IOException {
        return getDefinition(word, Dictionary.valueOf(CommandsEX.config.getString("defaultDictionary", "DUCK_DUCK_GO")));
    }

    /**
     * Gets the definition of a word
     * @param word the word to get
     * @param dictionary the dictionary to lookup with
     * @return the definition of the word
     * @throws IOException if an error occurred whilst reading the definition
     */
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

    /**
     * Joins the elements in a {@link JsonArray} with a specified string in between
     * @param array the array
     * @param glueString the glue string
     * @return the joined String
     */
    public static String joinJsonArray(JsonArray array, String glueString) {
        Iterator<JsonElement> it = array.iterator();
        StringBuilder builder = new StringBuilder();
        while(it.hasNext()) {
            builder.append(it.next().getAsString() + glueString);
            it.remove();
        }
        return builder.toString().replaceAll(glueString + "$", "");
    }

}
