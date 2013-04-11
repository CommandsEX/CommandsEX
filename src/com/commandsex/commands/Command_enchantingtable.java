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
 * Enchanting table, opens an enchanting table for the player
 * @author Kezz101
 */
@Builder(name = "enchantingtable", description = "Opens an enchanting table for the player", type = "COMMAND")
@Cmd(command = "enchantingtable", description = "Opens an enchanting table for the player", usage = "%c% [player | (<all> [permission]) ]")
public class Command_enchantingtable implements Command, EnableJob {
    private Permission others = new Permission("cex.enchantingtable.others", PermissionDefault.OP);
    private Permission all = new Permission("cex.enchantingtable.all", PermissionDefault.OP);
    private Permission byPerm = new Permission("cex.enchantingtable.byPermission", PermissionDefault.OP);
    
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
        
        if(args == null || args.length == 0) { // Table self
            ((Player) sender).openEnchanting(null, true);
            sender.sendMessage(Language.getTranslationForSender(sender, "tableSelf"));
            return true;
        }
        
        if(args.length == 1) { // Table others
            if(args[1].equalsIgnoreCase("all")) {
                if(!Players.hasPermission(sender, all))
                    return true;
                int tabled = 0;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.openEnchanting(null, true);
                    player.sendMessage(Language.getTranslationForSender(sender, "tabledBy", sender.getName()));
                    tabled++;
                }
                sender.sendMessage(Language.getTranslationForSender(sender, "tableOthers", String.valueOf(tabled)));
            } else {
                if(!Players.hasPermission(sender, others))
                    return true;
                Player target = Players.getPlayer(args[0], sender);
                if(target != null) {
                    target.openEnchanting(null, true);
                    target.sendMessage(Language.getTranslationForSender(sender, "tabledBy", sender.getName()));
                    sender.sendMessage(Language.getTranslationForSender(sender, "tableOther", target.getName()));
                }
            }
            return true;
        }
        
        if(args.length == 2) { // Table by perm
            if(!args[0].equalsIgnoreCase("all"))
                return false;
            
            if(!Players.hasPermission(sender, byPerm))
                return true;
            
            int tabled = 0;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission(args[1])) {
                    player.openEnchanting(null, true);
                    player.sendMessage(Language.getTranslationForSender(sender, "tabledBy", sender.getName()));
                    tabled++;
                }
            }
            sender.sendMessage(Language.getTranslationForSender(sender, "tableOthersByPerm", String.valueOf(tabled), args[1]));
        }
        
        return true;
    }

}