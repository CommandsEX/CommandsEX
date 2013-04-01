package com.commandsex.api.addons;

import com.commandsex.helpers.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all add-ons for CommandsEX
 */
public class AddOnManager {

    private List<AddOn> addOns = new ArrayList<AddOn>();
    private static boolean initialized = false;

    /**
     * Constructs a new {@link AddOnManager}, this should only be used by {@link com.commandsex.AddOnLoader}
     */
    public AddOnManager(){
        if (initialized){
            LogHelper.logSevere("AddOn Manager has already been initialized");
            return;
        }

        initialized = true;
    }

    /**
     * Registers a new add-on, this should only be used by {@link com.commandsex.AddOnLoader}
     * @param addOn The add-on to register
     */
    public void registerAddOn(AddOn addOn){
        addOns.add(addOn);
    }

    /**
     * Gets all available add-ons
     * @return A list of all add-ons
     */
    public List<AddOn> getAddOns(){
        return addOns;
    }

    /**
     * Checks if an {@link AddOn} is enabled
     * @param name The {@link AddOn} to find
     * @return Is the {@link AddOn} enabled?
     */
    public boolean isAddonEnabled(String name){
        return getAddOn(name) != null;
    }

    /**
     * Gets an {@link AddOn} by its name
     * @param name The {@link AddOn} to get
     * @return The {@link AddOn}, null if not found
     */
    public AddOn getAddOn(String name){
        for (AddOn addOn : addOns){
            if (addOn.getName().equalsIgnoreCase(name)){
                return addOn;
            }
        }

        return null;
    }
}
