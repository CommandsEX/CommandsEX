package com.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.commandsex.Language;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Muting;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.Command;

/**
 * Mute, mutes a player
 * @author Kezz101
 */
@Cmd(command = "mute", description = "Mutes a player", usage = "%c% <player> [minutes]")
public class Command_mute implements Command  {

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(args == null || args.length == 0 || args.length > 2)
            return false;
        
        Player target = Players.getPlayer(args[0], sender);
        
        if(target == null)
            return true;
        
        if(target.isOp() && !sender.hasPermission(Muting.MUTE_OP)) {
            sender.sendMessage(Language.getTranslationForSender(sender, "muteNoPermission", target.getName()));
            return true;
        }
        
        if(Muting.isMuted(target.getName())) {
            sender.sendMessage(Language.getTranslationForSender(sender, "mutedAlready", target.getName()));
            return true;
        }
        
        if(args.length == 1) { // Mute
            Muting.mute(target.getName());
            target.sendMessage(Language.getTranslationForSender(target, "mutedPre"));
            sender.sendMessage(Language.getTranslationForSender(sender, "muteAnother", target.getName()));
        } else if(args.length == 2) { // Time mute
            if(!Utils.isInt(args[1])) {
                sender.sendMessage(Language.getTranslationForSender(sender, "muteInvalidTime", args[1]));
                return true;
            }
            
            int time = Integer.parseInt(args[1]);
            Muting.mute(target.getName(), time);
            target.sendMessage(Language.getTranslationForSender(target, "mutedPreTime", args[1]));
            sender.sendMessage(Language.getTranslationForSender(sender, "muteAnotherTime", target.getName(), args[1]));
        }
        
        return true;
    }

}
