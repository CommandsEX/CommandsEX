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
 * AMessage, sends an anon message to a player
 * @author Kezz101
 */
@Builder(name = "amessage", description = "Sends an anon message to a player", type = "COMMAND")
@Cmd(command = "amessage", description = "Sends an anon message to a player", usage = "%c% <player | all) > <message>")
public class Command_amessage implements Command, EnableJob{
    private Permission all = new Permission("cex.amessage.all", PermissionDefault.OP);
    
    @Override
    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(all);
    }

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(!Players.checkIsPlayer(sender, true))
            return true;
        
        if(args == null || args.length < 2)
            return false;
        
        if(args[0].equalsIgnoreCase("all")) {
            if(!Players.hasPermission(sender, all))
                return true;
            
            int count = 0;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.getName().equals(sender.getName()))
                    continue;
                player.sendMessage(Language.getTranslationForSender(player, "amessage", Utils.join(args, " ", 1)));
                count ++;
            }
            
            sender.sendMessage(Language.getTranslationForSender(sender, "amessageSentMany", String.valueOf(count)));
        } else {
            if(Players.getPlayer(args[0], sender) == null)
                return true;
            
            Player player = Players.getPlayer(args[0]);
            player.sendMessage(Language.getTranslationForSender(player, "amessage", Utils.join(args, " ", 1)));
            sender.sendMessage(Language.getTranslationForSender(sender, "amessageSentOne", player.getName()));
        }
        
        return true;
    }

}
