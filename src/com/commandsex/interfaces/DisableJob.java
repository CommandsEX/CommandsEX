package com.commandsex.interfaces;

/**
 * Interface for functions that require code run when the plugin is disabled
 */
public interface DisableJob {

    /**
     * Method to be run when the plugin is disabled
     */
    public void onDisable();

}
