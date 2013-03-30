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

    private CommandMap commandMap = null;

    public void onEnable(){
        plugin = this;
        logger = Bukkit.getLogger();
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();

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

        Reflections reflections = new Reflections("com.commandsex");

        // Get command and listener classes
        Set<Class<? extends com.commandsex.api.interfaces.Command>> commandClasses = reflections.getSubTypesOf(com.commandsex.api.interfaces.Command.class);
        Set<Class<? extends Listener>> listenerClasses = reflections.getSubTypesOf(Listener.class);

        // Get classes requiring jobs
        Set<Class<? extends EnableJob>> enableJobs = reflections.getSubTypesOf(EnableJob.class);
        Set<Class<? extends DisableJob>> disableJobs = reflections.getSubTypesOf(DisableJob.class);

        // register commands
        int commandsRegistered = 0;
        for (Class<?> clazz : commandClasses){
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
                commandsRegistered++;
            } else {
                LogHelper.logWarning("Error: class " + clazz.getName() + " does not have an Cmd annotation");
                LogHelper.logWarning("The command will not function due to this");
            }
        }

        // Register events
        for (Class<? extends Listener> clazz : listenerClasses){
            try {
                Listener listener = (Listener) clazz.newInstance();
                pluginManager.registerEvents(listener, this);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Run all enable jobs
        for (Class<? extends EnableJob> clazz : enableJobs){
            try {
                EnableJob enableJob = (EnableJob) clazz.newInstance();
                Jobs.addEnableJob(enableJob);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Queue disable jobs
        for (Class<? extends DisableJob> clazz : disableJobs){
            try {
                DisableJob disableJob = (DisableJob) clazz.newInstance();
                Jobs.addDisableJob(disableJob);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
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

        if (config.getBoolean("metricsEnabled")){
            try {
                Metrics metrics = new Metrics(this);
                metrics.start();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        Jobs.executeEnableJobs();
    }

    public void onDisable(){
        Jobs.executeDisableJobs();
        database.close();
    }
}
