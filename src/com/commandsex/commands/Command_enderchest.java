package com.commandsex.commands;

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
 * Ender chest, opens a players enderchest
 * @author Kezz101
 */
@Builder(name = "enderchest", description = "Opens an ender chest", type = "COMMAND")
@Cmd(command = "enderchest", description = "Opens a ender chest", usage = "%c% [player]")
public class Command_enderchest implements Command, EnableJob {
    private Permission others = new Permission("cex.enderchest.others", PermissionDefault.OP);
    
    @Override
    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(others);
    }

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(!Players.checkIsPlayer(sender, true))
            return true;
        
        if(args == null || args.length == 0) { // Chest self
            ((Player) sender).openInventory(((Player) sender).getEnderChest());
            sender.sendMessage(Language.getTranslationForSender(sender, "enderChestSelf"));
            return true;
        }
        
        if(args.length == 1) { // Chest another
            if(!Players.hasPermission(sender, others))
                return true;
            Player target = Players.getPlayer(args[0], sender);
            if(target != null) {
                ((Player) sender).openInventory(target.getEnderChest());
                sender.sendMessage(Language.getTranslationForSender(sender, "enderChestOther", target.getName()));
            }
            return true;
        }
        
        return false;
    }
}
