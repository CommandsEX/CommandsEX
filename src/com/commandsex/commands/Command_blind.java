package com.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;

/**
 * Blind, blinds a player for a specified amount of time
 * @author Kezz101
 */
@Builder(name = "blind", description = "Blinds a player for a specified amount of time", type = "COMMAND")
@Cmd(command = "blind", description = "Blinds a player for a specified amount of time", usage = "%c% <time> [player | (<all> [permission]) ]")
public class Command_blind implements Command, EnableJob{
    private Permission others = new Permission("cex.blind.others", PermissionDefault.OP);
    private Permission all = new Permission("cex.blind.all", PermissionDefault.OP);
    private Permission byPerm = new Permission("cex.blind.byPermission", PermissionDefault.OP);
    
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
            sender.sendMessage(Language.getTranslationForSender(sender, "blindInvalidTime", args[0]));
            return true;
        }
        
        int time = Integer.parseInt(args[0]);
        
        if(args.length == 1) { // Blind self
            ((Player) sender).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 0));
            sender.sendMessage(Language.getTranslationForSender(sender, "blindSelf"));
            return true;
        }
        
        if(args.length == 2) { // Blind others
            if(args[1].equalsIgnoreCase("all")) {
                if(!Players.hasPermission(sender, all))
                    return true;
                int blinded = 0;
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 0));
                    player.sendMessage(Language.getTranslationForSender(sender, "blindedBy", sender.getName()));
                    blinded++;
                }
                sender.sendMessage(Language.getTranslationForSender(sender, "blindOthers", blinded + ""));
            } else {
                if(!Players.hasPermission(sender, others))
                    return true;
                Player target = Players.getPlayer(args[0], sender);
                if(target != null) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 0));
                    target.sendMessage(Language.getTranslationForSender(sender, "blindedBy", sender.getName()));
                    sender.sendMessage(Language.getTranslationForSender(sender, "blindOther", target.getName()));
                }
            }
            return true;
        }
        
        if(args.length == 3) { // Heal by perm
            if(!args[0].equalsIgnoreCase("all"))
                return false;
            
            if(!Players.hasPermission(sender, byPerm))
                return true;
            
            int blinded = 0;
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission(args[1])) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 0));
                    player.sendMessage(Language.getTranslationForSender(sender, "blindedBy", sender.getName()));
                    blinded++;
                }
            }
            sender.sendMessage(Language.getTranslationForSender(sender, "blindOthersByPerm", blinded + "", args[1]));
        }
        
        return true;
    }

}
