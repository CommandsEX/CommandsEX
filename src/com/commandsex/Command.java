package com.commandsex;

import java.util.List;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command extends org.bukkit.command.Command {

    private CommandExecutor exe = null;

    /**
     * Constructs a new command
     * @param name The name of the command
     * @param description The description of the command
     * @param usageMessage The usage message that should be shown if the command returns false
     * @param aliases List of strings to be used as aliases to the command
     */
    public Command(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage.trim(), aliases);
    }

    /**
     * Function used by Bukkit, not to be used by anything else
     */
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(exe != null){
            boolean returnValue = exe.onCommand(sender, this, commandLabel, args);
            if (!returnValue){
                sender.sendMessage(Language.getTranslationForSender(sender, "invalidUsage", getUsage().replaceAll("%c%", "/" + commandLabel)));
            }

            return returnValue;
        }
        
        return false;
    }

    /**
     * Sets the {@link CommandExecutor} that should handle this command
     * @param exe The instance of the executor to run this command
     */
    public void setExecutor(CommandExecutor exe){
        this.exe = exe;
    }
    
}
