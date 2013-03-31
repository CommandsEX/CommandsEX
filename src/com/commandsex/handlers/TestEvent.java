package com.commandsex.handlers;


import com.commandsex.api.interfaces.DisableJob;
import com.commandsex.api.interfaces.EnableJob;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;

public class TestEvent implements EnableJob, DisableJob, Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e){
        e.setMessage(ChatColor.GOLD + e.getMessage());
    }

    @Override
    public void onEnable(PluginManager pluginManager) {
        System.out.println("Test Enabling");
    }

    @Override
    public void onDisable() {
        System.out.println("Test Disabling");
    }
}
