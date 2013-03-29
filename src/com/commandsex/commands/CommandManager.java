package com.commandsex.commands;

/* BREAKABLE IMPORTS */
import com.commandsex.CommandsEX;
import com.commandsex.HackedCommand;
import com.commandsex.api.interfaces.Command;
import com.commandsex.helpers.CommandForwarder;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.reflections.Reflections;

import com.commandsex.helpers.LogHelper;
import com.commandsex.helpers.Utils;

public class CommandManager {
    public static CommandMap cmap = null;

    /**
     * The constructor
     */
    public CommandManager(){
        try {
            Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            cmap = (CommandMap) f.get(Bukkit.getServer());
        } catch (Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * Registers all CommandsEX commands
     * @return Number of commands successfully registered
     */
    public int registerCommands(){
        int ret = 0;
        Reflections reflections = new Reflections("com.commandsex.commands");
        Set<Class<? extends Command>> commandClasses = reflections.getSubTypesOf(Command.class);

        for (Class<?> clazz : commandClasses){
            Annotation anno = clazz.getAnnotation(com.commandsex.api.annotations.Command.class);
            
            if (anno != null){
                com.commandsex.api.annotations.Command aCmd = (com.commandsex.api.annotations.Command) anno;
                
                List<String> aliases = new ArrayList<String>();
                aliases.add("cex_" + aCmd.command());
                
                if (!aCmd.aliases().equals("")){
                    aliases.addAll(Utils.separateCommaList(aCmd.aliases()));
                }
                
                HackedCommand hackCmd = new HackedCommand(aCmd.command(), aCmd.description(), "/<command> " + aCmd.usage().trim(), aliases);
                cmap.register("", hackCmd);
                hackCmd.setExecutor(new CommandForwarder());
                
                try {
                    Command iCmd = (Command) clazz.newInstance();
                    iCmd.init(CommandsEX.plugin, CommandsEX.config);
                } catch (Exception e){
                    e.printStackTrace();
                    LogHelper.logSevere("Error while running init() method for class " + clazz.getName());
                }
                
                ret++;
            } else {
                LogHelper.logWarning("Error: class " + clazz.getName() + " does not have an Command annotation");
                LogHelper.logWarning("The command will not function due to this issue");
                LogHelper.logWarning("Please alert the CommandsEX developers to this issue");
            }
        }

        return ret;
    }
    
    /**
     * Checks if the command is available, has it been removed with the builder
     * @param command The command to check
     * @return Whether or not the command is available
     */
    public static boolean isCommandAvailable(String command){
        try {
            return Class.forName("com.commandsex.commands.Command_" + command) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
