package com.commandsex;

import com.commandsex.api.addons.Addon;
import com.commandsex.api.addons.AddonDescriptionFile;
import com.commandsex.api.interfaces.EnableJob;
import com.commandsex.helpers.ClasspathHacker;
import com.commandsex.helpers.LogHelper;
import org.bukkit.plugin.PluginManager;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonLoader implements EnableJob {

    private File addonFolder = new File(CommandsEX.plugin.getDataFolder(), "addons");

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
                        AddonDescriptionFile descriptionFile = new AddonDescriptionFile(inputStream);
                        File dataFolder = new File(file.getParentFile(), descriptionFile.getName());
                        ClasspathHacker.addFile(file);
                        Class<?> mainClass = Class.forName(descriptionFile.getMain(), true, ClassLoader.getSystemClassLoader());
                        Class<? extends Addon> addon = mainClass.asSubclass(Addon.class);

                        Constructor<? extends Addon> constructor = addon.getConstructor(AddonDescriptionFile.class, File.class, File.class);
                        constructor.newInstance(descriptionFile, dataFolder, file);
                    } catch (IOException e) {
                        LogHelper.logSevere("Error while enabling " + fName);
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        LogHelper.logSevere("Invalid main class for addon " + fName);
                        e.printStackTrace();
                    } catch (Throwable throwable){
                        LogHelper.logSevere("Error while enabling add-on " + fName);
                        throwable.printStackTrace();
                    }
                }
            }
        }
    }
}
