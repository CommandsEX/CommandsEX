package com.commandsex.helpers;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Useful class to make it easier to edit the classpath at runtime
 */
public class ClasspathHacker {

    /**
     * Parameters of the method used to add a URL to the System classes
     */
    private static final Class<?>[] parameters = new Class[]{URL.class};

    /**
     * Adds a file to the System ClassLoader
     * @param file The file to add to the ClassLoader
     * @return Was the the file successfully added to the system class-loader
     */
    public static boolean addFile(String file) {
        return addFile(new File(file));
    }

    /**
     * Adds a file to the System ClassLoader
     * @param file The file to add to the ClassLoader
     * @return Was the the file successfully added to the system class-loader
     */
    public static boolean addFile(File file) {
        try {
            return addURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a URL to the System ClassLoader
     * @param url The URL to add to the ClassLoader
     * @return Was the the url successfully added to the system class-loader
     */
    public static boolean addURL(URL url) {
        URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> systemClassLoaderClass = URLClassLoader.class;

        try {
            Method method = systemClassLoaderClass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(systemClassLoader, new Object[]{ url });
            return true;
        } catch (Throwable throwable){
            throwable.printStackTrace();
            LogHelper.logSevere("Couldn't add URL " + url.toString() + " to the System Classloader");
            return false;
        }
    }
}
