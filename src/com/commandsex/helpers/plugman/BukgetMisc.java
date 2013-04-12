package com.commandsex.helpers.plugman;

import com.commandsex.helpers.WebHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

/**
 * Class containing misc Bukget functions
 * @author Kezz101
 */
public class BukgetMisc {

    /**
     * Gets the amount of plugins in a category
     * @param category the category
     * @return the amount of plugins
     * @throws IOException if an error occurred whilst reading the api
     */
    public static Long pluginsInCategory(Bukget.Category category) throws IOException {
        JsonArray jsonArray = new JsonParser().parse(WebHelper.readURLToString(new URL("http://api.bukget.org/3/categories"))).getAsJsonArray();
        Iterator<JsonElement> it = jsonArray.iterator();

        while(it.hasNext()) {
            JsonObject next = it.next().getAsJsonObject();
            if(next.get("name").getAsString().equalsIgnoreCase(category.getFriendlyName()))
                return next.get("count").getAsLong();
            it.remove();
        }
        return null;
    }

    /**
     * Gets the amount of plugins in existence
     * @return the amount of plugins
     * @throws IOException if an error occurred whilst reading the api
     */
    public static Long amountOfPlugins() throws IOException {
        JsonArray jsonArray = new JsonParser().parse(WebHelper.readURLToString(new URL("http://api.bukget.org/3/categories"))).getAsJsonArray();
        Iterator<JsonElement> it = jsonArray.iterator();
        long amount = 0l;

        while(it.hasNext()) {
            JsonObject next = it.next().getAsJsonObject();
            amount += next.get("count").getAsLong();
        }
        return amount;
    }

}
