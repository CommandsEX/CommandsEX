package com.commandsex.api.interfaces;

/**
 * Classes which need to run code when the plugin is enabled must implement this class
 */
public interface EnableJob {

    /**
     * The method to be run when the plugin is enabled
     */
    public void onEnable();

}
