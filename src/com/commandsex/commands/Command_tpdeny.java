package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Teleportation;
import com.commandsex.interfaces.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Cmd(command = "tpdeny", description = "Deny teleportation requests", aliases = "tpadeny, tpno, tpd, tpde", permissionDefault = "ALL")
public class Command_tpdeny implements Command {
    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if (Players.checkIsPlayer(sender)){
            Player player = (Player) sender;
            String pName = player.getName();

            if (args.length != 1){
                return false;
            }

            Player target = Players.getPlayer(args[0], player);

            if (target == null){
                return true;
            }

            String tName = target.getName();
            if (Teleportation.hasTpaRequest(pName, tName)){
                Teleportation.removeTpaRequest(pName, tName);
            } else if (Teleportation.hasTpaHereRequest(pName, tName)){
                Teleportation.removeTpaHereRequest(pName, tName);
            } else {
                player.sendMessage(Language.getTranslationForSender(player, "noRequestFromThatPlayer", args[0]));
                return true;
            }

            player.sendMessage(Language.getTranslationForSender(player, "tpaDeny", tName));
            target.sendMessage(Language.getTranslationForSender(target, "tpaDenyNotify", pName));
        }

        return true;
    }
}
