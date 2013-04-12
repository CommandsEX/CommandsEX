package com.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.commandsex.Language;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Muting;
import com.commandsex.helpers.Players;
import com.commandsex.interfaces.Command;

/**
 * UnMute, unmutes a player
 * @author Kezz101
 */
@Cmd(command = "unmute", description = "Unmutes a player", usage = "%c% <player>")
public class Command_unmute implements Command {

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(args == null || args.length != 1)
            return false;
                
        Player player = Players.getPlayer(args[0], sender);
        
        if(player == null)
            return true;
        
        if(!Muting.isMuted(player.getName())) {
            sender.sendMessage(Language.getTranslationForSender(sender, "unmuteError"));
            return true;
        }
        
        Muting.unmute(player.getName());
        player.sendMessage(Language.getTranslationForSender(player, "unmuted"));
        sender.sendMessage(Language.getTranslationForSender(sender, "unmutedAnother", player.getName()));
        return true;
    }

}
