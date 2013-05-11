package com.commandsex;

import com.commandsex.database.Database;
import com.commandsex.database.H2Database;
import com.commandsex.database.MySqlDatabase;
import com.commandsex.database.SqLiteDatabase;
import com.commandsex.interfaces.DisableJob;
import com.commandsex.interfaces.EnableJob;
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.commandsex.annotations.Cmd;
import com.commandsex.helpers.CommandForwarder;
import com.commandsex.helpers.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
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
        pluginManager = getServer().getPluginManager();

        if (!(Bukkit.getServer() instanceof CraftServer)){
            logger.log(Level.WARNING, "Unfortunately CommandsEX is not compatible with custom CraftBukkit builds");
            logger.log(Level.WARNING, "Please alert the developers of your CraftBukkit build and we may add support for it!");
            pluginManager.disablePlugin(this);
            return;
        }

        try {
            LogHelper.logInfo("Connecting to CommandsEX database...");

            switch (config.getString("database.type")){
                default :
                    LogHelper.logSevere("Invalid database type in config, disabling...");
                    pluginManager.disablePlugin(this);
                    break;
                case ("mysql") :
                    database = new MySqlDatabase(config.getString("database.name"),
                            config.getString("database.username"), config.getString("database.password"),
                            config.getString("database.host"), config.getString("database.port"),
                            config.getString("database.prefix"));
                            break;
                case ("sqlite") :
                    database = new SqLiteDatabase(new File(getDataFolder(), config.getString("database.name") + ".db").getAbsolutePath(),
                            config.getString("database.prefix"));
                    break;
                case ("h2") :
                    database = new H2Database(new File(getDataFolder(), config.getString("database.name") + ".db").getAbsolutePath(),
                            config.getString("database.prefix"));
                    break;
            }

            LogHelper.logInfo("Successfully connected to the CommandsEX database");
        } catch (Exception e){
            e.printStackTrace();
            LogHelper.logSevere("Error while connecting to CommandsEX database, disabling plugin...");
            pluginManager.disablePlugin(this);
        }

        // Initialize languages
        Language.init();

        Libraries.registerLibrary("reflections-0.9.9-RC1", "http://www.commandsex.com/downloads/dependencies/reflections-0.9.9-RC1.jar");
        Libraries.registerLibrary("javassist", "http://www.commandsex.com/downloads/dependencies/javassist.jar");
        Libraries.registerLibrary("metrics-R6", "http://www.commandsex.com/downloads/dependencies/metrics-R6.jar");
        Libraries.registerLibrary("jooq-3.0.0-RC2", "http://www.commandsex.com/downloads/dependencies/jooq-3.0.0-RC2.jar");

        try {
            metrics = new Metrics(this);
        } catch (IOException e) {
            e.printStackTrace();
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

        int commandsRegistered = 0;
        int eventsRegistered = 0;

        CommandForwarder commandForwarder = new CommandForwarder();

        // regster commands
        for (Class<? extends com.commandsex.interfaces.Command> clazz : reflections.getSubTypesOf(com.commandsex.interfaces.Command.class)){
            Annotation annotation = clazz.getAnnotation(Cmd.class);

            if (annotation != null){
                Cmd commandAnnotation = (Cmd) annotation;
                String cmdName = commandAnnotation.command();
                String perm = commandAnnotation.permission();
                String permDefault = commandAnnotation.permissionDefault();
                PermissionDefault permissionDefault = PermissionDefault.OP;

                // auto generate permission
                if (perm.equals("")){
                    perm = "cex." + cmdName;
                }

                // set permission default
                if (!permDefault.equals("")){
                    permissionDefault = PermissionDefault.getByName(permDefault);
                }

                // auto add permission
                Permission permission = new Permission(perm, "Allows access to /" + cmdName, permissionDefault);
                Bukkit.getPluginManager().addPermission(permission);

                // register aliases
                List<String> aliases = new ArrayList<>();

                // Add the command as an alias of itself, this is because each command is actually
                // commandsRegistered as /cex_<command>, this will allows people to use /<command>
                aliases.add(commandAnnotation.command());

                if (!commandAnnotation.aliases().equals("")){
                    aliases.addAll(Utils.separateCommaList(commandAnnotation.aliases()));
                }

                com.commandsex.Command hackCommand = new com.commandsex.Command("cex_" + cmdName, commandAnnotation.description(), commandAnnotation.usage(), aliases);
                commandMap.register("", hackCommand);
                hackCommand.setExecutor(commandForwarder);
                commandsRegistered++;
            } else {
                LogHelper.logDebug("Error: class " + clazz.getName() + " does not have an Cmd annotation");
                LogHelper.logDebug("The command will not function due to this");
            }
        }

        // register events
        for (Class<? extends Listener> clazz : reflections.getSubTypesOf(Listener.class)){
            try {
                Listener listener = clazz.newInstance();
                Bukkit.getPluginManager().registerEvents(listener, this);
                eventsRegistered++;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        // queue disable jobs
        for (Class<? extends DisableJob> clazz : reflections.getSubTypesOf(DisableJob.class)){
            try {
                DisableJob disableJob = clazz.newInstance();
                Jobs.addDisableJob(disableJob);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        // execute enable jobs
        for (Class<? extends EnableJob> clazz : reflections.getSubTypesOf(EnableJob.class)){
            try {
                EnableJob enableJob = clazz.newInstance();
                enableJob.onEnable(Bukkit.getPluginManager());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        config.set("lastVersion", Double.parseDouble(getDescription().getVersion()));
        saveConfig();

        LogHelper.logDebug("Successfully registered " + commandsRegistered + " commands and " + eventsRegistered + " events");

        if (config.getBoolean("metricsEnabled")){
            metrics.start();
        }
    }

    public void onDisable(){
        Jobs.executeDisableJobs();
        //database.close();
    }

    public void reload(){
        reloadConfig();
        LogHelper.logInfo("Reloaded CommandsEX");
    }
}
