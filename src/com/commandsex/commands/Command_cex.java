package com.commandsex.commands;

import com.commandsex.CommandsEX;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.commandsex.api.ACommand;
import com.commandsex.api.ICommand;

@ACommand(command = "cex", description = "Displays information about CommandsEX", aliases = "cex_about, cex_info")
public class Command_cex implements ICommand {

    public void init(CommandsEX cex, FileConfiguration config) {
        
    }

    public boolean run(CommandSender sender, String[] args, String alias, CommandsEX cex, FileConfiguration config) {
        sender.sendMessage(ChatColor.AQUA + "Test :D");
        return false;
    }

}
