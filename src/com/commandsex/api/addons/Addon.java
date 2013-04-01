package com.commandsex.api.addons;

import java.io.File;

public class Addon {

    private boolean initialized = false;
    private AddonDescriptionFile addonDescriptionFile;
    private File dataFolder;
    private File file;

    public Addon(AddonDescriptionFile addonDescriptionFile, File dataFolder, File file) {
        this.addonDescriptionFile = addonDescriptionFile;
        this.dataFolder = dataFolder;
        this.file = file;
    }

    public void onEnable(){}
    public void onDisable(){}

}
