package com.commandsex.commands;

import com.commandsex.CommandsEX;
import com.commandsex.Language;
import com.commandsex.annotations.Builder;
import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.Utils;
import com.commandsex.helpers.plugman.Bukget;
import com.commandsex.helpers.plugman.BukgetPlugin;
import com.commandsex.helpers.plugman.BukgetPluginList;
import com.commandsex.interfaces.Command;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Builder(name = "plugin", description = "Full plugin management suite", type = "COMMAND", depends = "helpers/plugman/PluginManager, helpers/plugman/BukgetPlugin, helpers/plugman/BukgetPluginList")
@Cmd(command = "plugin", description = "Full plugin management suite", aliases = "pm, pluginmanager", usage = "%c% help")
public class Command_plugin implements Command {
    private PluginManager pluginManager = Bukkit.getPluginManager();
    private HashMap<String, String> playerConfirmations = new HashMap<>();

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
                Plugin bukkitPlugin = com.commandsex.helpers.plugman.PluginManager.getPlugin(plugin);

                if (bukkitPlugin  != null){
                    PluginDescriptionFile desc = bukkitPlugin.getDescription();
                    List<String> depends = new ArrayList<>();
                    if (desc.getDepend() != null) depends.addAll(desc.getDepend());
                    if (desc.getSoftDepend() != null) depends.addAll(desc.getSoftDepend());

                    String name = desc.getName();
                    String enabled = WordUtils.capitalize(String.valueOf(bukkitPlugin.isEnabled()).toLowerCase());
                    String version = desc.getVersion();
                    String authors = Utils.join(desc.getAuthors(), ChatColor.AQUA + ", " + ChatColor.GOLD, ChatColor.AQUA + " & " + ChatColor.GOLD);
                    String description = desc.getDescription();
                    String website = desc.getWebsite() != null ? desc.getWebsite() : ChatColor.RED + Language.getTranslationForSender(sender, "unavailable");
                    String dependencies = (depends.size() > 0 ? Utils.join(depends, ChatColor.AQUA + ", " + ChatColor.GOLD, ChatColor.AQUA + " & " + ChatColor.GOLD) : ChatColor.RED + Language.getTranslationForSender(sender, "none"));

                    boolean bukGetInfoFound = false;
                    String bukkitDevName = null;
                    String bukkitDevPage = null;
                    String latestVersion = null;
                    String bukkitVersion = null;
                    String latestDownload = null;

                    String bukGetSearchTerm;
                    if (website != null && website.contains("dev.bukkit.org")){
                        bukGetSearchTerm = website.substring(website.lastIndexOf("/"));
                    } else {
                        bukGetSearchTerm = name.toLowerCase().replaceAll(" ", "-");
                    }

                    try {
                        BukgetPlugin bukgetPlugin = BukgetPlugin.getPluginFromSlug(bukGetSearchTerm, Bukget.Version.LATEST, Bukget.Field.VERSION, Bukget.Field.FILE_LINK, Bukget.Field.BUKKIT_VERSION, Bukget.Field.BUKKIT_DEV_PAGE, Bukget.Field.PLUGIN_NAME);

                        if (bukgetPlugin != null){
                            bukGetInfoFound = true;
                            bukkitDevName = bukgetPlugin.getFieldValue(Bukget.Field.PLUGIN_NAME);
                            bukkitDevPage = bukgetPlugin.getFieldValue(Bukget.Field.BUKKIT_DEV_PAGE);
                            latestVersion = bukgetPlugin.getFieldValue(Bukget.Field.VERSION);
                            bukkitVersion = bukgetPlugin.getFieldValue(Bukget.Field.BUKKIT_VERSION);
                            latestDownload = bukgetPlugin.getFieldValue(Bukget.Field.FILE_LINK);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerInfo", name, enabled, version, authors, description, website, dependencies));

                    if (bukGetInfoFound){
                        sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerBukkitDev", bukkitDevName, bukkitDevPage, latestVersion, bukkitVersion, latestDownload));
                    } else {
                        sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerBukkitDevNotFound", bukGetSearchTerm));
                    }
                }

                break;
            case "install" : {
                if (args.length != 2){
                    return false;
                }

                String sName = sender.getName();

                BukgetPluginList bukgetPluginList = null;
                try {
                    bukgetPluginList = BukgetPluginList.getFromQuery(Bukget.Field.PLUGIN_NAME, Bukget.SearchAction.LIKE, args[1], Bukget.Field.PLUGIN_NAME);
                } catch (IOException e) {
                    sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerBukgetError"));
                    return true;
                }

                TreeMap<Integer, String> idNameMap = bukgetPluginList.getIDNameMap();
                if (idNameMap.size() == 0){
                    sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerBukgetNoMatches", args[1]));
                    return true;
                }

                if (playerConfirmations.containsKey(sName) && playerConfirmations.get(sName).equalsIgnoreCase(args[1])){
                    try {
                        BukgetPlugin bukgetPlugin = BukgetPlugin.getPluginFromList(bukgetPluginList, 00, Bukget.Version.LATEST, Bukget.Field.PLUGIN_NAME, Bukget.Field.VERSION, Bukget.Field.DOWNLOAD_LINK);
                        String downloadUrl = bukgetPlugin.getFieldValue(Bukget.Field.DOWNLOAD_LINK);
                        File file = new File("plugins/" + downloadUrl.substring(downloadUrl.lastIndexOf("/")));

                        if (file.exists()){
                            sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerFileExists", file.getName()));
                            return true;
                        }

                        sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerDownloading", bukgetPlugin.getFieldValue(Bukget.Field.PLUGIN_NAME)));

                        if (Utils.downloadWithProgress(new URL(downloadUrl), file)){
                            try {
                                Plugin plugin1 = pluginManager.loadPlugin(file);
                                pluginManager.enablePlugin(plugin1);
                                sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerDownloaded", plugin1.getName()));
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                                sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerEnablingError", bukgetPlugin.getFieldValue(Bukget.Field.PLUGIN_NAME)));
                            }
                        } else {

                        }
                    } catch (IOException e) {
                        sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerBukgetError"));
                        return true;
                    }
                } else {
                    try {
                        BukgetPlugin bukgetPlugin = BukgetPlugin.getPluginFromList(bukgetPluginList, 0, Bukget.Version.LATEST, Bukget.Field.PLUGIN_NAME, Bukget.Field.AUTHORS, Bukget.Field.BUKKIT_DEV_PAGE, Bukget.Field.DESCRIPTION);
                        sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerConfirm", bukgetPlugin.getFieldValue(Bukget.Field.PLUGIN_NAME), bukgetPlugin.getFieldValue(Bukget.Field.DESCRIPTION), bukgetPlugin.getFieldValue(Bukget.Field.BUKKIT_DEV_PAGE), alias, args[1]));
                        playerConfirmations.put(sName, args[1]);
                    } catch (IOException e) {
                        sender.sendMessage(Language.getTranslationForSender(sender, "pluginManagerBukgetError"));
                        return true;
                    }
                }
            }
        }

        return true;
    }
}
