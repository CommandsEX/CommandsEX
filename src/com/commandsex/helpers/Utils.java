package com.commandsex.helpers;

import com.commandsex.Language;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static Pattern urlPattern = Pattern.compile("(^https?\\://)?[a-zA-Z0-9\\-\\.]+\\W+[a-zA-Z]{2,3}(/\\S*)?$", Pattern.CASE_INSENSITIVE);

    /**
     * Checks if a string is an integer
     * @param s The string to check
     * @return Is the string an integer
     */
    public static boolean isInt(String s){
        return s.matches("(-)?(\\d){1,10}(\\.(\\d){1,10})?");
    }

    /**
     * Separates a comma separated list into a String List
     * @param s The list to separate
     * @return The separated list
     */
    public static List<String> separateCommaList(String s){
        return Arrays.asList(s.split("\\s*,\\s*"));
    }

    /**
     * Joins objects together with the glueString
     * @param objects The objects to be joined
     * @param glueString The string to join them with
     * @param lastGlueString The string to use for the last join
     * @return The joined string
     */
    public static String join(List<?> objects, String glueString, String lastGlueString){
        return join(objects.toArray(), glueString, lastGlueString);
    }

    /**
     * Joins objects together with the glueString
     * @param objects The objects to be joined
     * @param glueString The string to join them with
     * @return The joined string
     */
    public static String join(List<?> objects, String glueString){
        return join(objects, glueString, glueString);
    }

    /**
     * Joins objects together with the glueString
     * @param array The array of objects to be joined
     * @param glueString The string to join them with
     * @param lastGlueString The string to use for the last join
     * @return The joined string
     */
    public static String join(Object[] array, String glueString, String lastGlueString){
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < array.length; i++){
            Object object = array[i];

            if (i != array.length - 1){
                if (i != 0){
                    stringBuilder.append(glueString);
                }

                stringBuilder.append(object);
            } else {
                if (i != 0){
                    stringBuilder.append(lastGlueString);
                }

                stringBuilder.append(object);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Joins objects together with the glueString
     * @param array The array of objects to be joined
     * @param glueString The string to join them with
     * @return The joined string
     */
    public static String join(Object[] array, String glueString){
        return join(array, glueString, glueString);
    }
    
    /**
     * Joins objects together with the glueString starting at startIndex
     * @param array the array of objects to be joined
     * @param glueString the string to glue them with
     * @param startIndex where to start
     * @return the joined string
     */
    public static String join(Object[] array, String glueString, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < array.length; i++)
            sb.append(array[i]).append(" ");
        return sb.toString().trim();
    }

    /**
     * Checks a string for invalid characters
     * @param string The string to check
     * @return Does the string contain invalid characters
     */
    public static boolean containsInvalidCharacters(String string){
        return !string.matches("^[A-Za-z0-9 _.-]+$");
    }

    /**
     * Gets a user friendly version of a String, e.g. SMOOTH_BRICK would become Smooth Brick
     * @param string The friendly name to get
     * @return The friendly version of the String
     */
    public static String getFriendlyName(String string) {
        return WordUtils.capitalize(string.toLowerCase().replaceAll("_", ""));
    }

    /**
     * Converts an <code>InputStream</code> to a String
     * @param stream the stream to convert
     * @return the string
     */
    public static String convertStreamToString(InputStream stream) {
        try {
            return new Scanner(stream).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

    /**
     * Downloads a file while also displaying the progress in the console
     * @param download The download of the saveLocation to download
     * @param saveLocation The location to save the file to
     * @return Did the download download successfully
     */
    public static boolean downloadWithProgress(URL download, File saveLocation){
        final Download downloadInstance = new Download(download, saveLocation);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (downloadInstance.getStatus() == Download.Status.DOWNLOADING){
                    LogHelper.logInfo(Language.getTranslationForSender(Bukkit.getConsoleSender(), "libraryDownloadingProgress", humanReadableByteCount(downloadInstance.getDownloadedSize(), true), downloadInstance.getProgress(), humanReadableByteCount(downloadInstance.getTotalSize(), true)));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return downloadInstance.getStatus() != Download.Status.FAILED;
    }
    
    /**
     * Converts bytes to a human readable form
     * @param bytes The amount of bytes
     * @param si Whether to use SI or binary
     * @return The size in human readable form
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Checks a string for a URL
     * @param s The string to check for a URL
     * @return Does the string contain a URL?
     */
    public static boolean containsUrl(String s){
       return getUrl(s) != null;
    }

    /**
     * Gets the first URL in a string
     * @param string The string to get the URL from
     * @return The url, null if not found
     */
    public static URL getUrl(String string){
        String[] parts = string.split(" ");

        for (String part : parts){
            Matcher matcher = urlPattern.matcher(part);

            while (matcher.find()){
                try {
                    return new URL(matcher.group());
                } catch (MalformedURLException e) {
                    continue;
                }
            }
        }

        return null;
    }
}
