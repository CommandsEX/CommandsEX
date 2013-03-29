package com.commandsex.handlers;


import com.commandsex.CommandsEX;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.commandsex.api.IEvent;
import com.commandsex.api.IInit;

public class TestEvent implements IEvent, IInit {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e){
        e.setMessage(ChatColor.GOLD + e.getMessage());
    }

    @Override
    public void init(CommandsEX cex, FileConfiguration config) {
        
    }
    
}
