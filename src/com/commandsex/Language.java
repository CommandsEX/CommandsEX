package com.commandsex;

import static org.jooq.impl.Factory.field;
import static org.jooq.impl.Factory.tableByName;
import static org.jooq.impl.Factory.value;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.commandsex.interfaces.EnableJob;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;

import com.commandsex.database.Database;
import com.commandsex.database.MySqlDatabase;
import com.commandsex.helpers.LogHelper;
import com.google.common.io.ByteStreams;

/**
 * All helper methods to do with languages and translations
 */
public class Language implements EnableJob {

    private static HashMap<String, Properties> langs = new HashMap<>();
    private static HashMap<String, String> userLangs = new HashMap<>();
    private static FileConfiguration config = CommandsEX.config;
    private  static Database database = CommandsEX.database;
    public static File langFolder = new File(CommandsEX.plugin.getDataFolder(), "langs");

    /**
     * Run when CommandsEX is enabled
     */
    public static void init(){
        database = CommandsEX.database;
        // create language database if it does not already exist
        try {
            database.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS " + database.getPrefix() + "userlangs (username varchar(50) NOT NULL, lang varchar(5) NOT NULL)" + (CommandsEX.database instanceof MySqlDatabase ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores per-user selected plugin language'" : ""));
        } catch (SQLException e) {
            e.printStackTrace();
            LogHelper.logSevere("Unable to connect to create the language table, disabling...");
            CommandsEX.pluginManager.disablePlugin(CommandsEX.plugin);
            return;
        }

        if (!langFolder.exists()){
            langFolder.mkdir();
        }

        CodeSource src = Language.class.getProtectionDomain().getCodeSource();
        if (src != null){
            URL jar = src.getLocation();
            try {
                ZipInputStream zipInputStream = new ZipInputStream(jar.openStream());

                try {
                    ZipEntry ze;
                    while ((ze = zipInputStream.getNextEntry()) != null){
                        String name = ze.getName();

                        try {
                            if (name.startsWith("lang/lang_") && name.endsWith(".properties")){
                                File file = new File(CommandsEX.plugin.getDataFolder(), "langs" + name.replaceFirst("lang", ""));
                                InputStream jarLangInputStream = Language.class.getResourceAsStream("/" + name);

                                try {
                                    if (!file.exists()){
                                        // Copy the file from the JAR to the CommandsEX data folder
                                        if (jarLangInputStream != null){
                                            FileOutputStream fileOutputStream = new FileOutputStream(file);

                                            try {
                                                ByteStreams.copy(jarLangInputStream, fileOutputStream);
                                            } finally {
                                                fileOutputStream.close();
                                            }
                                        }
                                    }
                                } finally {
                                    jarLangInputStream.close();
                                }
                            }
                        } finally {
                            zipInputStream.closeEntry();
                        }
                    }
                } finally {
                    zipInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File[] files = langFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String fileName = pathname.getName();
                return !pathname.isDirectory() && fileName.startsWith("lang_") && fileName.endsWith(".properties");
            }
        });

        // Iterate through all files, first if the last version is lower than the current version, all language files will be overwtitten if they are contained in the JAR
        // Secondly it will check all current language files and if one of them is missing a varibale, it will copy it in
        // Lastly it will load the language file
        for (File file : files){
            if (CommandsEX.config.getDouble("lastVersion") < Double.parseDouble(CommandsEX.plugin.getDescription().getVersion())){
                InputStream inputStream = Language.class.getResourceAsStream("/lang/" + file.getName());

                if (inputStream != null){
                    try {
                        File newFile = new File(file.getParentFile(), file.getName() + ".cex-upgrade-backup");
                        LogHelper.logWarning(file.getPath() + " has been overwritten due to a CommandsEX update\nBackup saved to " + newFile.getPath());
                        file.renameTo(newFile);

                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);

                            try {
                                ByteStreams.copy(inputStream, fileOutputStream);
                            } finally {
                                fileOutputStream.close();
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    } finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                Properties properties = new Properties();
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);

                    if (fileInputStream != null){
                        properties.load(fileInputStream);
                        boolean propertiesChanged = false;

                        try {
                            InputStream inputStream = Language.class.getResourceAsStream("/lang/" + file.getName());

                            if (inputStream != null){
                                Properties jarProperties = new Properties();
                                jarProperties.load(inputStream);

                                for (Object o : jarProperties.keySet()){
                                    if (!properties.containsKey(o)){
                                        propertiesChanged = true;
                                        properties.put(o, jarProperties.get(o));
                                    }
                                }
                            }
                        } finally {
                            fileInputStream.close();
                        }

                        // Only save if properties if it has actually been changed
                        if (propertiesChanged){
                            LogHelper.logInfo(file.getPath() + " had missing language entries, adding defaults");
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            try {
                                properties.store(fileOutputStream, " For a cleaner copy, copy the file from the lang folder in the CommandsEX JAR");
                            } finally {
                                fileOutputStream.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Actually load the language now
            String fName = file.getName();
            String languageName = fName.substring(5, fName.length() - 11);

            if (languageName.length() <= 5){
                Properties language = new Properties();
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);

                    try {
                        language.load(fileInputStream);
                        langs.put(languageName, language);
                        LogHelper.logDebug("Loaded language " + languageName);
                    } finally {
                        fileInputStream.close();
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                LogHelper.logSevere("The language name " + languageName + "is too long\nPlease make sure it is 5 characters or less");
            }
        }
    }

    /**
     * Load player languages after dependencies (such as JOOQ) have been enabled
     */
    public void onEnable(PluginManager pluginManager){
        // Load player languages into HashMap
        Result<Record> result = database.getExecutor().select().from(database.getPrefix() + "userlangs").fetch();

        for (Record record : result){
            String username = record.getValue("username").toString();
            String language = record.getValue("lang").toString();

            if (!langs.containsKey(language)){
                LogHelper.logWarning("Invalid language for player " + username + " the language " + language + " cannot be found\nResetting to default");
                database.getExecutor().update(tableByName("userlangs")).set(field("lang"), value(getDefaultLanguage())).where("user", username);
                userLangs.put(username, getDefaultLanguage());
            } else {
                userLangs.put(username, language);
            }
        }
    }

    /**
     * Gets a translation for a CommandSender, if console will return in default languages
     * @param sender The CommandSender to get the translation for
     * @param key The message to get
     * @param args Formatting arguments
     * @return The translated message
     */
    public static String getTranslationForSender(CommandSender sender, String key, Object...args){
        if (sender instanceof Player){
            return getTranslationForUser(sender.getName(), key, args);
        } else {
            return getTranslationForLanguage(getDefaultLanguage(), key, args);
        }
    }
    
    /**
     * A shortened version of <code>getTranslationForSender()</code> designed to be statically imported for easier use.
     * @param sender The CommandSender to get the translation for
     * @param key The message to get
     * @param args Formatting arguments
     * @return The translated message
     */
    public static String _(CommandSender sender, String key, Object...args) {
        return getTranslationForSender(sender, key, args);
    }

    /**
     * Gets a translation for a user (player)
     * @param user The user (player) to get the message for
     * @param key The message to get
     * @param args Formatting arguments
     * @return The translated message
     */
    public static String getTranslationForUser(String user, String key, Object...args){
        return getTranslationForLanguage(getUserLanguage(user), key, args);
    }

    /**
     * Gets a translation in a language
     * @param language The language to get the message in
     * @param key The message to get
     * @param args Formatting arguments
     * @return The translated message
     */
    public static String getTranslationForLanguage(String language, String key, Object...args){
        return String.format(ChatColor.translateAlternateColorCodes('&', (getLanguage(language) != null ? getLanguage(language).getProperty(key) : key)), args);
    }

    /**
     * Gets the user's (player's) language
     * Returns null if the player does not have a language set (possibly never joined server)
     *
     * @param user The user (player) to get the language for
     * @return The current language of the user
     */
    public static String getUserLanguage(String user){
        return (userLangs.get(user) != null ? userLangs.get(user) : getDefaultLanguage());
    }

    /**
     * Gets the server's default language
     * @return The server's default language
     */
    public static String getDefaultLanguage(){
        return config.getString("defaultLanguage");
    }

    /**
     * Gets the properties file for the specified language
     * @param lang The language to get
     * @return The properties file of the language
     */
    public static Properties getLanguage(String lang){
        return langs.get(lang);
    }

    /**
     * Sets a users language to be used throughout CommandsEX
     * @param user The user to set the language
     * @param language The language to set the users language to
     */
    public static void setUserLanguage(String user, String language){
        userLangs.put(user, language);

        Result<Record> resultQuery = database.getExecutor().select().from(database.getPrefix() + "userlangs").where("username", user).fetch();

        Table<Record> table = tableByName(database.getPrefix() + "userlangs");
        if (resultQuery.isNotEmpty()){
            database.getExecutor().update(table).set(field("username"), value(language)).where("username", user);
        } else {
            database.getExecutor().insertInto(table, field("username"), field("lang")).values(user, language).execute();
        }
    }

    /**
     * Checks if a language is available
     * @param language The language to check for availability
     * @return Is the language available
     */
    public static boolean isLanguageAvailable(String language){
        return langs.containsKey(language);
    }

    /**
     * Gets all available languages on the server
     * @return A list of all available languages
     */
    public static List<String> getAvailableLanguages(){
        List<String> list = new ArrayList<>();
        list.addAll(langs.keySet());
        return list;
    }

}
