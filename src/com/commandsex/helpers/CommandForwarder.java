package com.commandsex.helpers;

import com.commandsex.interfaces.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandForwarder implements CommandExecutor {

    public static HashMap<String, Long> lastCommandUsage = new HashMap<String, Long>();

    /**
     * This method acts as a command forwarder, this class forwards the command to the applicable class
     */
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String cmdAlias, String[] args) {
        if (!Players.checkCommandSpam(sender)){
            String commandName = cmd.getName().toLowerCase().replaceFirst("cex_", "");

            try {
                if (!sender.hasPermission("cex.commandspam.bypass")){
                    lastCommandUsage.put(sender.getName(), System.currentTimeMillis());
                }
                Class<?> clazz = Class.forName("com.commandsex.commands.Command_" + commandName);
                Command command = (Command) clazz.newInstance();
                return command.run(sender, args, cmdAlias);
            } catch (Throwable e){
                e.printStackTrace();
            }
        }

        return true;
    }

}
