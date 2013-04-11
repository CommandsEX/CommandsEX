package com.commandsex.interfaces;

import org.bukkit.command.CommandSender;

public interface Command {

    public boolean run(CommandSender sender, String[] args, String alias);
    
}
