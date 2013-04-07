package com.commandsex.commands;

import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.interfaces.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@Builder(name = "suicide", type = "COMMAND", description = "Kill yourself!")
@Cmd(command = "suicide", description = "Kill yourself!", usage = "%c% [player]", aliases = "kill")
public class Command_suicide implements Command {

    public boolean run(CommandSender sender, String[] args, String alias) {
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "This command can only be used in-game");
            return true;
        }

        Player target;

        if (args.length == 0){
            if (Players.checkIsPlayer(sender)){
                target = (Player) sender;
            } else {
                return true;
            }
        } else if (args.length == 1){
            target = Players.getPlayer(args[0], sender);

            if (target == null){
                return true;
            }
        } else {
            return false;
        }

        EntityDamageEvent damageEvent = new EntityDamageEvent(target, EntityDamageEvent.DamageCause.SUICIDE, 1000);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (damageEvent.isCancelled()) return true;

        damageEvent.getEntity().setLastDamageCause(damageEvent);
        target.setHealth(0);
        return true;
    }
}
