package com.commandsex.helpers;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Utils {

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
                if (i != 0) stringBuilder.append(glueString);
                stringBuilder.append(object);
            } else {
                stringBuilder.append(lastGlueString);
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
     * Checks a string for invalid characters
     * @param string The string to check
     * @return Does the string contain invalid characters
     */
    public static boolean containsInvalidCharacters(String string){
        return !string.matches("^[A-Za-z0-9 _.-]+$");
    }

    /**
     * Gets a user friendly version of a Material
     * @param material the material to get
     * @return the friendly version of the material
     */
    public static String getFriendlyMaterialName(Material material) {
        return WordUtils.capitalize(material.name().replaceAll("_", ""));
    }

    /**
     * Gets a user friendly version of an Item
     * @param item the item to get
     * @return the friendly version of the Item
     */
    public static String getFriendlyItemName(ItemStack item) {
        return getFriendlyMaterialName(item.getType());
    }

    public static String convertStreamToString(InputStream stream) {
        try {
            return new java.util.Scanner(stream).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

}
