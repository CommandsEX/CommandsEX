package com.commandsex.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;

/**
 * Hat, puts a block on your head
 * @author kezz101
 */
@Builder(name="hat", description="Puts a block on your head!", type="COMMAND")
@Cmd(command = "hat", description = "Puts a block on your head!", aliases = "head", usage = "%c% [item id] [players]")
public class Command_hat implements Command, EnableJob {
    final Permission customItem = new Permission("cex.hat.customItem", "Allows the sender to put any item on their head", PermissionDefault.OP);
    final Permission others = new Permission("cex.hat.others", "Allows the sender to put a hat on another player's head", PermissionDefault.OP);
    final Permission prot = new Permission("cex.hat.protected", "Prevents the player from being given a hat", PermissionDefault.FALSE);
    
    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if(!Players.checkIsPlayer(sender)) // Player?
            return true;
        
        if(args == null || args.length == 0) { // Hand on head
            Player player = (Player) sender;

            if(player.getInventory().getHelmet() != null) {
                player.sendMessage(Language.getTranslationForSender(player, "hatOwnHeadFull"));
                return true;
            }
            
            if(player.getItemInHand().getTypeId() == 0 || !player.getItemInHand().getType().isSolid()) {
                player.sendMessage(Language.getTranslationForSender(player, "hatOwnInvalidItem", Utils.getFriendlyMaterialName(player.getItemInHand().getType())));
                return true;
            }
            
            player.getInventory().setHelmet(new ItemStack(player.getItemInHand().getTypeId(), 1));
            
            if(player.getItemInHand().getAmount() > 1)
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            else
                player.setItemInHand(null);
            
            player.sendMessage(Language.getTranslationForSender(player, "hatOwnNew"));
            return true;
        }
        
        if(args.length == 1) { // Anything on head
            Player player = (Player) sender;

            if (!Players.hasPermission(player, customItem)){
                return true;
            }

            if(player.getInventory().getHelmet() != null) {
                player.sendMessage(Language.getTranslationForSender(player, "hatOwnHeadFull"));
                return true;
            }
            
            if(!Utils.isInt(args[0]) || Material.getMaterial(Integer.parseInt(args[0])) == null) {
                player.sendMessage(Language.getTranslationForSender(player, "hatInvalidItemArg", args[0]));
                return true;
            }
            
            if(!Material.getMaterial(Integer.parseInt(args[0])).isSolid()) {
                player.sendMessage(Language.getTranslationForSender(player, "hatOwnInvalidItem", Utils.getFriendlyMaterialName(Material.getMaterial(Integer.parseInt(args[0])))));
                return true;
            }
            
            player.getInventory().setHelmet(new ItemStack(Integer.parseInt(args[0]), 1));            
            player.sendMessage(Language.getTranslationForSender(player, "hatOwnNew"));
            return true;
        }
        
        if(args.length == 2) { // Anything on anothers head
            Player player = (Player) sender, target;

            if (!Players.hasPermission(player, others)){
                return true;
            }
            
            if(Players.getPlayer(args[1], player) == null)
                return true;
            
            target = Players.getPlayer(args[1]);
            
            if(target.hasPermission(prot)) {
                player.sendMessage(Language.getTranslationForSender(target, "hatProtected", target.getName()));
                return true;
            }
            
            if(target.getInventory().getHelmet() != null) {
                player.sendMessage(Language.getTranslationForSender(player, "hatHeadFull", target.getName()));
                return true;
            }
            
            if(!Utils.isInt(args[0]) || Material.getMaterial(Integer.parseInt(args[0])) == null) {
                player.sendMessage(Language.getTranslationForSender(player, "hatInvalidItemArg", args[0]));
                return true;
            }
            
            if(!Material.getMaterial(Integer.parseInt(args[0])).isSolid()) {
                player.sendMessage(Language.getTranslationForSender(player, "hatInvalidItem", Utils.getFriendlyMaterialName(Material.getMaterial(Integer.parseInt(args[0]))), target.getName()));
                return true;
            }
            
            target.getInventory().setHelmet(new ItemStack(Integer.parseInt(args[0]), 1));            
            player.sendMessage(Language.getTranslationForSender(player, "hatNewSender", target.getName()));
            target.sendMessage(Language.getTranslationForSender(target, "hatNewTarget", player.getName()));
            return true;
        }
        
        return false;
    }

    @Override
    public void onEnable(PluginManager pm) {
        pm.addPermission(customItem);
        pm.addPermission(others);
        pm.addPermission(prot);
    }

}
