package com.commandsex.helpers.plugman;

import java.io.IOException;
import java.net.URL;

public class BukgetPlugin {

    private BukgetPlugin() {}

    public static BukgetPlugin getPluginFromList(BukgetPluginList list, String name) throws IOException {
        return new BukgetPlugin();
    }

    public static BukgetPlugin getPluginBySlug(String slug) throws IOException {
         return null;
    }

    public static URL getDownloadLinkFromSlug(String slug) throws IOException {
        return null;
    }

    public URL download() {
        return null;
    }

    public URL devPage() {
        return null;
    }

    public int downloads() {
        return 0;
    }

    public String version() {
        return null;
    }

    public String bukkitVersion() {
        return null;
    }

    public String name() {
        return null;
    }

    public String description() {
        return null;
    }

    public String changelog() {
        return null;
    }

    public String[] authors() {
        return null;
    }

    public BukgetPluginList.Category[] categories() {
        return null;
    }

    public String[] dependenciesSoft() {
        return null;
    }

    public String[] dependenciesHard() {
        return null;
    }

    public Stage stage() {
        return null;
    }

    public enum Stage {
        PLANNING,
        ALPHA,
        BETA,
        MATURE,
        INACTIVE,
        ABANDONED,
        DELETED
    }

}
