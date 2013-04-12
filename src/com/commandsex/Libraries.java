package com.commandsex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.commandsex.helpers.Utils;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.commandsex.helpers.ClasspathHacker;
import com.commandsex.helpers.LogHelper;

/**
 * A helper class for managing Libraries
 * @author Kezz101
 */
public class Libraries {
    private static Set<String> libraries = new HashSet<>();
    
    /**
     * An enum containing a list of possible result when registering a library
     */
    public enum LoadingResult {
        /** If the library is already loaded **/
        LIBRARY_ALREADY_LOADED,
        
        /** If the supplied download URL throws a <code>MalformedURLExcpeiton</code> **/
        INVALID_DOWNLOAD_URL,
        
        /** If an <code>IOException</code> occurred whilst downloading the lib **/
        LIBRARY_DOWNLOAD_ERROR,
        
        /** If the library was successfully added to the classpath **/
        LIBRARY_SUCCESSFULLY_LOADED,
        
        /** If the library was not successfully added to the classpath **/
        CLASSPATH_ADDITION_ERROR
    }
    
    /**
     * Attempts to register a library
     * @param name the name of the library
     * @param download the download link of the library
     * @return the {@link LoadingResult} containg the result of the register
     */
    public static LoadingResult registerLibrary(String name, String download) {        
        if(libraries.contains(name.toUpperCase()))
            return LoadingResult.LIBRARY_ALREADY_LOADED;
        
        if(!getLibFolder().exists())
            getLibFolder().mkdirs();
        
        URL downloadLink;
        try {
            downloadLink = new URL(download);
        } catch(MalformedURLException e) {
            return LoadingResult.INVALID_DOWNLOAD_URL;
        }
        
        File downloadedLib = new File(getLibFolder(), name + ".jar");
        
        if(!downloadedLib.exists()){
            LogHelper.logInfo(Language.getTranslationForSender(Bukkit.getConsoleSender(), "libraryDownloading", name));
            boolean success = Utils.downloadWithProgress(downloadLink, downloadedLib);

            if (!success){
                return LoadingResult.LIBRARY_DOWNLOAD_ERROR;
            }

            LogHelper.logInfo(Language.getTranslationForSender(Bukkit.getConsoleSender(), "libraryDownloaded", name));
        }

        if(ClasspathHacker.addFile(downloadedLib)) {
            libraries.add(name.toUpperCase());
            return LoadingResult.LIBRARY_SUCCESSFULLY_LOADED;
        } else
            return LoadingResult.CLASSPATH_ADDITION_ERROR;
    }
    
    /**
     * Checks if a library is loaded in the classpath
     * @param name the name of the library
     * @return <code>true</code> if the library is useable
     */
    public static boolean checkForLibrary(String name) {
        return libraries.contains(name.toUpperCase());
    }
    
    /**
     * Checks if a library is loaded in the classpath displaying an error to the {@link CommandSender} if it does not
     * @param name the name of the library
     * @param sender who to send the error to
     * @return <code>true</code> if the library is useable
     */
    public static boolean checkForLibrary(String name, CommandSender sender) {
        if(libraries.contains(name.toUpperCase()))
            return true;
        else {
            sender.sendMessage(Language.getTranslationForSender(sender, "libraryNeeded", name));
            return false;
        }
    }
    
    /**
     * Returns the folder containg downloaded libraries
     * @return the file
     */
    public static File getLibFolder() {
        return new File(CommandsEX.plugin.getDataFolder(), "libs/");
    }

}
