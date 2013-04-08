package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Teleportation;
import com.commandsex.interfaces.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Builder(name = "tpahere", description = "", type = "COMMAND", depends = "commands/Command_tpaccept, commands/Command_tpdeny")
@Cmd(command = "tpahere", description = "Request a player to teleport to you", aliases = "tpah, tpatome", usage = "%c% <player>")
public class Command_tpahere implements Command {
    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if (Players.checkIsPlayer(sender)){
            if (args.length != 1){
                return false;
            }

            Player player = (Player) sender;
            Player target = Players.getPlayer(args[0], sender);

            if (target == null){
                return true;
            }

            String pName = player.getName();
            String tName = target.getName();

            if (player == target){
                player.sendMessage(Language.getTranslationForSender(player, "cannotUseOnSelf"));
                return true;
            }

            Teleportation.newTpaHereRequests(tName, pName);
            player.sendMessage(Language.getTranslationForSender(player, "tpa", tName));
            target.sendMessage(Language.getTranslationForSender(target, "tpaHereNotify", pName, pName, pName));
        }

        return true;
    }
}
