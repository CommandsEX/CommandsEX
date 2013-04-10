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
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;

/**
 * Burn, sets a player on fire for a specified amount of time
 * @author Kezz101
 */
@Builder(name = "burn", description = "Sets a player on fire for a specified amount of time", type = "COMMAND")
@Cmd(command = "burn", description = "Sets a player on fire for a specified amount of time", usage = "%c% <time> [player | (<all> [permission]) ]")
public class Command_burn implements Command, EnableJob{
    private Permission others = new Permission("cex.burn.others", PermissionDefault.OP);
    private Permission all = new Permission("cex.burn.all", PermissionDefault.OP);
    private Permission byPerm = new Permission("cex.burn.byPermission", PermissionDefault.OP);
    
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
        
        if(args == null || args.length == 0)
            return false;
        
        if(!Utils.isInt(args[0])) {
            sender.sendMessage(Language.getTranslationForSender(sender, "burnInvalidTime", args[0]));
            return true;
        }
        
        int time = Integer.parseInt(args[0]) * 20;
        
        if(args.length == 1) { // Burn self
            ((Player) sender).setFireTicks(time);
            sender.sendMessage(Language.getTranslationForSender(sender, "burnSelf"));
            return true;
        }
        
        if(args.length == 2) { // Burn others
            if(args[1].equalsIgnoreCase("all")) {
                if(!Players.hasPermission(sender, all))
                    return true;
                int burnt = 0;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.setFireTicks(time);
                    player.sendMessage(Language.getTranslationForSender(sender, "burntBy", sender.getName()));
                    burnt++;
                }
                sender.sendMessage(Language.getTranslationForSender(sender, "burnOthers", burnt + ""));
            } else {
                if(!Players.hasPermission(sender, others))
                    return true;
                Player target = Players.getPlayer(args[0], sender);
                if(target != null) {
                    target.setFireTicks(time);
                    target.sendMessage(Language.getTranslationForSender(sender, "burntBy", sender.getName()));
                    sender.sendMessage(Language.getTranslationForSender(sender, "burnOther", target.getName()));
                }
            }
            return true;
        }
        
        if(args.length == 3) { // Burn by perm
            if(!args[0].equalsIgnoreCase("all"))
                return false;
            
            if(!Players.hasPermission(sender, byPerm))
                return true;
            
            int burnt = 0;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission(args[1])) {
                    player.setFireTicks(time);
                    player.sendMessage(Language.getTranslationForSender(sender, "burntBy", sender.getName()));
                    burnt++;
                }
            }
            sender.sendMessage(Language.getTranslationForSender(sender, "burnOthersByPerm", burnt + "", args[1]));
        }
        
        return true;
    }

}