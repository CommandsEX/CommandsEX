package com.commandsex.api.addons;

import com.commandsex.helpers.LogHelper;
import com.commandsex.helpers.Utils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to easily load and retrieve options from an addon.yml file
 * Inspired by the Bukkit PluginDescriptionFile
 */
public class AddOnDescriptionFile {

    private static Yaml yaml = new Yaml(new SafeConstructor());
    private String name = null;
    private String main = null;
    private String description = null;
    private String version = null;
    private List<String> authors = new ArrayList<String>();

    /**
     * Constructs a new AddOnDescriptionFile
     * @param inputStream The addon.yml as an InputStream
     */
    public AddOnDescriptionFile(InputStream inputStream){
        loadMap(asMap(yaml.load(inputStream)));
    }

    /**
     * Gets the name of the add-on
     * @return The name of the add-on
     */
    public String getName(){
        return name;
    }

    /**
     * Gets the main class of the add-on
     * @return The main class of the add-on
     */
    public String getMain(){
        return main;
    }

    /**
     * Gets the add-ons description
     * @return The add-ons description
     */
    public String getDescription(){
        return description;
    }

    /**
     * Gets the add-ons version
     * @return The add-ons version
     */
    public String getVersion(){
        return getVersion();
    }

    /**
     * Gets the authors of the add-on
     * @return The authors of the add-on
     */
    public List<String> getAuthors(){
        return authors;
    }

    private void loadMap(Map<?, ?> map) {
        try {
            name = map.get("name").toString();
            if (Utils.containsInvalidCharacters(name)){
                LogHelper.logSevere("Invalid characters in addon.yml");
                return;
            }

            version = map.get("version").toString();
            main = map.get("main").toString();
            description = map.get("description").toString();

            if (map.get("authors") != null){
                if (map.get("author") != null){
                    authors.add(map.get("authors").toString());
                }

                for (Object object : (Iterable<?>) map.get("authors")){
                    authors.add(object.toString());
                }
            } else {

            }
        } catch (NullPointerException exception){
            exception.printStackTrace();
        } catch (ClassCastException exception){
            exception.printStackTrace();
        }
    }

    private Map<?, ?> asMap(Object object){
        if (object instanceof Map){
            return (Map<?, ?>) object;
        }

        LogHelper.logSevere(object + " is not properly structured.");
        return null;
    }

}
