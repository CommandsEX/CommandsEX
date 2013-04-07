package com.commandsex;

import java.util.List;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command extends org.bukkit.command.Command {

    private CommandExecutor exe = null;
    
    public Command(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage.trim().replaceAll("%c%", "/" + name), aliases);
    }

    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(exe != null){
            return exe.onCommand(sender, this, commandLabel,args);
        }
        
        return false;
    }
   
    public void setExecutor(CommandExecutor exe){
        this.exe = exe;
    }
    
}
