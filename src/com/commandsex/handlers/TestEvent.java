package com.commandsex.handlers;


import com.commandsex.CommandsEX;
import com.commandsex.api.interfaces.Event;
import com.commandsex.api.interfaces.Init;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TestEvent implements Event, Init {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e){
        e.setMessage(ChatColor.GOLD + e.getMessage());
    }

    @Override
    public void init(CommandsEX cex, FileConfiguration config) {
        
    }
    
}
