package com.commandsex;

import com.commandsex.api.addons.AddOn;
import com.commandsex.api.addons.AddOnDescriptionFile;
import com.commandsex.api.addons.AddOnManager;
import com.commandsex.api.interfaces.EnableJob;
import com.commandsex.helpers.LogHelper;
import org.bukkit.plugin.PluginManager;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddOnLoader implements EnableJob {

    private File addonFolder = new File(CommandsEX.plugin.getDataFolder(), "addons");
    private AddOnManager addOnManager = new AddOnManager();

    public void onEnable(PluginManager pluginManager) {
        if (addonFolder.exists()){
            addonFolder.mkdir();
        }

        if (!addonFolder.isDirectory()){
            LogHelper.logSevere(addonFolder.getPath() + " isn't a directory, addons will fail to load");
            return;
        }

        for (File file : addonFolder.listFiles()){
            if (file.isFile()){
                String fName = file.getName();
                if (fName.endsWith(".jar")){
                    JarFile jarFile = null;
                    InputStream inputStream = null;

                    try {
                        jarFile = new JarFile(file);
                        JarEntry jarEntry = jarFile.getJarEntry("addon.yml");

                        if (jarEntry == null){
                            LogHelper.logSevere("Add-on JAR " + file.getName() + " doesn't contain an addon.yml");
                        }

                        inputStream = jarFile.getInputStream(jarEntry);
                        AddOnDescriptionFile descriptionFile = new AddOnDescriptionFile(inputStream);
                        File dataFolder = new File(file.getParentFile(), descriptionFile.getName());
                        addOnManager.registerAddOn(new AddOn(descriptionFile, dataFolder, file));
                    } catch (IOException e) {
                        LogHelper.logSevere("Error while enable AddOn " + fName);
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
