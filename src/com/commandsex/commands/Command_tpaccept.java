package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Teleportation;
import com.commandsex.interfaces.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@Cmd(command = "tpaccept", description = "Accepts a teleport request", aliases = "tpac, tpacc, tpyes", permissionDefault = "ALL")
public class Command_tpaccept implements Command {
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
            } else if (Teleportation.hasTpaHereRequest(pName)){
                targetName = Teleportation.getTpaHereRequest(pName);
            } else {
                player.sendMessage(Language.getTranslationForSender(player, "noRequests"));
                return true;
            }

            Player target = Bukkit.getPlayerExact(targetName);

            if (target == null){
                player.sendMessage(Language.getTranslationForSender(player, "tpaAcceptOffline", targetName));
                return true;
            }

            if (Teleportation.hasTpaRequest(pName)){
                target.teleport(player, PlayerTeleportEvent.TeleportCause.COMMAND);
                Teleportation.removeTpaRequest(pName);
            } else {
                player.teleport(target, PlayerTeleportEvent.TeleportCause.COMMAND);
                Teleportation.removeTpaHereRequest(pName);
            }

            player.sendMessage(Language.getTranslationForSender(player, "tpaAccept", targetName));
            target.sendMessage(Language.getTranslationForSender(target, "tpaAcceptNotify", pName));
        }

        return true;
    }
}
