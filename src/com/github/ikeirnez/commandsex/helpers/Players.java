package com.github.ikeirnez.commandsex.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.ikeirnez.commandsex.CommandsEX;

public class Players {

    public static HashMap<String, Long> lastCommandUsage = new HashMap<String, Long>();
    
    /**
     * Gets an online player
     * If the argument begins with x: then we will get the player by that exact name
     * @param arg The players name to get
     * @return The player
     */
    public static Player getPlayer(String arg){
        if (arg.startsWith("x:")){
            String name = arg.replaceFirst("x:", "");
            Player toReturn = Bukkit.getPlayerExact(name);
            
            if (toReturn == null){
                toReturn = Bukkit.getOfflinePlayer(name).getPlayer();
            }
            
            return toReturn;
        } else {
            return Bukkit.getPlayer(arg);
        }
    }
    
    public static boolean checkCommandSpam(CommandSender sender){
        if (!(sender instanceof Player)){
            return false;
        }
        
        return checkCommandSpam(sender.getName());
    }
    
    public static boolean checkCommandSpam(String player){
        // schedule an async task to check if all players in the lastCommandUsage HashMap are online
        // if a player is offline he will be removed from the HashMap
        Bukkit.getScheduler().runTaskLaterAsynchronously(CommandsEX.plugin, new Runnable() {
            public void run() {
                synchronized (lastCommandUsage){
                    List<String> toRemove = new ArrayList<String>();
                    
                    for (String player : lastCommandUsage.keySet()){
                        if (Bukkit.getPlayerExact(player) == null){
                            // add player to remove queue
                            toRemove.add(player);
                        }
                    }
                    
                    // remove players from the HashMap this way to prevent ConcurrentModificationExceptions
                    for (String s : toRemove){
                        lastCommandUsage.remove(s);
                    }
                    
                    toRemove.clear();
                }
            }
        }, 0L);
        
        if (lastCommandUsage.containsKey(player)){
            return (System.currentTimeMillis() - lastCommandUsage.get(player)) / 1000 > CommandsEX.config.getInt("commandCooldownSeconds");
        } else {
            return false;
        }
    }
}
