package com.commandsex;

import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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

        // Copy languages from JAR
        CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        if (codeSource != null){
            URL jar = codeSource.getLocation();
            try {
                ZipInputStream zipInputStream = new ZipInputStream(jar.openStream());

                try {
                    byte[] buffer = new byte[1024];
                    ZipEntry zipEntry;
                    while ((zipEntry = zipInputStream.getNextEntry()) != null){
                        String name = zipEntry.getName();

                       if (name.startsWith("lang") && name.endsWith(".properties")){
                           try {
                               File langFile = new File(langFolder, name.replaceFirst("lang/", ""));

                               // if lastVersion isn't equal to this one, reset lang files
                               if (CommandsEX.config.getString("lastVersion") != CommandsEX.plugin.getDescription().getVersion()){
                                   langFile.renameTo(new File(langFolder, langFile.getName() + ".cexbackup"));
                               }

                               if (!langFile.exists()){
                                   langFile.createNewFile();
                                   FileOutputStream fileOutputStream = new FileOutputStream(langFile);

                                   try {
                                       int len;
                                       while ((len = zipInputStream.read(buffer)) > 0){
                                           fileOutputStream.write(buffer, 0, len);
                                       }
                                   } finally {
                                       fileOutputStream.close();
                                   }
                               }
                           } finally {
                               zipInputStream.closeEntry();
                           }
                       }
                    }
                } finally {
                    zipInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load available languages
        for (String s : config.getStringList("availableLanguages")){
            if (s.length() > 5){
                LogHelper.logWarning("Language " + s + " is too long, this language file will not be available");
                continue;
            }

            Properties lang = new Properties();
            String langFileName = "lang_" + s + ".properties";
            File langFile = new File(langFolder, langFileName);
            if (!langFile.exists()){
                LogHelper.logWarning("Couldn't find language file for language " + s);
                continue;
            } else {
                try {
                    lang.load(new FileInputStream(langFile));
                } catch (FileNotFoundException e) {
                    // This should never be thrown as we check above whether the file exists
                    e.printStackTrace();
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                    LogHelper.logSevere("IO error when reading language file for language " + s);
                    continue;
                }
            }

            langs.put(s, lang);
            LogHelper.logDebug("Successfully loaded language " + s);
        }

        // load player languages into HashMap
        try {
            ResultSet resultSet = CommandsEX.database.query_res("SELECT * FROM %prefix%userlangs");

            while (resultSet.next()){
                String user = resultSet.getString("user");
                String language = resultSet.getString("lang");
                userLangs.put(user, language);
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
        return config.getStringList("availableLanguages");
    }

}
