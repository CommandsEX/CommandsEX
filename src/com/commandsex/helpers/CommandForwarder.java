package com.commandsex.helpers;

import com.commandsex.annotations.Cmd;
import com.commandsex.interfaces.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Class used to forward commands to the correct command class
 */
public class CommandForwarder implements CommandExecutor {

    /**
     * This method acts as a command forwarder, this class forwards the command to the applicable class
     */
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String cmdAlias, String[] args) {
        if (!Players.checkCommandSpam(sender)){
            Players.senderUseCommand(sender);
            String commandName = cmd.getName().toLowerCase().replaceFirst("cex_", "");

            try {
                Class<?> clazz = Class.forName("com.commandsex.commands.Command_" + commandName);
                Command command = (Command) clazz.newInstance();
                Cmd cmdAnno = clazz.getAnnotation(Cmd.class);

                if (Players.hasPermission(sender, cmdAnno.permission())){
                    return command.run(sender, args, cmdAlias);
                } else {
                    return true;
                }
            } catch (Throwable e){
                e.printStackTrace();
            }
        }

        return true;
    }

}
