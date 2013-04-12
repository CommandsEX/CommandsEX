package com.commandsex.commands;

import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.Command;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

@Builder(name = "plugin", description = "Full plugin management suite", type = "COMMAND", depends = "helpers/plugman/PluginManager, helpers/plugman/BukgetPlugin, helpers/plugman/BukgetPluginList")
@Cmd(command = "plugin", description = "Full plugin management suite", aliases = "pm, pluginmanager", usage = "%c% help")
public class Command_plugin implements Command {
    private PluginManager pluginManager = Bukkit.getPluginManager();

    public boolean run(CommandSender sender, String[] args, String alias) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")){
            sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerHelp").replaceAll("<command>", alias));
            return true;
        }

        switch (args[0]){
            default:
                sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerHelp").replaceAll("<command>", alias));
                break;
            case "info" :
                if (args.length != 2){
                    return false;
                }

                String plugin = args[1];
                Plugin bukkitPlugin = pluginManager.getPlugin(plugin);

                if (bukkitPlugin  != null){
                    PluginDescriptionFile desc = bukkitPlugin.getDescription();
                    List<String> depends = new ArrayList<>();
                    depends.addAll(desc.getDepend());
                    depends.addAll(desc.getSoftDepend());

                    String name = desc.getName();
                    String enabled = WordUtils.capitalize(String.valueOf(bukkitPlugin.isEnabled()).toLowerCase());
                    String version = desc.getVersion();
                    String authors = Utils.join(desc.getAuthors(), ChatColor.AQUA + ", " + ChatColor.GOLD, ChatColor.AQUA + " & " + ChatColor.GOLD);
                    String description = desc.getDescription();
                    String website = desc.getWebsite() != null ? desc.getWebsite() : ChatColor.RED + Language.getTranslationForSender(sender, "unavailable");
                    String dependencies = Utils.join(depends, ChatColor.AQUA + ", " + ChatColor.GOLD, ChatColor.AQUA + " & " + ChatColor.GOLD);

                    sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerInfo", name, enabled, version, authors, description, website, dependencies));
                }
        }

        return true;
    }
}
