package com.commandsex.helpers;

import com.commandsex.api.interfaces.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.commandsex.CommandsEX;

public class CommandForwarder implements CommandExecutor {

    /**
     * This method acts as a command forwarder, this class forwards the command to the applicable class
     */
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String cmdAlias, String[] args) {
        String c = cmd.getName().toLowerCase();
        cmdAlias = cmdAlias.toLowerCase();
        
        try {
            Class<?> clazz = Class.forName("com.commandsex.commands.Command_" + c);
            Object instance = clazz.newInstance();
            Command iCmd = (Command) instance;
            return iCmd.run(sender, args, cmdAlias, CommandsEX.plugin, CommandsEX.plugin.getConfig());
        } catch (Throwable e){
            e.printStackTrace();
        }

        return true;
    }

}
