package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Utils;
import com.commandsex.helpers.VaultHelper;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

/**
 * Written by Keir Nellyer (iKeirNez)
 * Project: CommandsEX
 * Date: 11/05/13
 * Time: 16:44
 */

@Builder(name = "give", description = "Gives a player an item", type = "COMMAND")
@Cmd(command = "give", description = "Gives a player an item", aliases = "giveitem", usage = "%c% <player> <item>[:damage] [amount]")
public class Command_give implements Command, EnableJob {

    private Permission givePermission = new Permission("cex.give", PermissionDefault.OP);
    private Permission giveOthersPermission = new Permission("cex.give.others", PermissionDefault.OP);

    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(giveOthersPermission);
    }

    public boolean run(CommandSender sender, String[] args, String alias) {
        if (!Players.hasPermission(sender, givePermission)){
            return true;
        }

        if (args.length < 2|| args.length > 3){
            return false;
        }

        Player target = Players.getPlayer(args[0], sender);

        if (target == null){
            return true;
        }

        if (sender != target && !Players.hasPermission(sender, giveOthersPermission)){
            return true;
        }

        String itemName = args[1].contains(":") ? args[1].split(":")[0] : args[1];
        Material material = null;

        if (VaultHelper.isVaultAvailable()){
            ItemInfo itemInfo = Items.itemByName(itemName);

            if (itemInfo != null){
                material = itemInfo.getType();
            }
        } else {
            material = Material.getMaterial(itemName);
        }

        if (material == null){
            sender.sendMessage(Language.getTranslationForSender(sender, "giveItemNotFound", itemName));
            return true;
        }

        short damage;

        try {
            damage = Short.parseShort(args[1].contains(":") ? args[1].split(":")[1] : "0");
        } catch (NumberFormatException e){
            sender.sendMessage(Language.getTranslationForSender(sender, "giveDamageNotNumeric"));
            return true;
        }

        int amount;

        try {
            amount = args.length == 3 ? Integer.parseInt(args[2]) : material.getMaxStackSize();
        } catch (NumberFormatException e){
            sender.sendMessage(Language.getTranslationForSender(sender, "giveAmountNotNumeric", args[2]));
            return true;
        }

        ItemStack itemStack = new ItemStack(material, amount, damage);
        target.getInventory().addItem(itemStack);

        String userFriendlyAmount = String.valueOf((amount % 64 == 0 ? (amount == 64 ? Language.getTranslationForSender(sender, "giveAStackOf") : Language.getTranslationForSender(sender, "giveStacksOf", amount / 64)) : (amount == 1 ? WordUtils.capitalize(Language.getTranslationForSender(sender, "a")) : String.valueOf(amount))));
        String friendlyName = Utils.getFriendlyName(material.name());
        String userFriendlyName = friendlyName + (amount > 1 ? "s" : "");

        if (target == sender){
            target.sendMessage(Language.getTranslationForSender(target, "giveSelf", userFriendlyAmount, userFriendlyName));
        } else {
            sender.sendMessage(Language.getTranslationForSender(sender, "giveOther", target.getName(), userFriendlyAmount, userFriendlyName));
            String targetUserFriendlyAmount = String.valueOf((amount % 64 == 0 ? (amount == 64 ? Language.getTranslationForSender(target, "giveAStackOf") : Language.getTranslationForSender(target, "giveStacksOf", amount / 64)) : (amount == 1 ? WordUtils.capitalize(Language.getTranslationForSender(target, "a")) : String.valueOf(amount))));
            target.sendMessage(Language.getTranslationForSender(target, "giveOtherNotify", sender.getName(), targetUserFriendlyAmount, userFriendlyName));
        }

        return true;
    }
}
