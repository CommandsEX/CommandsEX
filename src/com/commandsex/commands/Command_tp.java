package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

@Builder(name = "tp", description = "Teleport to players or locations", type = "COMMAND")
@Cmd(command = "tp", description = "Teleport to players or locations", aliases = "teleport, tpplayer, tele", usage = "%c% [player] <target>")
public class Command_tp implements Command, EnableJob {

    Permission tpOthersPerm = new Permission("cex.tp.others", "Allows /tp to be used on others", PermissionDefault.OP);

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if (args.length != 1 && args.length != 2){
            return false;
        }

        if (args.length == 1){
            // Teleport sender to player
            if (Players.checkIsPlayer(sender, false)){
                Player player = (Player) sender;
                Player target = Players.getPlayer(args[0], sender);

                if (target == null){
                    return true;
                }

                if (player == target){
                    player.sendMessage(Language.getTranslationForSender(player, "cannotUseOnSelf"));
                    return true;
                }

                player.teleport(target, PlayerTeleportEvent.TeleportCause.COMMAND);
                player.sendMessage(Language.getTranslationForSender(player, "tpPlayer", target.getName()));
            } else {
                return false;
            }
        } else {
            if (Players.hasPermission(sender, tpOthersPerm)){
                // Teleport player to player
                Player target1 = Players.getPlayer(args[0], sender);

                if (target1 == null){
                    return true;
                }

                Player target2 = Players.getPlayer(args[1], sender);

                if (target2 == null){
                    return true;
                }

                if (target1 == target2){
                    target1.sendMessage(Language.getTranslationForSender(target1, "tpPlayersMustBeDifferent"));
                    return true;
                }

                target1.teleport(target2, PlayerTeleportEvent.TeleportCause.COMMAND);
                target1.sendMessage(Language.getTranslationForSender(target1, "tpPlayerToPlayer", sender.getName(), target2.getName()));
            }
        }

        return true;
    }

    @Override
    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(tpOthersPerm);
    }
}
