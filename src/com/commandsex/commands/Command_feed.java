package com.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;

/**
 * Feed, fills the food bar of specified player
 * @author Kezz101
 */
@Builder(name = "feed", description = "Fills the food bar of specified player", type = "COMMAND")
@Cmd(command = "feed", description = "Fills the food bar of specified player", usage = "%c% [player | (<all> [permission]) ]")

public class Command_feed implements Command, EnableJob{
    private Permission others = new Permission("cex.feed.others", PermissionDefault.OP);
    private Permission all = new Permission("cex.feed.all", PermissionDefault.OP);
    private Permission byPerm = new Permission("cex.feed.byPermission", PermissionDefault.OP);
    
    @Override
    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(others);
        pluginManager.addPermission(all);
        pluginManager.addPermission(byPerm);
    }

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(!Players.checkIsPlayer(sender, true))
            return true;
        
        if(args == null || args.length == 0) { // Feed self
            ((Player) sender).setFoodLevel(20);
            sender.sendMessage(Language.getTranslationForSender(sender, "feedSelf"));
            return true;
        }
        
        if(args.length == 1) { // Feed others
            if(args[0].equalsIgnoreCase("all")) {
                if(!Players.hasPermission(sender, all))
                    return true;
                int healed = 0;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.setFoodLevel(20);
                    player.sendMessage(Language.getTranslationForSender(sender, "fedBy", sender.getName()));
                    healed++;
                }
                sender.sendMessage(Language.getTranslationForSender(sender, "feedOthers", healed + ""));
            } else {
                if(!Players.hasPermission(sender, others))
                    return true;
                Player target = Players.getPlayer(args[0], sender);
                if(target != null) {
                    target.setFoodLevel(20);
                    target.sendMessage(Language.getTranslationForSender(sender, "fedBy", sender.getName()));
                    sender.sendMessage(Language.getTranslationForSender(sender, "feedOther", target.getName()));
                }
            }
            return true;
        }
        
        if(args.length == 2 && Players.hasPermission(sender, byPerm)) {
            if(!args[0].equalsIgnoreCase("all"))
                return false;
            
            int healed = 0;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission(args[1])) {
                    player.setFoodLevel(20);
                    player.sendMessage(Language.getTranslationForSender(sender, "fedBy", sender.getName()));
                    healed++;
                }
            }
            sender.sendMessage(Language.getTranslationForSender(sender, "feedOthersByPerm", healed + "", args[1]));
        }
        
        return true;
    }

}