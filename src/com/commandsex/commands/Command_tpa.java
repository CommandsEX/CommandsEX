package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Teleportation;
import com.commandsex.interfaces.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Builder(name = "tpa", description = "Request to teleport to another player", type = "COMMAND", depends = "helpers/Teleportation, commands/Command_tpaccept, commands/Command_tpdeny")
@Cmd(command = "tpa", description = "Request to teleport to another player", usage = "%c% <player>", aliases = "tpask, tprequest")
public class Command_tpa implements Command {
    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if (args.length != 1){
            return false;
        }

        if (Players.checkIsPlayer(sender)){
            Player player = (Player) sender;
            Player target = Players.getPlayer(args[0], player);

            if (target == null){
                return true;
            }

            if (player == target){
                player.sendMessage(Language.getTranslationForSender(player, "cannotUseOnSelf"));
                return true;
            }

            Teleportation.newTpaRequest(target.getName(), player.getName());
            player.sendMessage(Language.getTranslationForSender(player, "tpaSentTo", target.getName()));
        }

        return true;
    }
}