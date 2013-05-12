package com.commandsex.handlers;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.helpers.Utils;
import com.commandsex.helpers.WebHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.net.MalformedURLException;
import java.net.URL;

@Builder(name = "Auto URL Shortener", description = "Automatically shortens URLs used in chat", type = "EVENT")
public class AutoURLShortener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        String[] split = message.split(" ");

        if (Utils.containsUrl(message)){
            Player player = e.getPlayer();
            player.sendMessage(Language.getTranslationForSender(player, "shortening"));

            for (int i = 0; i < split.length; i++) {
                try {
                    new URL(!split[i].toLowerCase().startsWith("http://") && !split[i].startsWith("https://") ? "http://" + split[i] : split[i]);
                } catch(MalformedURLException err) {
                    continue;
                }

                String shortenedUrl = WebHelper.shortenURL(split[i]);
                if (shortenedUrl != null){
                    split[i] = shortenedUrl;
                }
            }
        }

        e.setMessage(Utils.join(split, " "));
    }

}
