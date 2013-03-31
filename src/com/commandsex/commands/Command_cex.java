package com.commandsex.commands;

import com.commandsex.CommandsEX;
import com.commandsex.api.annotations.Builder;
import com.commandsex.api.annotations.Cmd;
import com.commandsex.api.interfaces.Command;
import com.commandsex.api.interfaces.EnableJob;
import com.commandsex.helpers.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

@Cmd(command = "cex", description = "Displays information about CommandsEX", aliases = "cex_about, cex_info, commandsex", permission = "cex.info")
public class Command_cex implements Command, EnableJob {

    @Override
    public boolean run(CommandSender sender, String[] args, String alias) {

        if (args.length == 0){
            PluginDescriptionFile pluginDescriptionFile = CommandsEX.plugin.getDescription();
            // TODO move this to language
            sender.sendMessage(ChatColor.AQUA + "Currently running " + ChatColor.GOLD + "CommandsEX " + pluginDescriptionFile.getVersion());
            sender.sendMessage(ChatColor.AQUA + "Developed by " + ChatColor.GOLD + Utils.join(pluginDescriptionFile.getAuthors(), ChatColor.AQUA + ", " + ChatColor.GOLD, ChatColor.AQUA + " & " + ChatColor.GOLD));
            return true;
        } else if (args[0].equalsIgnoreCase("reload")){
            CommandsEX.plugin.reload();
            sender.sendMessage(ChatColor.GREEN + "CommandsEX Reloaded"); // todo language
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onEnable(PluginManager pluginManager) {
        pluginManager.addPermission(new Permission("cex.info", "Displays information about CommandsEX", PermissionDefault.TRUE));
        pluginManager.addPermission(new Permission("cex.reload", "Reloads CommandsEX", PermissionDefault.OP));
    }
}
