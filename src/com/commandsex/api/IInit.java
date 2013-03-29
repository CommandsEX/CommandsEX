package com.commandsex.api;

import org.bukkit.configuration.file.FileConfiguration;

import com.commandsex.CommandsEX;

public interface IInit extends IEvent {

    public void init(CommandsEX cex, FileConfiguration config);
    
}
