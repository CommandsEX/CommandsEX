package com.commandsex.api.interfaces;

import com.commandsex.CommandsEX;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public interface Command {

    public boolean run(CommandSender sender, String[] args, String alias);
    
}
