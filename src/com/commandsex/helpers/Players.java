package com.commandsex.helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.MinecraftServer;
import net.minecraft.server.v1_5_R2.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.Player;

import com.commandsex.CommandsEX;

public class Players {

    public static HashMap<String, Long> lastCommandUsage = new HashMap<String, Long>();
    
    /**
     * Gets a player
     *
     * If the arg begins with x-, only a player by that exact name will be returned
     * If the arg begins with -, this will search for an online player by that exact name, if that is not found
     * then it will get an offline player by that exact name
     *
     * @param arg The players name to get
     * @return The player
     */
    public static Player getPlayer(String arg){
        if (arg.startsWith("x-")){
            String name = arg.replaceFirst("x-", "");
            Player player = Bukkit.getPlayerExact(name);
            
            return player;
        } else if (arg.startsWith("-")){
            String name = arg.replaceFirst("-", "");
            Player player = Bukkit.getPlayerExact(name);

            if (player == null){
                player = getOfflinePlayer(name);
            }

            return player;
        } else {
            Player player = Bukkit.getPlayer(arg);

            if (player == null){
                for (Player p : Bukkit.getOnlinePlayers()){
                    if (p.getName().contains(arg)){
                        return p;
                    }
                }
            }

            return player;
        }
    }

    /**
     * Allows you to use functions for an offline name like they where an online name
     * By creating a fake entity
     *
     * This method will break with every single Minecraft update thanks to a recent commit
     * All version numbers in packages will have to be changed.
     *
     * @param name The offline player to get
     * @return The offline player, as an online player
     */
    public static Player getOfflinePlayer(String name) {
        Player player = Bukkit.getPlayerExact(name);

        if (player == null){
            try {
                File playerFolder = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "players");
                for (File playerFile : playerFolder.listFiles()) {
                    String filename = playerFile.getName();
                    String playerName = filename.substring(0, filename.length() - 4);

                    if (playerName.trim().equalsIgnoreCase(name)) {
                        final MinecraftServer server = ((CraftServer) CommandsEX.plugin.getServer()).getServer();
                        final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), playerName, new PlayerInteractManager(server.getWorldServer(0)));
                        player = (entity == null) ? null : (Player) entity.getBukkitEntity();
                        if (player != null) {
                            player.loadData();
                        } else {
                            return null;
                        }
                    }
                }
            } catch (final Exception e) {}
        }

        return player;
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
