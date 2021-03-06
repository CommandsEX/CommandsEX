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
 * Heal, fills the health bar of specified player
 * @author Kezz101
 */
@Builder(name = "heal", description = "Fills the health bar of specified player", type = "COMMAND")
@Cmd(command = "heal", description = "Fills the health bar of specified player", usage = "%c% [player | (<all> [permission]) ]")
public class Command_heal implements Command, EnableJob{
    private Permission others = new Permission("cex.heal.others", PermissionDefault.OP);
    private Permission all = new Permission("cex.heal.all", PermissionDefault.OP);
    private Permission byPerm = new Permission("cex.heal.byPermission", PermissionDefault.OP);
    
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
        
        if(args == null || args.length == 0) { // Heal self
            ((Player) sender).setHealth(20);
            sender.sendMessage(Language.getTranslationForSender(sender, "healSelf"));
            return true;
        }
        
        if(args.length == 1) { // Heal others
            if(args[0].equalsIgnoreCase("all")) {
                if(!Players.hasPermission(sender, all))
                    return true;
                int healed = 0;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.setHealth(20);
                    player.sendMessage(Language.getTranslationForSender(sender, "healedBy", sender.getName()));
                    healed++;
                }
                sender.sendMessage(Language.getTranslationForSender(sender, "healOthers", String.valueOf(healed)));
            } else {
                if(!Players.hasPermission(sender, others))
                    return true;
                Player target = Players.getPlayer(args[0], sender);
                if(target != null) {
                    target.setHealth(20);
                    target.sendMessage(Language.getTranslationForSender(sender, "healedBy", sender.getName()));
                    sender.sendMessage(Language.getTranslationForSender(sender, "healOther", target.getName()));
                }
            }
            return true;
        }
        
        if(args.length == 2) { // Heal by perm
            if(!args[0].equalsIgnoreCase("all"))
                return false;
            
            if(!Players.hasPermission(sender, byPerm))
                return true;
            
            int healed = 0;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission(args[1])) {
                    player.setHealth(20);
                    player.sendMessage(Language.getTranslationForSender(sender, "healedBy", sender.getName()));
                    healed++;
                }
            }
            sender.sendMessage(Language.getTranslationForSender(sender, "healOthersByPerm", String.valueOf(healed), args[1]));
        }
        
        return true;
    }

}
