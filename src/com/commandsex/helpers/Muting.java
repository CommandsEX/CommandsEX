package com.commandsex.helpers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.commandsex.CommandsEX;
import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.interfaces.EnableJob;

/**
 * Muting, manages all server muting functions
 * @author Kieran
 */
@Builder(name = "Muting", type = "PACKAGE", depends = "commands/Command_mute, commands/Command_unmute")
public class Muting implements EnableJob, Listener {
    public static final Permission MUTE_OP = new Permission("cex.mute.useOnOp", PermissionDefault.OP);
    private static Set<String> muted = new HashSet<>();
    
    @Override
    public void onEnable(PluginManager plugin) {
        plugin.addPermission(MUTE_OP);
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if(muted.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Language.getTranslationForSender(e.getPlayer(), "muted"));
        }
    }
    
    public static boolean isMuted(String playerName) {
        return muted.contains(playerName);
    }
    
    public static void mute(String playername) {
        muted.add(playername);
    }
    
    public static void mute(String playername, int minutes) {
        mute(playername);
        Bukkit.getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new MuteRunnable(playername), minutes * 60 * 20);
    }
    
    public static void unmute(String playername) {
        muted.remove(playername);
    }
    
}

class MuteRunnable implements Runnable {
    private String name;
    
    public MuteRunnable(String playerName) {
        this.name = playerName;
    }

    @Override
    public void run() {
        if(Muting.isMuted(name)) {
            Player p = Players.getPlayer(name);
            if(p != null)
                p.sendMessage(Language.getTranslationForSender(p, "unmuted"));
            Muting.unmute(p.getName());
        }
    }
    
}
