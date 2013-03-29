package com.commandsex.api.interfaces;

import org.bukkit.configuration.file.FileConfiguration;

import com.commandsex.CommandsEX;

public interface Init extends Event {

    public void init(CommandsEX cex, FileConfiguration config);
    
}
