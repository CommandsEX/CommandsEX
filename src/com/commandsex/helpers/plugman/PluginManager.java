package com.commandsex.helpers.plugman;

import com.commandsex.Libraries;
import com.commandsex.helpers.plugman.BukgetPlugin;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PluginManager implements EnableJob {

    public void onEnable(org.bukkit.plugin.PluginManager pluginManager) {
        Libraries.registerLibrary("gson-2.2.2", "http://www.commandsex.com/downloads/dependencies/gson-2.2.2.jar");
    }

    /**
     * Searches for a plugin by a name, this is different to Bukkit's default getPlugin() method
     * @param name The name of the plugin to search for
     * @return The plugin, null if not found
     */
    public static Plugin getPlugin(String name){
        List<Plugin> matches = new ArrayList<>();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()){
            String pluginName = plugin.getName().replaceAll(" ", "");

            if (pluginName.equalsIgnoreCase(name)){
                return plugin;
            } else if (pluginName.contains(name)){
                matches.add(plugin);
            }
        }

        return matches.size() > 0 ? matches.get(0) : null;
    }
}
