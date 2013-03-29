package com.commandsex.api;

import com.commandsex.CommandsEX;

/**
 * Interface for functions that require code run when the plugin is reloaded
 */
public interface ReloadJob {

    /**
     * Method to be run when the plugin is reloaded
     *
     * @param commandsEX The CommandsEX class instance
     */
    public void onReload(CommandsEX commandsEX);

}
