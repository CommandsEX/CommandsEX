package com.commandsex.helpers.plugman;

import com.commandsex.CommandsEX;
import com.commandsex.Libraries;
import com.commandsex.helpers.LogHelper;
import com.commandsex.helpers.Utils;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import static com.commandsex.Language._;

public class PluginManager implements EnableJob {

    public void onEnable(org.bukkit.plugin.PluginManager pluginManager) {
        Libraries.registerLibrary("gson-2.2.2", "http://www.commandsex.com/downloads/dependencies/gson-2.2.2.jar");

        Bukkit.getScheduler().runTaskAsynchronously(CommandsEX.plugin, new Runnable() {

            @Override
            public void run() {
                List<BukgetPlugin> needsUpdating = needsUpdating();

                if(needsUpdating.isEmpty())
                    LogHelper.logDebug(_(Bukkit.getConsoleSender(), "pluginManagerAutoUpdateNoUpdates"));
                else
                    LogHelper.logInfo(_(Bukkit.getConsoleSender(), "pluginManagerAutoUpdateUpdates", Utils.join(convertPluginToName(needsUpdating), ", ", " & ")));
            }

        });
    }

    /**
     * Searches for a plugin by a name, this is different to Bukkit's default getPlugin() method
     * @param name The name of the plugin to search for
     * @return The plugin, null if not found
     */
    public static Plugin getPlugin(String name){
        List<Plugin> matches = new ArrayList<>();

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()){
            String pluginName = plugin.getName().replaceAll(" ", "");

            if (pluginName.equalsIgnoreCase(name)){
                return plugin;
            } else if (pluginName.contains(name)){
                matches.add(plugin);
            }
        }

        return matches.size() > 0 ? matches.get(0) : null;
    }
    
    public static List<BukgetPlugin> needsUpdating() {
        List<BukgetPlugin> needsUpdating = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            String slug;
            if(plugin.getDescription().getWebsite().toLowerCase().contains("dev.bukkit.org/server-mods/"))
                slug = plugin.getDescription().getWebsite().toLowerCase().split("server-mods/")[1];
            else
                slug = plugin.getName().toLowerCase().replace(" ", "_");

            BukgetPluginList bpl;
            try {
                bpl = BukgetPluginList.getFromQuery(Bukget.Field.PLUGIN_NAME, Bukget.SearchAction.EQUALS, slug, Bukget.Field.POPULARITY_MONTHLY);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            if(bpl == null) {
                LogHelper.logDebug(_(Bukkit.getConsoleSender(), "pluginManagerAutoUpdateSlugError", plugin.getName()));
                break;
            }

            BukgetPlugin bukgetPlugin = null;

            for(int i = 0; i < bpl.getIDNameMap().size() - 1; i++) {
                if(bpl.getIDNameMap().get(i).equalsIgnoreCase(plugin.getName())) {
                    try {
                        bukgetPlugin = BukgetPlugin.getPluginFromList(bpl, i, Bukget.Version.LATEST, Bukget.Field.DOWNLOAD_LINK, Bukget.Field.VERSION);
                    } catch (IOException e) { break; }
                }
            }

            if(bukgetPlugin == null) {
                LogHelper.logDebug(_(Bukkit.getConsoleSender(), "pluginManagerAutoUpdateNotFound", plugin.getName()));
                break;
            }

            float newer, current;

            try {
                newer = Float.parseFloat(bukgetPlugin.getFieldValue(Bukget.Field.VERSION));
                current = Float.parseFloat(plugin.getDescription().getVersion());
            } catch(NumberFormatException e) {
                LogHelper.logDebug(_(Bukkit.getConsoleSender(), "pluginManagerAutoUpdateFormatError", plugin.getName()));
                break;
            }

            if(newer > current) {
                needsUpdating.add(bukgetPlugin);
            }
        }
        return needsUpdating;
    }

    public static void updateAll(CommandSender sender, List<BukgetPlugin> plugins) {
        sender.sendMessage(_(sender, "pluginManagerAutoUpdateStarting"));
        Iterator<BukgetPlugin> it = plugins.iterator();
        List<Plugin> bPlugins = new ArrayList<>();
        while(it.hasNext()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(it.next().getFieldValue(Bukget.Field.PLUGIN_NAME));
            if(plugin == null)
                break;
            bPlugins.add(plugin);
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        sender.sendMessage(_(sender, "pluginManagerAutoUpdateDeleting"));
        Iterator<Plugin> it2 = bPlugins.iterator();
        while(it2.hasNext())
            try {
                Class clazz = it2.next().getClass();
                Method method = clazz.getDeclaredMethod("getFile", File.class);
                method.setAccessible(true);
                File file = (File) method.invoke(clazz, null);
                file.renameTo(new File(CommandsEX.plugin.getDataFolder().getParent(), file.getName() + ".backup"));
                it2.remove();
            } catch (Exception e) {
                break;
            }

        sender.sendMessage(_(Bukkit.getConsoleSender(), ""));
        it = plugins.iterator();
    }

    public static List<String> convertPluginToName(List<BukgetPlugin> plugins) {
        Iterator<BukgetPlugin> it = plugins.iterator();
        List<String> returnMe = new ArrayList<>();
        while(it.hasNext()) {
            returnMe.add(it.next().getFieldValue(Bukget.Field.PLUGIN_NAME));
            it.remove();
        }
        return returnMe;
    }

    public List<BukgetPlugin> getMatchesFor(String plugin){
        return null;
    }
}
