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
 * CMDU, runs a command as another player
 * @author Kezz101
 */
@Builder(name = "cmdu", description = "Runs a command as another player", type = "COMMAND")
@Cmd(command = "cmdu", description = "Runs a command as another player", usage = "%c% <player> <command>")
public class Command_cmdu implements Command, EnableJob {
    private Permission onOP = new Permission("cex.cmdu.runOnOp", PermissionDefault.FALSE);

    @Override
    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(onOP);
    }
    
    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(args == null || args.length < 2)
            return false;
        
        Player target = Players.getPlayer(args[0], sender);
        
        if(target == null)
            return true;
        
        if(target.isOp() && !sender.hasPermission(onOP)) {
            sender.sendMessage(Language.getTranslationForSender(sender, "cmduNoPermission", target.getName()));
            return true;
        }
        
        Bukkit.dispatchCommand(target, args[1].startsWith("/") ? Utils.join(args, " ", 1).substring(1) : Utils.join(args, " ", 1));
        sender.sendMessage(Language.getTranslationForSender(sender, "cmduExecuted", target.getName()));
        return true;
    }

}
