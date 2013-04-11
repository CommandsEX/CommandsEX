package com.commandsex.helpers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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
     * @throws IOException
     */
    public static void addFile(String file) throws IOException {
        addFile(new File(file));
    }

    /**
     * Adds a file to the System ClassLoader
     * @param file The file to add to the ClassLoader
     * @throws IOException
     */
    public static void addFile(File file) throws IOException {
        addURL(file.toURI().toURL());
    }

    /**
     * Adds a URL to the System ClassLoader
     * @param url The URL to add to the ClassLoader
     * @throws IOException
     */
    public static void addURL(URL url) throws IOException {
        URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> systemClassLoaderClass = URLClassLoader.class;

        try {
            Method method = systemClassLoaderClass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(systemClassLoader, new Object[]{ url });
        } catch (Throwable throwable){
            throwable.printStackTrace();
            throw new IOException("Couldn't add URL " + url.toString() + " to the System Classloader");
        }
    }
    
    /**
     * Adds a file to the System ClassLoader without throwing any exceptions
     * @param file The file to add to the ClassLoader
     * @returns <code>true</code> if the file was added successfully
     */
    public static boolean addFileGetResult(File file) {
        try {
            URL url = file.toURI().toURL();
            URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<?> systemClassLoaderClass = URLClassLoader.class;
            Method method = systemClassLoaderClass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(systemClassLoader, new Object[]{ url });
        } catch (Throwable throwable){
            return false;
        }
        
        return true;
    }

}
