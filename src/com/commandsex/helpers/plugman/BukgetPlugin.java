package com.commandsex.helpers.plugman;

import com.commandsex.helpers.Utils;
import com.commandsex.helpers.WebHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A <code>BukgetPlugin</code> is a class used for storing and retrieving a plugin's information
 * @author Kezz101
 */
public class BukgetPlugin {
    private Map<Bukget.Field, String> values;

    private BukgetPlugin(Map<Bukget.Field, String> values) {
        this.values = values;
    }

    /**
     * Returns the value of a {@link Bukget.Field}. <br />
     * You may need to format/parse this into a {@link java.util.Date} or {@link URL}
     * @param field the field to retrieve
     * @return the value or <code>null</code> if not found
     */
    public String getFieldValue(Bukget.Field field) {
        return values.get(field);
    }

    /*
     * START STATIC METHODS
     */

    /**
     * Gets the information for a plugin by it's slug
     * @param slug the plugin's slug
     * @param version the {@link Bukget.Version} of download files you want
     * @param toReturn a list of {@link Bukget.Field}'s that the <code>BukgetPlugin</code> will contain.
     * @return the <code>BukgetPlugin</code> or <code>null</code> if the plugin doesn't exist or if you didn't enter any {@link Bukget.Field}s
     * @throws IOException if an error occurred during reading of the api
     */
    public static BukgetPlugin getPluginFromSlug(String slug, Bukget.Version version, Bukget.Field... toReturn) throws IOException {
        if(toReturn == null)
            return null;

        JsonObject jsonObject = new JsonParser().parse(WebHelper.readURLToString(new URL("http://api.bukget.org/3/plugins/bukkit/" + slug + "/" + version + "?size=1&fields=" + Utils.join(toReturn, ",")))).getAsJsonObject();

        if(jsonObject.isJsonNull() || jsonObject.getAsJsonArray("versions").isJsonNull())
            return null;

        Map<Bukget.Field, String> values = new HashMap<>();

        JsonObject versions = jsonObject.getAsJsonArray("versions").get(0).getAsJsonObject(), popularity = jsonObject.getAsJsonObject("popularity");

        for(Bukget.Field field : toReturn)
            switch (field) {
                case CATEGORIES:
                    values.put(field, WebHelper.joinJsonArray(jsonObject.getAsJsonArray("categories"), ", "));
                    break;
                case AUTHORS:
                    values.put(field, WebHelper.joinJsonArray(jsonObject.getAsJsonArray("authors"), ", "));
                    break;
                case WEBSITE:
                    values.put(field, jsonObject.get("website").getAsString().replace("\\", ""));
                    break;
                case BUKKIT_DEV_PAGE:
                    values.put(field, jsonObject.get("dbo_page").getAsString().replace("\\", ""));
                    break;
                case VERSION:
                    values.put(field, versions.get("version").getAsString());
                    break;
                case FILE_LINK:
                    values.put(field, versions.get("link").getAsString().replace("\\", ""));
                    break;
                case DOWNLOAD_LINK:
                    values.put(field, versions.get("downloads").getAsString().replace("\\", ""));
                    break;
                case BUKKIT_VERSION:
                    values.put(field, WebHelper.joinJsonArray(versions.getAsJsonArray("game_versions"), ", "));
                    break;
                case CHANGELOG:
                    values.put(field, versions.get("changelog").getAsString());
                    break;
                case DATE_RELEASED:
                    values.put(field, versions.get("date").getAsString());
                    break;
                case VERSION_SLUG:
                    values.put(field, versions.get("slug").getAsString());
                    break;
                case HARD_DEPENDENCIES:
                    if(versions.getAsJsonArray("hard_dependencies").isJsonNull())
                        break;
                    values.put(field, WebHelper.joinJsonArray(versions.getAsJsonArray("hard_dependencies"), ", "));
                    break;
                case SOFT_DEPENDENCIES:
                    if(versions.getAsJsonArray("soft_dependencies").isJsonNull())
                        break;
                    values.put(field, WebHelper.joinJsonArray(versions.getAsJsonArray("soft_dependencies"), ", "));
                    break;
                case POPULARITY_DAILY:
                    values.put(field, popularity.get("daily").getAsString());
                    break;
                case POPULARITY_WEEKLY:
                    values.put(field, popularity.get("weekly").getAsString());
                    break;
                case POPULARITY_MONTHLY:
                    values.put(field, popularity.get("monthly").getAsString());
                    break;
                default:
                    values.put(field, jsonObject.get(field.toString()).getAsString());
                    break;
            }

        return new BukgetPlugin(values);
    }

    /**
     * Gets the information for a plugin by it's ID in a {@link BukgetPluginList}
     * @param list the list
     * @param id the list id
     * @param version the {@link Bukget.Version} of download files you want
     * param toReturn a list of {@link Bukget.Field}'s that the <code>BukgetPlugin</code> will contain.
     * @return the <code>BukgetPlugin</code> or <code>null</code> if the plugin doesn't exist or if you didn't enter any {@link Bukget.Field}s
     * @throws IOException if an error occurred during reading of the api
     */
    public static BukgetPlugin getPluginFromList(BukgetPluginList list, int id, Bukget.Version version, Bukget.Field... toReturn) throws IOException {
        if(list.getSlugFromID(id) == null || toReturn == null)
            return null;

        return getPluginFromSlug(list.getSlugFromID(id), version, toReturn);
    }

}
