package com.commandsex.helpers;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static boolean isInt(String s){
        return s.matches("(-)?(\\d){1,10}(\\.(\\d){1,10})?");
    }
    
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

}
