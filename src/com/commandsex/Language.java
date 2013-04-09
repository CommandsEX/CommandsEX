package com.commandsex;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.commandsex.interfaces.EnableJob;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.commandsex.helpers.LogHelper;
import org.bukkit.plugin.PluginManager;

/**
 * All helper methods to do with languages and translations
 */
public class Language implements EnableJob {

    private static HashMap<String, Properties> langs = new HashMap<>();
    private static HashMap<String, String> userLangs = new HashMap<>();
    private static FileConfiguration config = CommandsEX.config;
    public static File langFolder = new File(CommandsEX.plugin.getDataFolder(), "langs");

    /**
     * Run when CommandsEX is enabled
     */
    public void onEnable(PluginManager pluginManager){
        if (!langFolder.exists()){
            langFolder.mkdir();
        }

        CodeSource src = getClass().getProtectionDomain().getCodeSource();
        if (src != null){
            URL jar = src.getLocation();
            try {
                ZipInputStream zipInputStream = new ZipInputStream(jar.openStream());

                try {
                    ZipEntry ze = null;

                    while ((ze = zipInputStream.getNextEntry()) != null){
                        String name = ze.getName();

                        try {
                            if (name.startsWith("lang/lang_") && name.endsWith(".properties")){
                                File file = new File(CommandsEX.plugin.getDataFolder(), "langs" + name.replaceFirst("lang", ""));

                                if (!file.exists()){
                                    InputStream inputStream = getClass().getResourceAsStream("/" + name);

                                    try {
                                        if (inputStream != null){
                                            FileOutputStream fileOutputStream = new FileOutputStream(file);

                                            try {
                                                ByteStreams.copy(inputStream, fileOutputStream);
                                            } finally {
                                                fileOutputStream.close();
                                            }
                                        }
                                    } finally {
                                        inputStream.close();
                                    }
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
                InputStream inputStream = getClass().getResourceAsStream("/lang/" + file.getName());

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
                            InputStream inputStream = getClass().getResourceAsStream("/lang/" + file.getName());

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
                            System.out.print("6");
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
        }

        // Load player languages into HashMap
        try {
            ResultSet resultSet = CommandsEX.database.query_res("SELECT * FROM %prefix%userlangs");

            while (resultSet.next()){
                String user = resultSet.getString("user");
                String playerLanguage = resultSet.getString("lang");

                if (!langs.containsKey(playerLanguage)){
                    LogHelper.logWarning("Invalid language for player " + user + " the language " + playerLanguage + " cannot be found\nResetting to default");
                    CommandsEX.database.query("UPDATE %prefix%userlangs SET lang = ? WHERE user = ?", getDefaultLanguage(), user);
                    userLangs.put(user, getDefaultLanguage());
                } else {
                    userLangs.put(user, playerLanguage);
                }
            }

            resultSet.close();
        } catch (SQLException e){
            e.printStackTrace();
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
        try {
            userLangs.put(user, language);

            Database database = CommandsEX.database;
            ResultSet resultSet = database.query_res("SELECT user FROM %prefix%userlangs WHERE user = ?", user);

            if (resultSet.next()){
                database.query("UPDATE %prefix%userlangs SET lang = ? WHERE user = ?", language, user);
            } else {
                database.query("INSERT INTO %prefix%userlangs VALUES ?, ?", user, language);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
