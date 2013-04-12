package com.commandsex.helpers.plugman;

import com.commandsex.helpers.WebHelper;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BukgetPluginList {
    private String[][] arr;// {id, {slug, name, description}}
    private TreeMap<Integer, String> idNameMap = null;

    private BukgetPluginList(String[][] plugins) {
        this.arr = plugins;
    }

    public TreeMap<Integer, String> getIDNameMap() {
        if(idNameMap != null)
            return idNameMap;

        TreeMap<Integer, String> returnMe = new TreeMap<>();
        for(String[] single : arr) {

        }

        idNameMap = returnMe;
        return returnMe;
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

    public static BukgetPluginList getFromQuery(int results, Bukget.Field field, Bukget.SearchAction action, String value, int amount) throws IOException {
        return null;
    }

}
