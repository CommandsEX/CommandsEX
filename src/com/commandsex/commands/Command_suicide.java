package com.commandsex.commands;

import com.commandsex.CommandsEX;
import com.commandsex.api.annotations.Builder;
import com.commandsex.api.annotations.Cmd;
import com.commandsex.api.interfaces.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@Builder(name = "Suicide", description = "Kill yourself!")
@Cmd(command = "suicide", description = "Kill yourself!", aliases = "kill")
public class Command_suicide implements Command {

    public boolean run(CommandSender sender, String[] args, String alias) {
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "This command can only be used in-game");
            return true;
        }
        
        Player player = (Player) sender;
        
        EntityDamageEvent event = new EntityDamageEvent(player, DamageCause.SUICIDE, 1000);
        Bukkit.getPluginManager().callEvent(event);
        
        if (event.isCancelled()) return true;
        
        event.getEntity().setLastDamageCause(event);
        player.damage(event.getDamage());
        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.RED + " committed suicide");
        
        return true;
    }
}
