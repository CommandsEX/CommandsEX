package com.commandsex.handlers;

import com.commandsex.annotations.Builder;
import com.commandsex.helpers.Utils;
import com.commandsex.helpers.WebHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.net.MalformedURLException;
import java.net.URL;

@Builder(name = "Auto URL Shortener", description = "Automaticlly shortens URLs chatted", type = "EVENT")
public class AutoURLShortener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String[] split = e.getMessage().split(" ");
        for(int i = 0; i < split.length; i++) {
            try {
                new URL(!split[i].toLowerCase().startsWith("http://") || !split[i].startsWith("https://") ? "http://" + split[i] : split[i]);
            } catch(MalformedURLException err) {
                break;
            }
            split[i] = WebHelper.shortenURL(split[i]);
        }
        e.setMessage(Utils.join(split, " "));
    }

}
