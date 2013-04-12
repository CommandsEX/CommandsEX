package com.commandsex.helpers.plugman;

import java.net.URL;

public class BukgetPlugin {

    private BukgetPlugin() {

    }

    public static BukgetPlugin getPluginFromList(BukgetPluginList list, String name) {
        return new BukgetPlugin();
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
