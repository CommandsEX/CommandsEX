package com.commandsex.commands;

import java.util.HashMap;
import java.util.Random;

import com.commandsex.CommandsEX;
import com.commandsex.api.annotations.Cmd;
import com.commandsex.api.interfaces.Command;
import com.commandsex.api.interfaces.EnableJob;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import com.commandsex.helpers.Utils;

@Cmd(description = "Changes a dogs collar color at a set interval", command = "discodog", permission = "cex.discodog")
public class Command_discodog implements Command {

    private HashMap<Integer, DyeColor> discoDogging = new HashMap<Integer, DyeColor>();
    public Permission discodogPerm = new Permission("cex.discodog", "Allows access to /discodog", PermissionDefault.OP);

    public boolean run(CommandSender sender, String[] args, String alias) {
        if (!(sender instanceof Player)){
            return true;
        }
        
        int time = 30;
        if (args.length == 1){
            if (!Utils.isInt(args[0])){
                // integer message
                return true;
            }
            
            time = Integer.parseInt(args[0]);
        } else if (args.length > 1){
            return false;
        }
        
        Player p = (Player) sender;
        Entity e = null;
        
        for (Entity en : p.getNearbyEntities(5, 5, 5)){
            if (en.getType() == EntityType.WOLF){
                e = en;
                break;
            }
        }
        
        if (discoDogging.containsKey(e.getEntityId())){
            // already disco dogging
            return true;
        }
        
        final Wolf f = (Wolf) e;
        
        if (!f.isTamed()){
            return true;
        }
        
        DyeColor original = f.getCollarColor();
        discoDogging.put(f.getEntityId(), original);
        
        final BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(CommandsEX.plugin, new Runnable() {
            public void run() {
                if (!f.isDead()){
                    DyeColor[] dc = DyeColor.values();
                    Random r = new Random();
                    f.setCollarColor(dc[r.nextInt(dc.length)]);
                }
            }
        }, 1L, 1L);
        
        Bukkit.getScheduler().runTaskLaterAsynchronously(CommandsEX.plugin, new Runnable() {
            public void run() {
                task.cancel();
                f.setCollarColor(discoDogging.get(f.getEntityId()));
                discoDogging.remove(f.getEntityId());
            }
        }, 20L * time);
        
        return true;
    }

}
