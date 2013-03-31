package com.commandsex;

import com.commandsex.api.Jobs;
import com.commandsex.api.interfaces.DisableJob;
import com.commandsex.api.interfaces.EnableJob;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.commandsex.api.annotations.Cmd;
import com.commandsex.helpers.CommandForwarder;
import com.commandsex.helpers.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.commandsex.helpers.LogHelper;
import org.reflections.Reflections;

public class CommandsEX extends JavaPlugin {

    public static CommandsEX plugin;
    public static FileConfiguration config;
    public static Logger logger;
    public static Database database;
    public static PluginManager pluginManager;
    public static Metrics metrics;

    private CommandMap commandMap = null;

    public void onEnable(){
        plugin = this;
        logger = Bukkit.getLogger();
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
        try {
            metrics = new Metrics(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pluginManager = getServer().getPluginManager();

        if (!(Bukkit.getServer() instanceof CraftServer)){
            logger.log(Level.WARNING, "Unfortunately CommandsEX is not compatible with custom CraftBukkit builds");
            logger.log(Level.WARNING, "Please alert the developers of your CraftBukkit build and we may add support for it!");
            pluginManager.disablePlugin(this);
            return;
        }

        try {
            LogHelper.logInfo("Connecting to CommandsEX database...");
            Database.DatabaseType databaseType = Database.DatabaseType.fromString(config.getString("database.type"));

            switch (databaseType){
                case SQLITE :
                    database = new Database(config.getString("database.name"), config.getString("database.prefix"));
                    break;
                case MYSQL :
                    database = new Database(config.getString("database.name"), config.getString("database.username"),
                            config.getString("database.password"), config.getString("database.host"),
                            config.getString("database.port"), config.getString("database.prefix"));
                    break;
                default :
                    LogHelper.logSevere("Invalid database type - check your config, disabling plugin...");
                    pluginManager.disablePlugin(this);
                    return;
            }

            // create language database if it does not already exist
            database.query("CREATE TABLE IF NOT EXISTS %prefix%userlangs (username varchar(50) NOT NULL, lang varchar(5) NOT NULL)" + (database.getType() == Database.DatabaseType.MYSQL ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores per-user selected plugin language'" : ""));

            LogHelper.logInfo("Successfully connected to the CommandsEX database");
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logSevere("Error while connecting to CommandsEX database, disabling plugin...");
            pluginManager.disablePlugin(this);
        }

        // get the command map
        try {
            Field f = CraftServer.class.getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
        } catch (Throwable e){
            e.printStackTrace();
        }

        // create the CommandsEX directory if it has not been created already
        getDataFolder().mkdirs();
        File log = new File(getDataFolder(), "log.txt");

        // creates the log.txt file if it hasn't been created already
        try {
            log.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Reflections reflections = new Reflections("com.commandsex");

        List<Object> classes = new ArrayList<Object>();
        classes.addAll(reflections.getSubTypesOf(com.commandsex.api.interfaces.Command.class));
        classes.addAll(reflections.getSubTypesOf(Listener.class));
        classes.addAll(reflections.getSubTypesOf(EnableJob.class));
        classes.addAll(reflections.getSubTypesOf(DisableJob.class));

        int commandsRegistered = 0;
        int eventsRegistered = 0;
        for (Object object : classes){
            try {
                Class<?> clazz = (Class<?>) object;
                Object instance = clazz.newInstance();

                if (instance instanceof EnableJob){
                    EnableJob enableJob = (EnableJob) instance;
                    enableJob.onEnable(getServer().getPluginManager());
                    LogHelper.logDebug("Executed enable job for " + clazz.getName());
                }

                if (instance instanceof com.commandsex.api.interfaces.Command){
                    Annotation annotation = clazz.getAnnotation(Cmd.class);

                    if (annotation != null){
                        Cmd commandAnnotation = (Cmd) annotation;

                        List<String> aliases = new ArrayList<String>();

                        // Add the command as an alias of itself, this is because each command is actually
                        // commandsRegistered as /cex_<command>, this will allows people to use /<command>
                        aliases.add(commandAnnotation.command());

                        if (!commandAnnotation.aliases().equals("")){
                            aliases.addAll(Utils.separateCommaList(commandAnnotation.aliases()));
                        }

                        com.commandsex.Command hackCommand = new com.commandsex.Command("cex_" + commandAnnotation.command(), commandAnnotation.description(), "/<command> " + commandAnnotation.usage().trim(), aliases);
                        commandMap.register("", hackCommand);
                        hackCommand.setExecutor(new CommandForwarder());
                        LogHelper.logDebug("Registered command " + commandAnnotation.command() + " in " + clazz.getName());
                        commandsRegistered++;
                    } else {
                        LogHelper.logDebug("Error: class " + clazz.getName() + " does not have an Cmd annotation");
                        LogHelper.logDebug("The command will not function due to this");
                    }
                }

                if (instance instanceof Listener){
                    Listener listener = (Listener) instance;
                    Bukkit.getPluginManager().registerEvents(listener, this);
                    LogHelper.logDebug("Registered events for " + clazz.getName());
                    eventsRegistered++;
                }

                if (instance instanceof DisableJob){
                    DisableJob disableJob = (DisableJob) instance;
                    Jobs.addDisableJob(disableJob);
                    LogHelper.logDebug("Registered disable job for " + clazz.getName());
                }

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        LogHelper.logDebug("Successfully registered " + commandsRegistered + " commands and " + eventsRegistered + " events");

        if (config.getBoolean("metricsEnabled")){
            metrics.start();
        }
    }

    public void onDisable(){
        Jobs.executeDisableJobs();
        database.close();
    }

    public void reload(){
        reloadConfig();
        LogHelper.logInfo("Reloaded CommandsEX");
    }
}
