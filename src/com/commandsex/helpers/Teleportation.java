package com.commandsex.helpers;

import com.commandsex.CommandsEX;
import com.commandsex.Language;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to manage teleportation functions
 */
public class Teleportation implements EnableJob {

    /*private static final HashMap<String, String> tpaRequests = new HashMap<String, String>();
    private static final HashMap<String, String> tpaHereRequests = new HashMap<String, String>();*/

    private static final List<String> requests = new ArrayList<String>();

    /**
     * Creates a new tpa request for the player
     * @param to The player to send the tpa request to
     * @param from The player that sent the tpa request
     */
    public static void newTpaRequest(final String to, final String from){
        final String request = ("TPA#####" + to + "#####" + from).toLowerCase();
        requests.add(request);

        Bukkit.getScheduler().runTaskLater(CommandsEX.plugin, new Runnable() {
            @Override
            public void run() {
                if (requests.contains(request)){
                    Player toPlayer = Bukkit.getPlayerExact(to);

                    if (toPlayer != null){
                        toPlayer.sendMessage(Language.getTranslationForSender(toPlayer, "requestTimedOut", from));
                    }

                    requests.remove(request);
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
        final String request = ("TPA-HERE#####" + to + "#####" + from).toLowerCase();
        requests.add(request);

        Bukkit.getScheduler().runTaskLater(CommandsEX.plugin, new Runnable() {
            @Override
            public void run() {
                if (requests.contains(request)){
                    Player toPlayer = Bukkit.getPlayerExact(to);

                    if (toPlayer != null){
                        toPlayer.sendMessage(Language.getTranslationForSender(toPlayer, "requestTimedOut", from));
                    }

                    requests.remove(request);
                }
            }
        }, 20L * 60 * CommandsEX.config.getInt("tp.tpaTimeoutMins"));
    }

    /**
     * Checks if a player has a pending tpa request to accept
     * @param to The player the request was sent to
     * @param from The player who sent the request
     * @return Is there a tpa here request matching the players
     */
    public static boolean hasTpaRequest(String to, String from){
        for (String request : requests){
            String[] parts = request.split("#####");
            if (parts[0].equalsIgnoreCase("TPA") && parts[1].equalsIgnoreCase(to) && parts[2].equalsIgnoreCase(from)){
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a player has a pending tpa here request to accept
     * @param to The player the request was sent to
     * @param from The player who sent the request
     * @return Is there a tpa here request matching the players
     */
    public static boolean hasTpaHereRequest(String to, String from){
        for (String request : requests){
            String[] parts = request.split("#####");
            if (parts[0].equalsIgnoreCase("TPA-HERE") && parts[1].equalsIgnoreCase(to) && parts[2].equalsIgnoreCase(from)){
                return true;
            }
        }

        return false;
    }

    /**
     * Removes a pending tpa request
     * @param to The player that was sent the tpa request
     * @param from The player who sent the tpa request
     */
    public static void removeTpaRequest(String to, String from){
        requests.remove("tpa#####" + to.toLowerCase() + "#####" + from.toLowerCase());
    }

    /**
     * Removes a pending tpa here request
     * @param to The player that was sent the tpa here request
     * @param from The player who sent the tpa here request
     */
    public static void removeTpaHereRequest(String to, String from){
        requests.remove("tpa-here#####" + to.toLowerCase() + "#####" + from.toLowerCase());
    }

    @Override
    public void onEnable(PluginManager pluginManager) {
        FileConfiguration config = CommandsEX.config;
        config.addDefault("tp.tpaTimeoutMins", 5);
    }
}
