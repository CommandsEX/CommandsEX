package com.commandsex.helpers.plugman;

import java.util.Map;

public class BukgetPluginList {

    private BukgetPluginList() {

    }

    public static BukgetPluginList getListFromQuery(String searchTerm, Category category) {
        return new BukgetPluginList();
    }

    public Map<Integer, String> getPlugins() {
        return null;
    }

    public enum Category {
        ADMIN_TOOLS,
        DEVELOPER_TOOLS,
        FUN,
        MECHANICS,
        TELEPORTATION,
        WORLD_GENRATORS,
        ANTI_GRIEFING_TOOLS,
        ECONOMY,
        GENERAL,
        WEBSITE_ADMINISTRATION,
        CHAT_RELATED,
        FIXES,
        INFORMATIONAL,
        ROLE_PLAYING,
        WORLD_EDITING_AND_MANAGEMENT
    }

}
