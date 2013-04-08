package com.commandsex.helpers;

import com.commandsex.CommandsEX;
import com.commandsex.Language;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;

/**
 * Helper class to manage teleportation functions
 */
public class Teleportation implements EnableJob {

    private static final HashMap<String, String> tpaRequests = new HashMap<String, String>();
    private static final HashMap<String, String> tpaHereRequests = new HashMap<String, String>();

    /**
     * Creates a new tpa request for the player
     * @param to The player to send the tpa request to
     * @param from The player that sent the tpa request
     */
    public static void newTpaRequest(final String to, final String from){
        tpaRequests.put(to, from);

        Bukkit.getScheduler().runTaskLater(CommandsEX.plugin, new Runnable() {
            @Override
            public void run() {
                if (tpaRequests.containsKey(to)){
                    if (tpaRequests.get(to).equalsIgnoreCase(from)){
                        Player toPlayer = Bukkit.getPlayerExact(to);

                        if (toPlayer != null){
                            toPlayer.sendMessage(Language.getTranslationForSender(toPlayer, "requestTimedOut", from));
                        }

                        tpaRequests.remove(to);
                    }
                }
            }
        }, 20L * 60 * CommandsEX.config.getInt("tp.tpaTimeoutMins"));
    }

    /**
     * Creates a new tpa here request for the player
     * @param to The player to send the tpa here request to
     * @param from The player that sent the tpa here request
     */
    public static void newTpaHereRequests(final String to, final String from){
        tpaHereRequests.put(to, from);

        Bukkit.getScheduler().runTaskLater(CommandsEX.plugin, new Runnable() {
            @Override
            public void run() {
                if (tpaRequests.containsKey(to)){
                    if (tpaRequests.get(to).equalsIgnoreCase(from)){
                        Player toPlayer = Bukkit.getPlayerExact(to);

                        if (toPlayer != null){
                            toPlayer.sendMessage(Language.getTranslationForSender(toPlayer, "requestTimedOut", from));
                        }

                        tpaRequests.remove(to);
                    }
                }
            }
        }, 20L * 60 * CommandsEX.config.getInt("tp.tpaTimeoutMins"));
    }

    /**
     * Checks if a player has a pending tpa request to accept
     * @param player The player to check for a pending tpa request
     * @return Does the player have a pending tpa request
     */
    public static boolean hasTpaRequest(String player){
        return tpaRequests.containsKey(player);
    }

    /**
     * Checks if a player has a pending tpa here request to accept
     * @param player The player to check for a pending tpa here request
     * @return Does the player have a pending tpa here request
     */
    public static boolean hasTpaHereRequest(String player){
        return tpaHereRequests.containsKey(player);
    }

    /**
     * Removes a pending tpa request
     * @param to The player that was sent the tpa request
     */
    public static void removeTpaRequest(String to){
        tpaRequests.remove(to);
    }

    /**
     * Removes a pending tpa here request
     * @param to The player that was sent the tpa here request
     */
    public static void removeTpaHereRequest(String to){
        tpaHereRequests.remove(to);
    }

    /**
     * Gets the sender of a tpa request
     * @param to The player the tpa request was sent to
     * @return The sender of the tpa request
     */
    public static String getTpaRequest(String to){
        return tpaRequests.get(to);
    }

    /**
     * Gets the sender of a tpa here request
     * @param to The player the tpa here request was sent to
     * @return The sender of the tpa here request
     */
    public static String getTpaHereRequest(String to){
        return tpaHereRequests.get(to);
    }

    @Override
    public void onEnable(PluginManager pluginManager) {
        FileConfiguration config = CommandsEX.config;
        config.addDefault("tp.tpaTimeoutMins", 5);
    }
}
