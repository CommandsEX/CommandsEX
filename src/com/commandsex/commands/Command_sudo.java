package com.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.Command;

/**
 * Sudo, runs a command as the console
 * @author Kezz101
 */
@Builder(name = "sudo", description = "Runs a command as the console", type = "COMMAND")
@Cmd(command = "sudo", description = "Runs a command as the console", usage = "%c% <command>")
public class Command_sudo implements Command {

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(!Players.checkIsPlayer(sender))
            return true;
        
        if(args == null || args.length == 0)
            return false;
        
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), args[0].startsWith("/") ? Utils.join(args, " ").substring(1) : Utils.join(args, " "));
        sender.sendMessage(Language.getTranslationForSender(sender, "sudoExecuted"));
        return true;
    }

}
