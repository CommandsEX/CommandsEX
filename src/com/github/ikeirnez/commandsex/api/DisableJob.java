package com.github.ikeirnez.commandsex.api;

import com.github.ikeirnez.commandsex.CommandsEX;

/**
 * Interface for functions that require code run when the plugin is disabled
 */
public interface DisableJob {

    /**
     * Method to be run when the plugin is disabled
     *
     * @param commandsEX The CommandsEX class instance
     */
    public void onDisable(CommandsEX commandsEX);

}
