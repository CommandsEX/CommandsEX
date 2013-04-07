package com.commandsex.commands;

import com.commandsex.CommandsEX;
import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.interfaces.Command;
import com.commandsex.interfaces.EnableJob;
import com.commandsex.helpers.Players;
import com.commandsex.helpers.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

@Builder(name = "cex", description = "Core Management functions for CommandsEX", type = "COMMAND", core = true)
@Cmd(command = "cex", description = "Core Management functions for CommandsEX", usage = "%c% OR %c% [reload]", aliases = "cex_about, cex_info, commandsex")
public class Command_cex implements Command, EnableJob {

    public Permission permission = new Permission("cex.info", "Displays information about CommandsEX", PermissionDefault.TRUE);
    public Permission reloadPerm = new Permission("cex.reload", "Reloads CommandsEX", PermissionDefault.OP);

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {
        if (args.length == 0){
            if (Players.hasPermission(sender, permission)){
                PluginDescriptionFile pluginDescriptionFile = CommandsEX.plugin.getDescription();
                sender.sendMessage(Language.getTranslationForSender(sender, "info", pluginDescriptionFile.getVersion(), ChatColor.translateAlternateColorCodes('&', Utils.join(pluginDescriptionFile.getAuthors(), "&b, &6", "&b & &6"))));
            }

            return true;
        } else if (args[0].equalsIgnoreCase("reload")){
            if (Players.hasPermission(sender, reloadPerm)){
                CommandsEX.plugin.reload();
                sender.sendMessage(ChatColor.GREEN + Language.getTranslationForSender(sender, "reloaded"));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(permission);
        pluginManager.addPermission(reloadPerm);
    }
}
