package com.commandsex.commands;

import com.commandsex.CommandsEX;
import com.commandsex.api.annotations.Cmd;
import com.commandsex.api.interfaces.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

@Cmd(command = "cex", description = "Displays information about CommandsEX", aliases = "cex_about, cex_info")
public class Command_cex implements Command {

    public boolean run(CommandSender sender, String[] args, String alias) {
        sender.sendMessage(ChatColor.AQUA + "Test :D");
        return false;
    }

}
