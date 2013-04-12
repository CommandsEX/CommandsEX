package com.commandsex.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;

/**
 * ChatAs, chats as another player
 * @author Kezz101
 */
@Builder(name = "chatas", description = "Chats as another player", type = "COMMAND")
@Cmd(command = "chatas", description = "Chats as another player", usage = "%c% <player> [message]")
public class Command_chatas implements Command, EnableJob, Listener {
    private Permission onOp = new Permission("cex.chatas.runOnOp");
    private Permission perm = new Permission("cex.chatas.permanent");
    private Map<String, String> chatting = new HashMap<>();
    
    @Override
    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(onOp);
        pluginManager.addPermission(perm);
    }

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(!Players.checkIsPlayer(sender))
            return true;
        
        if(args == null || args.length == 0)
            return false;
                
        Player target = Players.getPlayer(args[0], sender);
        
        if(target == null)
            return true;
        
        if(target.isOp() && !sender.hasPermission(onOp)) {
            sender.sendMessage(Language.getTranslationForSender(sender, "chatasNoPermission", target.getName()));
            return true;
        }
        
        if(args.length == 1) {
            if(!sender.hasPermission(perm)) {
                sender.sendMessage(Language.getTranslationForSender(sender, "chatasNoPermanent"));
            } else {
                if(chatting.containsKey(sender.getName())) {
                    sender.sendMessage(Language.getTranslationForSender(sender, "chatasAlreadyChatting", chatting.get(sender.getName())));
                } else {
                    chatting.put(sender.getName(), target.getName());
                    sender.sendMessage(Language.getTranslationForSender(sender, "chatasAnother", chatting.get(sender.getName())));
                }
            }
            return true;
        }
        
        target.chat(Utils.join(args, " ", 1));
        return true;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if(chatting.containsKey(e.getPlayer().getName())) { // The evil one chatted
            Player target = Players.getPlayer(chatting.get(e.getPlayer().getName()));
            if(target == null) { // Target gone offline
                e.getPlayer().sendMessage(Language.getTranslationForSender(e.getPlayer(), "chatasPlayerLeft", chatting.get(e.getPlayer().getName())));
                chatting.remove(e.getPlayer().getName());
            } else { // Target isn't offline!
                e.setCancelled(true);
                if(e.getMessage().equalsIgnoreCase("stop")) {
                    chatting.remove(e.getPlayer().getName());
                    e.getPlayer().sendMessage(Language.getTranslationForSender(e.getPlayer(), "chatasNormal"));
                }
                target.chat(e.getMessage());
            }
        } else if(chatting.containsValue(e.getPlayer().getName())) { // The victim chatted
            e.setCancelled(true);
        }
    }

}
