package com.commandsex.helpers;

import java.io.File;
import java.util.HashMap;

import com.commandsex.Language;
import net.minecraft.server.v1_5_R2.EntityPlayer;
import net.minecraft.server.v1_5_R2.MinecraftServer;
import net.minecraft.server.v1_5_R2.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.entity.Player;

import com.commandsex.CommandsEX;
import org.bukkit.permissions.Permission;

public class Players {
    private static HashMap<String, Long> lastCommandUsage = new HashMap<String, Long>();

    /**
     * Gets a player, sends an error the the {@link CommandSender} if player isn't found
     *
     * If the arg begins with x-, only a player by that exact name will be returned
     * If the arg begins with -, this will search for an online player by that exact name, if that is not found
     * then it will get an offline player by that exact name
     *
     * @param arg The players name to get
     * @param commandSender The CommandSender to send the error to
     * @return The player
     */
    public static Player getPlayer(String arg, CommandSender commandSender){
        Player player = getPlayer(arg);

        if (player == null){
            commandSender.sendMessage(Language.getTranslationForSender(commandSender, "playerNotFound", arg));
        }

        return player;
    }

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
        arg = arg.toLowerCase();

        if (arg.startsWith("x-")){
            String name = arg.replaceFirst("x-", "");
            return Bukkit.getPlayerExact(name);
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
                    if (p.getName().toLowerCase().contains(arg)){
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
                        player = entity.getBukkitEntity();
                        if (player != null) {
                            player.loadData();
                        } else {
                            return null;
                        }
                    }
                }
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        return player;
    }

    /**
     * Checks if a commandSender is spamming
     * This method will send an error to the send if he/she is spamming
     * @param commandSender The commandSender to check for spamming
     * @return True if spamming, false if okay
     */
    public static boolean checkCommandSpam(CommandSender commandSender){
        String sName = commandSender.getName();

        if (!lastCommandUsage.containsKey(sName)){
            return false;
        }

        long time = System.currentTimeMillis() - lastCommandUsage.get(sName);
        boolean isSpamming = time / 1000 < CommandsEX.config.getInt("commandCooldownSeconds");

        if (isSpamming){
            commandSender.sendMessage(Language.getTranslationForSender(commandSender, "spamming", time / 1000.0));
        }

        return isSpamming;
    }

    /**
     * Executed when a {@link CommandSender} uses a command, this is used to count time for anti-spam purposes
     * @param sender The command sender that uses a command
     */
    public static void senderUseCommand(CommandSender sender){
        if (!sender.hasPermission("cex.commandspam.bypass")){
            lastCommandUsage.put(sender.getName(), System.currentTimeMillis());
        }
    }

    /**
     * Checks if a player has a permission and sends an error message if not
     * @param commandSender The CommandSender to check the permission for
     * @param permission The permission to check against the CommandSender
     * @return Does the player have the permission node?
     */
    public static boolean hasPermission(CommandSender commandSender, Permission permission){
        return hasPermission(commandSender, permission.getName());
    }

    /**
     * Checks if a player has a permission and sends an error message if not
     * @param commandSender The CommandSender to check the permission for
     * @param permission The permission to check against the CommandSender
     * @return Does the player have the permission node?
     */
    public static boolean hasPermission(CommandSender commandSender, String permission){
        if (commandSender.hasPermission(permission)){
            return true;
        } else {
            commandSender.sendMessage(Language.getTranslationForSender(commandSender, "noPermission", permission));
            return false;
        }
    }

    /**
     * Checks if a {@link CommandSender} is a player, if not, an error message will be sent
     * @param commandSender The CommandSender to check
     * @return Is the CommandSender a player
     */
    public static boolean checkIsPlayer(CommandSender commandSender){
        return checkIsPlayer(commandSender, true);
    }

    /**
     * Checks if a {@link CommandSender} is a player, if not, an error message will be sent
     * @param commandSender The CommandSender to check
     * @param sendWarning Should a warning be shown if their not a player
     * @return Is the CommandSender a player
     */
    public static boolean checkIsPlayer(CommandSender commandSender, boolean sendWarning){
        if (commandSender instanceof Player){
            return true;
        } else {
            if (sendWarning){
                commandSender.sendMessage(Language.getTranslationForSender(commandSender, "mustBePlayer"));
            }
            return false;
        }
    }
}
