package com.commandsex.helpers.plugman;

import com.commandsex.Libraries;
import com.commandsex.interfaces.EnableJob;

public class PluginManager implements EnableJob {

    public void onEnable(org.bukkit.plugin.PluginManager pluginManager) {
        Libraries.registerLibrary("gson-2.2.2", "http://www.commandsex.com/downloads/dependencies/gson-2.2.2.jar");
    }
}
