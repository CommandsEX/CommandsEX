package com.commandsex.api;

import com.commandsex.CommandsEX;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public interface ICommand {

    public void init(CommandsEX cex, FileConfiguration config);
    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex, FileConfiguration config);
    
}
