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
 * Workbench, opens a workbench for the player
 * @author Kezz101
 */
@Builder(name = "workbench", description = "Opens a workbench", type = "COMMAND")
@Cmd(command = "workbench", description = "Opens a workbench", usage = "%c% [player | (<all> [permission]) ]")
public class Command_workbench implements Command, EnableJob {
    private Permission others = new Permission("cex.workbench.others", PermissionDefault.OP);
    private Permission all = new Permission("cex.workbench.all", PermissionDefault.OP);
    private Permission byPerm = new Permission("cex.workbench.byPermission", PermissionDefault.OP);
    
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
        
        if(args == null || args.length == 0) { // Bench self
            ((Player) sender).openWorkbench(null, true);
            sender.sendMessage(Language.getTranslationForSender(sender, "workbenchSelf"));
            return true;
        }
        
        if(args.length == 1) { // Bench others
            if(args[1].equalsIgnoreCase("all")) {
                if(!Players.hasPermission(sender, all))
                    return true;
                int benched = 0;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.openWorkbench(null, true);
                    player.sendMessage(Language.getTranslationForSender(sender, "workbenchedBy", sender.getName()));
                    benched++;
                }
                sender.sendMessage(Language.getTranslationForSender(sender, "workbenchOthers", String.valueOf(benched)));
            } else {
                if(!Players.hasPermission(sender, others))
                    return true;
                Player target = Players.getPlayer(args[0], sender);
                if(target != null) {
                    target.openWorkbench(null, true);
                    target.sendMessage(Language.getTranslationForSender(sender, "workbenchedBy", sender.getName()));
                    sender.sendMessage(Language.getTranslationForSender(sender, "workbenchOther", target.getName()));
                }
            }
            return true;
        }
        
        if(args.length == 2) { // Bench by perm
            if(!args[0].equalsIgnoreCase("all"))
                return false;
            
            if(!Players.hasPermission(sender, byPerm))
                return true;
            
            int benched = 0;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission(args[1])) {
                    player.openWorkbench(null, true);
                    player.sendMessage(Language.getTranslationForSender(sender, "workbenchedBy", sender.getName()));
                    benched++;
                }
            }
            sender.sendMessage(Language.getTranslationForSender(sender, "workbenchOthersByPerm", String.valueOf(benched), args[1]));
        }
        
        return true;
    }

}
