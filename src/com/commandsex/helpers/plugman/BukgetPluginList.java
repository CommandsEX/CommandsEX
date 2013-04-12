package com.commandsex.helpers.plugman;

import com.commandsex.helpers.WebHelper;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BukgetPluginList {
    private String[][] arr;

    private BukgetPluginList(String[][] plugins) {
        this.arr = plugins;
    }

    public Map<Integer, String> getIDNameMap() {
        Map<Integer, String> returnMe = new HashMap<>();
        return null;
    }

    public String getDescriptionFromID(int id) {
        return null;
    }

    public String getSlugFromID(int id) {
        return null;
    }

    /*
     * START STATIC METHODS
     */

    public static BukgetPluginList getFromQuery(int results, Bukget.Field field, Bukget.SearchAction action, String value, int amount, Bukget.Field toReturn) throws IOException {
        return null;
    }

}
