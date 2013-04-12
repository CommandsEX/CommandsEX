package com.commandsex.helpers.plugman;

import com.commandsex.helpers.WebHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Class used for storing and retrieving a list of plugins from a search query
 * @author Kezz101
 */
public class BukgetPluginList {
    private String[][] arr;// {id, {slug, plugin_name, description}}
    private TreeMap<Integer, String> idNameMap = null;

    private BukgetPluginList(String[][] plugins) {
        this.arr = plugins;
    }

    /**
     * Gets a map in the following format <code>{ID, PluginName}</code>.<br />
     * It returns a {@link TreeMap} so that it can be displayed in ID order
     * @return the map
     */
    public TreeMap<Integer, String> getIDNameMap() {
        if(idNameMap != null)
            return idNameMap;

        TreeMap<Integer, String> returnMe = new TreeMap<>();
        for(int i = 0; i < arr.length; i++)
            returnMe.put(i, arr[i][1]);

        idNameMap = returnMe;
        return returnMe;
    }

    /**
     * Gets the description of a plugin from it's id
     * @param id the id
     * @return the description or <code>null</code> if the plugin is not in the map
     */
    public String getDescriptionFromID(int id) {
        try {
            return arr[id][2];
        } catch(ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Gets the slug of a plugin from it's id
     * @param id the id
     * @return the slug or <code>null</code> if the plugin is not in the map
     */
    public String getSlugFromID(int id) {
        try {
            return arr[id][0];
        } catch(ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /*
     * START STATIC METHODS
     */

    /**
     * Returns a List of plugins from a fairly advanced query. <br />
     * The first three parameters are a logic statement for finding plugins.
     * Here is an example of the code: <br/>
     * <code>Field.CATEGORY = "Fun"</code> would return plugins where the category equals fun <br/>
     * or <br />
     * <code>Field.NAME like "Commands"</code> would return plugins where the name is like 'Commands' e.g CommandsEX
     * @param field the field
     * @param action the action
     * @param value the value
     * @param sortBy how you want the results to be sorted. <b>Note:</b> this field can be negated to do an inverse sort
     * @return the sorted list of plugins
     * @throws IOException if an error occurs during the reading of the api
     */
    public static BukgetPluginList getFromQuery(Bukget.Field field, Bukget.SearchAction action, String value, Bukget.Field sortBy) throws IOException {
        JsonElement jsonElement = new JsonParser().parse(WebHelper.readURLToString(new URL("http://api.bukget.org/3/search/" + field + "/" + action + "/" + value + "?fields=" + Bukget.Field.SLUG + "," + Bukget.Field.PLUGIN_NAME + "," + Bukget.Field.DESCRIPTION + "&sort=" + sortBy)));

        if(jsonElement.isJsonNull() || !jsonElement.isJsonArray())
            return null;

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Iterator<JsonElement> it = jsonArray.iterator();
        String[][] arr = new String[][]{};
        int id = 0;
        while(it.hasNext()) {
            JsonObject jsonObject = it.next().getAsJsonObject();
            arr[id] = new String[] {jsonObject.get("slug").getAsString(), jsonObject.get("plugin_name").getAsString(), jsonObject.get("description").getAsString()};
            it.remove();
            id++;
        }

        return new BukgetPluginList(arr);
    }

}
