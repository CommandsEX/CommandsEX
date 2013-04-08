package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Teleportation;
import com.commandsex.interfaces.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Cmd(command = "tpdeny", description = "Deny teleportation requests", aliases = "tpno, tpd, tpde", permissionDefault = "ALL")
public class Command_tpdeny implements Command {
    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if (Players.checkIsPlayer(sender)){
            Player player = (Player) sender;

            if (args.length != 0){
                return false;
            }

            String pName = player.getName();
            String targetName;
            if (Teleportation.hasTpaRequest(pName)){
                targetName = Teleportation.getTpaRequest(pName);
                Teleportation.removeTpaRequest(pName);
            } else if (Teleportation.hasTpaHereRequest(pName)){
                targetName = Teleportation.getTpaHereRequest(pName);
                Teleportation.removeTpaHereRequest(pName);
            } else {
                player.sendMessage(Language.getTranslationForSender(sender, "noRequests"));
                return true;
            }

            Player target = Bukkit.getPlayerExact(targetName);

            if (target != null){
                target.sendMessage(Language.getTranslationForSender(target, "tpaDenyNotify", pName));
            }

            player.sendMessage(Language.getTranslationForSender(player, "tpaDeny", targetName));
        }

        return true;
    }
}
