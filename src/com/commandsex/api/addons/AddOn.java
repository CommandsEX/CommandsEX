package com.commandsex.api.addons;

import com.commandsex.helpers.ClasspathHacker;
import com.commandsex.helpers.LogHelper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.logging.Logger;

/**
 * Class in which AddOns should have their main class extend
 */
public class AddOn {

    private boolean initialized = false;
    private AddOnDescriptionFile addOnDescriptionFile;
    private Logger logger;
    private File dataFolder;
    private File file;

    /**
     * Constructs a new {@link AddOn}
     * @param addOnDescriptionFile The add-on description file to use
     * @param dataFolder The data folder in which the add-on will store its files (if any)
     * @param file The location of the Add-on jar file
     */
    public AddOn(AddOnDescriptionFile addOnDescriptionFile, File dataFolder, File file) {
        this.addOnDescriptionFile = addOnDescriptionFile;
        this.dataFolder = dataFolder;
        this.file = file;
        this.logger = new AddOnLogger(this);

        try {
            ClasspathHacker.addFile(file);
            Class<?> mainClass = Class.forName(addOnDescriptionFile.getMain(), true, ClassLoader.getSystemClassLoader());
            Class<? extends AddOn> addon = mainClass.asSubclass(AddOn.class);

            Constructor<? extends AddOn> constructor = addon.getConstructor(AddOnDescriptionFile.class, File.class, File.class);
            constructor.newInstance(addOnDescriptionFile, dataFolder, file);
        } catch (IOException e) {
            LogHelper.logSevere("Error while enabling " + getName());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            LogHelper.logSevere("Invalid main class for addon " + getName());
            e.printStackTrace();
        } catch (Throwable throwable){
            LogHelper.logSevere("Error while enabling add-on " + getName());
            throwable.printStackTrace();
        }
    }

    /**
     * Called when the add-on is enabled
     */
    public void onEnable(){}

    /**
     * Called when the add-on is disabled
     */
    public void onDisable(){}

    /**
     * Gets the name of the add-on, this is defined from {@link AddOnDescriptionFile}
     * @return The name of the add-on
     */
    public String getName(){
        return getAddOnDescriptionFile().getName();
    }

    /**
     * Gets the {@link AddOnDescriptionFile} containing info about the add-on
     * @return The add-on description file
     */
    public AddOnDescriptionFile getAddOnDescriptionFile(){
        return addOnDescriptionFile;
    }

    /**
     * Gets the {@link AddOnLogger} for this plugin to use, this is actually a modified {@link Logger}
     * The modified version is used to prefix logged message with CommandsEX-(AddOn Name)
     * @return The {@link Logger} to use
     */
    public Logger getLogger(){
        return logger;
    }

}
