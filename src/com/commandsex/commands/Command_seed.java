package com.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.commandsex.Language._;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.interfaces.Command;

/**
 * Seed, retrives the seed of any word
 * @author Kezz101
 */
@Builder(name = "seed", description = "Retrives the seed of any word", type = "COMMAND")
@Cmd(command = "seed", description = "Retrives the seed of any word", permissionDefault = "TRUE", usage = "%c% [world]")
public class Command_seed implements Command {

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        World world = null;
        
        if(args == null || args.length == 0) { //Current world
            if(sender instanceof Player)
                world = ((Player) sender).getWorld();
            else 
                return false;
        }
        
        if(args.length == 1) { //Any world
            if(Bukkit.getWorld(args[0]) == null) {
                sender.sendMessage(_(sender, "seedWorldNotFound", args[0]));
                return true;
            }
            world = Bukkit.getWorld(args[0]);
        }
        
        sender.sendMessage(_(sender, "seedFound", args[0], world.getSeed()));
        return true;
    }

}
